(defn isEqualLength? [data]
  (let [x (count (first data))]
  (every? (fn [cur] (== x (count cur))) data)))

(defn isScalars? [data] (every? number? data))
(defn setEmpty [data] (if (not (number? data)) (mapv setEmpty data) []))

(defn getShape [data]
  (cond (vector? data)
    (cond (vector? (first data))
      (cons (count data) (getShape (first data)))
      :else [(count data) 1])
    :else [1]))

(defn makeSame [params data]
  (cond (not (empty? params))
    (vec (repeat (first params) (makeSame (rest params) data)))
    :else data))

(defn broadcastOne [params data]
  (makeSame (drop-last (count (getShape data)) params) data))

(defn broadcastAll [data]
  (let [need (getShape (apply max-key (fn [cur] (count (getShape cur))) data))]
    (mapv (partial broadcastOne need) data)))

(defn check [cur]
  (or (empty? cur) (and (apply = cur) (every? true? (mapv check cur)))))

(defn abstractOperation [f & data]
  {:pre [(and (check (setEmpty data)) (not (empty? data)))]}
  (cond (number? (first data))
    (apply f data)
    :else (apply mapv (partial abstractOperation f) data)))

(defn create [dt, try]
  (fn [f]
    (fn [& data]
      {:pre  [try data]}
      (apply (partial abstractOperation f) (dt data)))))

(defn notEmpty? [data]
  (or (number? data) (and (every? notEmpty? data) (not (empty? data)))))

(defn isVectors? [data]
  (and (every? #(and (vector? %) (isScalars? %)) data)
       (isEqualLength? data)))
(defn isMatrices? [data]
  (every? #(and (vector? %) (isVectors? %)) data))
(defn isTensors? [data] (every? notEmpty? data))

(def Vector (create identity isVectors?))
(def Matrix (create identity isMatrices?))
(def Tensor (create broadcastAll isTensors?))

(def v+ (Vector +))
(def v- (Vector -))
(def v* (Vector *))
(def vd (Vector /))

(def m+ (Matrix v+))
(def m- (Matrix v-))
(def m* (Matrix v*))
(def md (Matrix vd))

(def hb+ (Tensor +))
(def hb- (Tensor -))
(def hb* (Tensor *))
(def hbd (Tensor /))

(defn scalar [& data]
  {:pre  [(isVectors? data)]}
  (apply + (apply v* data)))

(defn v*s [data & s]
  {:pre  [(and (isVectors? (vector data)) (isScalars? s))]}
  (let [x (apply * s)]
  (mapv (fn [current] (* current x)) data)))

(defn help [f x s y] (- (* (nth x f) (nth y s)) (* (nth y f) (nth x s))))
(defn vect3x3 [x y] (vector (help 1 x 2 y) (help 2 x 0 y) (help 0 x 1 y)))
(defn m*s [data & s] (mapv (fn [current] (apply v*s current s)) data))
(defn m*v [data & v] (mapv (fn [current] (apply scalar current v)) data))
(defn transpose [matrix] (apply mapv vector matrix))

(defn m*m [& data]
  {:pre  [(isMatrices? data)]}
  (reduce (fn [x y] (mapv (fn [current] (m*v (transpose y) current)) x)) data))

(defn vect [& data]
  {:pre  [(isVectors? data)]}
  (reduce vect3x3 data))
