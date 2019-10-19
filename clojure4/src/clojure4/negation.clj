(ns clojure4.negation

  (:require
    [clojure4.atoms :refer :all]
    [clojure4.junction :refer :all]
    )
  )


(defn negation [expr]
  {:pre [(not (keyword? expr))]}
  (cond


    (disjunction? expr) (apply conjunction (map (fn [x] (negation x)) (args expr)))

    (conjunction? expr) (apply disjunction (map (fn [x] (negation x)) (args expr)))



    :else (negation_internal expr))
  )
