(ns clojure4.negation

  (:require
    [clojure4.atoms :refer :all]
    )
  )


;(defn negation? [expr]
;  {:pre [(not (keyword? expr))]}
;  (= ::not (first expr)))
;
;(defn negation [expr]
;  "Boolean not(!,~) "
;  {:pre [(not (keyword? expr))]}
;
;  (cond
;    (const? expr) (const (not (const-val expr)))
;    (negation? expr) (args expr)
;    :else (cons ::not expr)
;    )
;
;  )
