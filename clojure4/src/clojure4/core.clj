(ns clojure4.core)


(defn args [expr]
  (rest expr))



(defn variable [name]
  {:pre [(keyword? name)]}
  (list ::var name))

(defn variable? [expr]
  {:pre [(not (keyword? expr))]}
  (= (first expr) ::var))

(assert (variable? (variable :a)))


(defn variable-name [v]
  {:pre [(not (keyword? v))]}
  (second v))

(defn same-variables? [v1 v2]
  (and
    (variable? v1)
    (variable? v2)
    (= (variable-name v1)
       (variable-name v2))))


(defn disjunction? [expr]
  {:pre [(not (keyword? expr))]}
  (= ::or (first expr)))

(defn disjunction [expr & rest]
  "Boolean or(|) "
  (println expr rest)
  ;(println (type expr) (type rest))

  (cond
    ;(and (empty? rest)) (if (disjunction? expr) expr (cons ::or (cons expr rest)) )
    ;
    ;(and (disjunction? expr) (not (disjunction? rest))) (cons expr rest)
    ;
    ;(and (disjunction? rest) (not (disjunction? expr))) (cons rest expr)
    ;
    ;(and (disjunction? expr) (disjunction? rest)) (cons ::or (cons (args expr) (args rest)))






    :else (cons ::or (cons expr rest)))

  )




;(assert (disjunction? (disjunction (variable :a) (variable :b))))


(defn negation? [expr]
  {:pre [(not (keyword? expr))]}
  (= ::not (first expr)))

(defn negation [expr]
  "Boolean not(!,~) "
  {:pre [(not (keyword? expr))]}
  (if (negation? expr)
    (args expr)
    (cons ::not expr)
    )

  )

;(assert (negation? (negation (variable :a))))


;


(defn conjunction? [expr]
  {:pre [(not (keyword? expr))]}
  (= ::and (first expr)))


(defn conjunction [expr & rest]
  "Boolean and(&) "
  (cons ::and (cons expr rest))
  )

;(assert (conjunction? (conjunction (variable :a) (variable :b))))


(defn propagate-neg [expr]
  {:pre [(not (keyword? expr))]}
  (println expr)
  (cond
    ;(negation? expr) (propagate-neg (args expr))

    (conjunction? expr) (apply disjunction (map (fn [x] (propagate-neg x)) (args expr)))

    (disjunction? expr) (apply conjunction (map (fn [x] (propagate-neg x)) (args expr)))

    :else (negation expr))
  )


(defn invert [expr]
  "Invert term"

  (cond

    (negation? expr) (rest expr)

    (conjunction? expr) (disjunction (rest expr))

    (disjunction? expr) (conjunction (rest expr))

    :else expr))





;(assert (= (invert (variable :a)) (variable :a)))

(def x (variable :X))
(def y (variable :Y))
(def z (variable :Z))

(def nx (negation x))
(def ny (negation y))

(def nx_or_y (disjunction nx y))
(def ny_or_z (disjunction ny z))

(def pre_f (disjunction nx_or_y (negation ny_or_z)))

(disjunction nx_or_y ny_or_z)

;(let [
;      x (variable :X)
;      y (variable :Y)
;      z (variable :Z)
;      nx (negation x)
;      ny (negation y)
;
;      nx_or_y (disjunction nx y)
;      ny_or_z (disjunction ny z)
;
;      pre_f (disjunction nx_or_y (negation ny_or_z))
;      f (negation pre_f)
;
;      ]
;  ;(assert (= (invert ab) aorb))
;
;  ;(assert (= (invert ab) aorb))
;  (println (propagate-neg pre_f))
;  ;(map #(negation % ) (rest naorb))
;  ;)
;
;  )




; propagate and so on



;(declare dnf)

(def dnf-rules
  (list
    ;not
    [(fn [expr] (negation? expr))
     (fn [expr] (

                  )

       )

     ]

    ;none
    [
     (fn [expr] expr)
     (fn [expr] expr)
     ]

    )
  )
;
;
(defn dnf [expr]
  ((some (fn [rule]
           (println rule)
           (if ((first rule) expr)
             (second rule)
             false))
         dnf-rules)
   expr))

;(dnf (conjunction
;       (disjunction (variable :a) (variable :b) (negation (variable :b)))
;       (variable :db)))
;
;(negation (negation (variable :a)))