(ns clojure4.disjunction

  (:require
    [clojure4.atoms :refer :all]

    )
  )




;(defn disjunction? [expr]
;  {:pre [(not (keyword? expr))]}
;  (= ::or (first expr)))
;
;(defn disjunction [expr & rest]
;  "Boolean or(|) "
;
;  (cond
;    (empty? rest) (if (disjunction? expr) expr)
;    :else
;    (let [expanded (expand disjunction? (cons expr rest) (const true))]
;      (if (and (= (count expanded) 1) (const? (first expanded)))
;
;        (first expanded)
;        (cons ::or expanded)
;        )
;      )
;    )
;  )