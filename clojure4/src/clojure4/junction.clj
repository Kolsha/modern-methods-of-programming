(ns clojure4.junction

  (:require
    [clojure4.atoms :refer :all]
    )
  )


(defn junction? [key]
  "base func for | and & operators"
  {:pre [(keyword? key)]}
  (fn [expr]
    "check if expr is one of operators"
    {:pre [(not (keyword? expr))]}

    (= key (first expr))
    )
  )

(defn junction [key, pred, collapse-val]
  "base func for | and & operators"
  {:pre [(keyword? key)]}
  (fn [expr & rest]
    "process operator, expand, collapses const, remove duplicates"
    {:pre [(not (keyword? expr))]}

    (cond
      (empty? rest) (if (pred expr) expr)
      :else
      (let [filter-const (fn [val x] (and (const? x) (= val (const-val x))))

            filter-false (partial filter-const false)
            filter-true (partial filter-const true)

            expanded (expand pred (cons expr rest) (const collapse-val))

            ;const-present (filter const? expanded)

            true-present (filter filter-true expanded)
            false-present (filter filter-false expanded)

            without-true (remove filter-true expanded)
            without-false (remove filter-false expanded)

            filter-one-element (fn [elements]
                                 (if (= 1 (count elements))
                                   (first elements)
                                   (cons key elements)
                                   )
                                 )
            ]
        ;(println expanded)
        (cond

          ;operator | and contains true const
          (and collapse-val (seq true-present))
          (first true-present)

          ;operator | and not contains true const
          (and collapse-val (empty? true-present))
          (if (seq without-false) (filter-one-element without-false) (first false-present))

          ;operator & and contains false
          (and (not collapse-val) (seq false-present))
          (first false-present)


          ;operator & and contains false
          (and (not collapse-val) (empty? false-present))
          (if (seq without-true) (filter-one-element without-true) (first true-present))

          :else (filter-one-element expanded)

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

