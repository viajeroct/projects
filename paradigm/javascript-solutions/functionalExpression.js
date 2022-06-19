"use strict"
const variables = ["x", "y", "z"]
const variable = value => (...args) => args[variables.indexOf(value)]
const cnst = value => () => value
const pi = cnst(Math.PI)
const e = cnst(Math.E)
const mp = {"pi": pi, "e": e}
variables.forEach(value => mp[value] = variable(value))
const operation = (name, f) => {
    let res = (...ops) => (...args) => f(...ops.map(op => op(...args)))
    mp[name] = res
    res.arity = f.length
    return res
}
const add = operation("+", (x, y) => x + y)
const subtract = operation("-", (x, y) => x - y)
const multiply = operation("*", (x, y) => x * y)
const divide = operation("/", (x, y) => x / y)
const negate = operation("negate", x => -x)
const avg3 = operation("avg3", (a, b, c) => (a + b + c) / 3)
const med5 = operation("med5", (a, b, c, d, e) => [a, b, c, d, e].sort((a, b) => a - b)[2])
const launch = (parsed, f) => f.arity === undefined ? f : f(...parsed.splice(parsed.length - f.arity, f.arity))
const parse = expr => (...args) => {
    let tmp = []
    expr.trim().split(/  */).forEach(cur => {
        tmp.push(cur in mp ? launch(tmp, mp[cur]) : cnst(Number.parseInt(cur)))
    })
    return tmp.pop()(...args)
}
