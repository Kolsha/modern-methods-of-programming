(ns clojure4.extensions

  (:require
    [clojure4.atoms :refer :all]
    [clojure4.junction :refer :all]
    [clojure4.negation :refer :all]

    ))


(defn implication [a b]
  (disjunction (negation a) b)
  )


(defn pier-arrow [a b]

  (conjunction (negation a) (negation b))
  )

(defn equivalence [a b]
  (disjunction (conjunction a b) (conjunction (negation a) (negation b)))
  )

(defn schaeffer-stroke [a b]
  (disjunction (negation a) (negation b))
  )

