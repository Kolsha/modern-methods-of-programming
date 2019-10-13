(ns clojure4.core
  (:require
    [clojure4.atoms :refer :all]
    [clojure4.junction :refer :all]
    [clojure4.negation :refer :all]
    )
  )


(defn propagate-neg [expr]
  {:pre [(not (keyword? expr))]}
  (println expr)
  (cond


    (disjunction? expr) (apply conjunction (map (fn [x] (propagate-neg x)) (args expr)))

    (conjunction? expr) (apply disjunction (map (fn [x] (propagate-neg x)) (args expr)))



    :else (negation expr))
  )


(defn distribute [expr]
  {:pre [(not (keyword? expr))]}
  (let [sel-or (fn [x] (and (not (keyword? x)) (disjunction? x)))
        rem-or (fn [x] (or (keyword? x) (disjunction? x)))
        or_terms (filter sel-or expr)
        other (remove rem-or expr)
        ]


    (cond
      (and (conjunction? expr) (not-empty or_terms))
      (let [ands (map (fn [el]
                        (println el)
                        (map
                          (fn [x] (println x)
                            (apply conjunction (cons x other)))

                          (args el))
                        )

                      or_terms)]

        (apply disjunction (apply concat ands))

        )


      :else expr)

    )
  )



(def x (variable :X))
(def y (variable :Y))
(def z (variable :Z))

(def nx (negation x))
(def ny (negation y))

(def nx_or_y (disjunction nx y))
(def ny_or_z (disjunction ny z))

(def triple_or (disjunction nx_or_y ny_or_z (conjunction x y z)))


(def pre_f (disjunction nx_or_y (negation ny_or_z)))
(def f (negation pre_f))






(declare dnf)

(def dnf-rules
  (list
    ;not
    [(fn [expr] (negation? expr))
     (fn [expr] (dnf (apply propagate-neg (args expr)))

       )

     ]

    ;none
    [
     (fn [expr] expr)
     (fn [expr] (distribute expr))
     ]

    )
  )


(defn dnf [expr]
  ((some (fn [rule]
           (println rule)
           (if ((first rule) expr)
             (second rule)
             false))
         dnf-rules)
   expr))





