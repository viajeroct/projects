"use strict"

const Const = function (num) {
    this.num = num;
}
const Variable = function (name) {
    this.name = name
}
const Operation = function (args) {
    this.args = args
}
const variables = ["x", "y", "z"]
const ZERO = new Const(0), ONE = new Const(1), M_ONE = new Const(-1)

const funcInterface = function (heir, evaluate, str, diff, pref, post) {
    heir.prototype.evaluate = evaluate;
    heir.prototype.toString = str;
    heir.prototype.diff = diff;
    heir.prototype.prefix = pref;
    heir.prototype.postfix = post;
}

funcInterface(Const, function () {
    return this.num
}, function () {
    return this.num.toString()
}, () => ZERO, function () {
    return this.num.toString()
}, function () {
    return this.num.toString()
})

funcInterface(Variable, function (...args) {
    return args[variables.indexOf(this.name)]
}, function () {
    return this.name
}, function (val) {
    if (val === this.name) return ONE
    return ZERO
}, function () {
    return this.name
}, function () {
    return this.name
})

funcInterface(Operation, function (...args) {
    return this.f(...this.args.map(cur => cur.evaluate(...args)))
}, function () {
    return this.args.join(" ") + " " + this.name
}, function (val) {
    return this.df(val, ...this.args, ...this.args.map(it => it.diff(val)))
}, function () {
    return "(" + this.name + " " + this.args.map(cur => cur.prefix()).join(" ") + ")"
}, function () {
    return "(" + this.args.map(cur => cur.postfix()).join(" ") + " " + this.name + ")"
})

const mp = {}
variables.forEach(value => mp[value] = new Variable(value))
const create = function (f, name, df) {
    const res = function (...args) {
        Operation.call(this, [...args])
    }
    res.arity = f.length
    res.prototype = Object.create(Operation.prototype)
    res.prototype.name = name
    res.prototype.f = f
    res.prototype.df = df
    mp[name] = res
    return res
}
const Add = create((x, y) => x + y, "+", (val, x, y, dx, dy) => new Add(dx, dy))
const Subtract = create((x, y) => x - y, "-", (val, x, y, dx, dy) => new Subtract(dx, dy))
const Multiply = create((x, y) => x * y, "*", (val, x, y, dx, dy) => new Add(new Multiply(dx, y), new Multiply(x, dy)))
const Divide = create((x, y) => x / y, "/", (val, x, y, dx, dy) => new Divide(new Subtract(new Multiply(dx, y), new Multiply(x, dy)), new Multiply(y, y)))
const Negate = create(x => -x, "negate", (val, x, dx) => new Multiply(M_ONE, dx))
const Exp = create(x => Math.exp(x), "exp", (val, x, dx) => new Multiply(new Exp(x), dx))
const Gauss = create((a, b, c, x) => a * Math.exp(-((x - b) * (x - b)) / (2 * c * c)), "gauss", (val, a, b, c, x) => new Multiply(a, new Exp(new Negate(new Divide(new Multiply(new Subtract(x, b), new Subtract(x, b)), new Multiply(new Const(2), new Multiply(c, c)))))).diff(val))
const foldLeft = function (op, map, data) {
    let ans = new map(data[0])
    for (let i = 1; i < Math.floor(data.length / 2); i++) {
        ans = new op(ans, new map(data[i]))
    }
    return ans
}
const Sumexp = create((...args) => args.reduce((sum, current) => sum + Math.exp(current), 0), "sumexp", (val, ...args) => {
    return foldLeft(Add, Exp, args).diff(val)
})
const Softmax = create((...args) => Math.exp(args[0]) / args.reduce((sum, current) => sum + Math.exp(current), 0), "softmax", (val, ...args) => {
    return new Divide(new Exp(args[0]), foldLeft(Add, Exp, args)).diff(val)
})

const launch = (parsed, f) => f.arity === undefined ? f : (new f(...parsed.splice(parsed.length - f.arity, f.arity)))
const parse = expr => {
    const tmp = []
    expr.trim().split(/  */).forEach(cur => {
        tmp.push(cur in mp ? launch(tmp, mp[cur]) : new Const(Number.parseInt(cur)))
    })
    return tmp.pop()
}

// function ExceptionCreator(name) {
//     this.prototype = Object.create(Error.prototype)
//     this.prototype.name = name
//     this.prototype.constructor = this
//     return function (msg) {
//         this.msg = msg
//     }
// }
//
// let ParserException = new ExceptionCreator("ParserException")
// let NonExisting = new ExceptionCreator("NonExisting")
// let WrongParentheses = new ExceptionCreator("WrongParentheses")

const Parser = function (stream, mode) {
    this.pos = mode ? 0 : stream.length - 1
    this.balance = 0
    this.stream = stream
    this.dir = mode ? +1 : -1
    this.mode = mode
}
Parser.prototype.mainParser = function () {
    if (this.stream.length === 0) {
        throw 1 // new ParserException("Empty file.")
    }
    if (this.stream[this.pos] === (this.mode ? '(' : ')')) {
        this.pos += this.dir
        this.balance++
        const it = mp[this.stream[this.pos]]
        this.pos += this.dir
        if (it === undefined) {
            throw 1 // new NonExisting("Non existing operation.")
        }
        let args = []
        for (let i = 0; i < it.arity || this.stream[this.pos] !== (this.mode ? ')' : '('); i++) {
            args.push(this.mainParser())
        }
        if (!this.mode) {
            args = args.reverse()
        }
        if (args.length !== it.arity && it.arity !== 0) {
            throw 1 // new ParserException("Wrong number of arguments, expected: " + (it.arity === undefined ? 0 : it.arity))
        }
        if (this.stream[this.pos] === (this.mode ? ')' : '(')) {
            this.balance--
        } else {
            throw 1 // new WrongParentheses("Expected closing ).")
        }
        this.pos += this.dir
        return new it(...args)
    }
    const token = this.stream[this.pos]
    this.pos += this.dir
    if (Number.parseInt(token).toString() === token) {
        return new Const(Number.parseInt(token))
    } else if (variables.indexOf(token) >= 0) {
        return mp[token]
    } else {
        // const err = (token === undefined ? "missing )(" : token)
        throw 1 // new ParserException("Wrong variable or wrong number -->" + err)
    }
}

const parseAny = type => expr => {
    let stream = expr.split(/([() ])/).filter(it => it.trim().length >= 1);
    const parser = new Parser(stream, type)
    let res = parser.mainParser()
    if (type && (parser.pos < stream.length) || !type && (parser.pos >= 0)) {
        throw 1 // new ParserException("Expected end, but found " + stream[parser.pos])
    }
    if (parser.balance !== 0) {
        throw 1 // new WrongParentheses("Wrong outer balance: " + parser.balance)
    }
    return res
}

const parsePrefix = parseAny(true)
const parsePostfix = parseAny(false)
