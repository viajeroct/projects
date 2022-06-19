; HOMEWORK 10
(defn abstractOperation [f]
  (fn [& data]
    (fn [values]
      (apply f (mapv (fn [cur] (cur values)) data)))))

(defn variable [name] (fn [values] (values name)))

(defn my-div
  ([value] (/ 1.0 value))
  ([arg & rest] (reduce #(/ (double %1) (double %2)) arg rest)))

(def constant constantly)
(def add (abstractOperation +))
(def subtract (abstractOperation -))
(def multiply (abstractOperation *))
(def negate subtract)
(def divide (abstractOperation my-div))

(defn exp[x] (Math/exp x))
(defn sumexp-impl [& values] (apply + (mapv exp values)))
(defn softmax-impl [& values]
  (my-div (exp (first values)) (apply sumexp-impl values)))

(def sumexp
  (abstractOperation
   (fn [& values]
     (apply sumexp-impl values))))
(def softmax
  (abstractOperation
   (fn [& values]
     (my-div (exp (first values)) (apply sumexp-impl values)))))

(def ops
  {'+       add
   '-       subtract
   '*       multiply
   '/       divide
   'negate  negate
   'sumexp  sumexp
   'softmax softmax})

(defn parser[tokens, num-op, var-op]
  (comp
   (fn parse [expr]
     (cond
       (list? expr)        (apply (tokens (first expr)) (mapv parse (rest expr)))
       (number? expr)      (num-op expr)
       (symbol? expr)      (var-op (name expr))))
   read-string))

(def parseFunction (parser ops constant variable))

; HOMEWORK 11
(load-file "proto.clj")

(def toString (method :toString))
(def evaluate (method :evaluate))
(def diff (method :diff))
(def args (field :args))
(def inf (method :inf))

(declare ZERO)

(def ConstantProto
  (let [x (field :val)]
    {:toString #(format "%.1f" (double (x %)))
     :evaluate (fn [this _] (x this))
     :diff     (fn [_ _] ZERO)
     :inf      toString}))

(def Constant (constructor #(assoc %1 :val %2) ConstantProto))

(def ZERO (Constant 0))
(def ONE (Constant 1))

(def VariableProto
  (let [name (field :val)]
    {:toString #(name %)
     :evaluate (fn [this cur] (cur (clojure.string/lower-case (first (name this)))))
     :diff     (fn [this cur] (if (= (name this) cur) ONE ZERO))
     :inf      toString}))

(def Variable (constructor #(assoc %1 :val %2) VariableProto))

(def getAt (fn [this index] ((args this) index)))
(def diffAll (fn [this var] (map #(diff % var) (args this))))

(def AbstractOperation
  (let [args (field :args)
        op   (field :op)
        f    (field :f)
        df   (method :df)]
    {:toString #(str "(" (op %) " " (clojure.string/join " " (mapv toString (args %))) ")")
     :evaluate (fn [this vars] (apply (f this) (mapv #(evaluate % vars) (args this))))
     :diff     (fn [this var] (df this (diffAll this var)))
     :inf      (fn [this]
                 (if (= (count (args this)) 1)
                   (str (op this) "(" (apply inf (args this)) ")")
                   (str "(" (clojure.string/join (str " " (op this) " ") (mapv inf (args this))) ")")))}))

(defn create-proto [op f df]
  {:prototype AbstractOperation
   :op        op
   :f         f
   :df        df})

(defn create [op f df]
  (fn [& args]
    {:prototype (create-proto op f df)
     :args      (vec args)}))

(def Add (create '+ + #(apply Add %2)))
(def Subtract (create '- - #(apply Subtract %2)))

(declare Multiply)

(defn mul-diff [this dt]
  (let [x (args this)]
    (apply Add
           (map-indexed
            #(apply Multiply (conj (into (subvec x 0 %1) (subvec x (inc %1))) %2)) dt))))

(def Multiply (create '* * mul-diff))
(def Negate (create 'negate - #(apply Negate %2)))

(defn ll [x] (java.lang.Double/doubleToLongBits x))
(defn dd [x] (java.lang.Double/longBitsToDouble x))

(def BitAnd (create '& (fn [x y] (dd (bit-and (ll x) (ll y)))) nil))
(def BitOr (create '| (fn [x y] (dd (bit-or (ll x) (ll y)))) nil))
(def BitXor (create (symbol "^") (fn [x y] (dd (bit-xor (ll x) (ll y)))) nil))

(def BitImpl (create '=> (fn [x y] (dd (bit-or (bit-not (ll x)) (ll y)))) nil))
(def BitIff (create '<=> (fn [x y] (dd (bit-not (bit-xor (ll x) (ll y))))) nil))

(declare Exp, Divide)

(defn exp-diff [this dt] (Multiply (Exp (first (args this))) (first dt)))

(def Exp (create 'exp exp exp-diff))

(defn div-diff [this dt]
  (let [tail (apply Multiply (rest (args this)))
        top  (getAt this 0)]
    (if (= (count (args this)) 1)
      (Negate (Divide (first dt) (Multiply top top)))
      (Divide
       (Subtract (Multiply tail (first dt)) (Multiply top (mul-diff tail (rest dt))))
       (Multiply tail tail)))))

(defn sumexp-diff [this dt]
  (apply Add (map-indexed (fn [i obj] (Multiply (Exp obj) (nth dt i))) (args this))))

(def Sumexp (create 'sumexp sumexp-impl sumexp-diff))

(defn softmax-diff [this dt]
  (let [bot (apply Sumexp (args this))]
    (Divide
     (Subtract
      (Multiply (Multiply (Exp (first (args this))) (first dt)) bot)
      (Multiply (Exp (first (args this))) (sumexp-diff bot dt)))
     (Multiply bot bot))))

(def Softmax (create 'softmax softmax-impl softmax-diff))

(def Divide (create '/ my-div div-diff))
(def Operations
  {'+           Add
   '-           Subtract
   '*           Multiply
   '/           Divide
   'negate      Negate
   'sumexp      Sumexp
   'softmax     Softmax,
   '&           BitAnd,
   '|           BitOr,
   (symbol "^") BitXor,
   '=>          BitImpl,
   '<=>         BitIff})

(def parseObject (parser Operations Constant Variable))

; HOMEWORK 12
(load-file "parser.clj")

; START OF AUXILIARY FUNCTIONS
(def *digit (+str (+plus (+char "0123456789"))))
(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))
(def *all-chars (mapv char (range 32 128)))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))
(def *number
  (+seqf (comp Constant read-string str) (+opt (+char "-")) *digit (+opt (+char ".")) *digit))
(def *variable (+map Variable (+str (+plus *letter))))

(defn +string [dt] (+str (apply +seq (mapv (comp +char str) (char-array dt)))))

; START OF CODE
(defn **star [symbols]
  (+map (comp symbol str) (apply +or (map +string symbols))))

(declare pr1)

(def cork
  (delay
    (+seqn 0 *ws
           (+or (+seqn 1 (+char "(") pr1 (+char ")"))
                (+map #(Negate (second %)) (+seq (**star ["negate"]) cork))
                *number *variable)
           *ws)))

(defn make-assoc [left-assoc]
  (fn [data]
    (let [args (if left-assoc data (reverse data))]
      (reduce
       (fn [arg arg-op]
         (if left-assoc
           ((Operations (first arg-op)) arg (second arg-op))
           ((Operations (first arg-op)) (second arg-op) arg)))
       (first args) (partition 2 (rest args))))))

(defn create-pr [next symbols assoc]
  (+map (make-assoc assoc)
        (+seqf cons next
               (+map (partial apply concat)
                     (+star (+seq (**star symbols) next))))))

(def pr7 (create-pr cork ["*" "/"] true))
(def pr6 (create-pr pr7 ["+" "-"] true))
(def pr5 (create-pr pr6 ["&"] true))
(def pr4 (create-pr pr5 ["|"] true))
(def pr3 (create-pr pr4 ["^"] true))
(def pr2 (create-pr pr3 ["=>"] false))
(def pr1 (create-pr pr2 ["<=>"] true))

(def parseObjectInfix (+parser pr1))
(def toStringInfix inf)
