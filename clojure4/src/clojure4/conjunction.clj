(ns clojure4.conjunction

  (:require
    [clojure4.atoms :refer :all]
    )
  )


(defn junction? [key]
  (fn [expr]
    {:pre [(not (keyword? expr))]}

    (= key (first expr))
    )
  )

(defn junction [key, pred, collapse-val]
  (fn [expr & rest]
    {:pre [(not (keyword? expr))]}

    (cond
      (empty? rest) (if (pred expr) expr)
      :else
      (let [expanded (expand pred (cons expr rest) (const collapse-val))]
        (if (and (= (count expanded) 1) (const? (first expanded)))

          (first expanded)
          (cons key expanded)
          )
        )
      )

    )
  )


(def ^:private conjunction-key ::and)

(def conjunction? (junction? conjunction-key))
(def conjunction (junction conjunction-key conjunction? false))




(def ^:private disjunction-key ::or)

(def disjunction? (junction? disjunction-key))
(def disjunction (junction disjunction-key disjunction? true))


;(defn conjunction? [expr]
;  {:pre [(not (keyword? expr))]}
;  (= ::and (first expr)))
;
;
;(defn conjunction [expr & rest]
;  "Boolean and(&) "
;  ;(cons ::and (cons expr rest))
;
;
;  (cond
;    (empty? rest) (if (conjunction? expr) expr)
;    :else
;    (let [expanded (expand conjunction? (cons expr rest) (const false))]
;      (if (and (= (count expanded) 1) (const? (first expanded)))
;
;        (first expanded)
;        (cons ::and expanded)
;        )
;      )
;    )
;  )