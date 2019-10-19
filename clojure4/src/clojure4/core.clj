(ns clojure4.core
  (:require
    [clojure4.atoms :refer :all]
    [clojure4.junction :refer :all]
    [clojure4.negation :refer :all]
    )
  )







(defn dnf [expr]
  {:pre [(not (keyword? expr))]}
  (let [sel-or (fn [x] (and (not (keyword? x)) (disjunction? x)))
        rem-or (fn [x] (or (keyword? x) (disjunction? x)))
        or_terms (filter sel-or expr)
        other (remove rem-or expr)
        ]


    (cond
      (and (conjunction? expr) (not-empty or_terms))
      (let [ands (map (fn [el]
                        ;(println el)
                        (map
                          (fn [x]                           ;(println x)
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


(use '[clojure.string :only (join split)])

(defn to-str [expr]
  {:pre [(not (keyword? expr))]}
  (cond

    (empty? expr)
    ""

    (const? expr)
    (str (const-val expr))

    (variable? expr)
    (str (name (variable-name expr)))

    (negation? expr)
    (str "!" (apply to-str (args expr)))

    (disjunction? expr)
    (str "(" (join " | " (map (fn [x] (to-str x)) (args expr))) ")")

    (conjunction? expr)
    (str "(" (join " & " (map (fn [x] (to-str x)) (args expr))) ")")

    )
  )

; забыл реализовать подстановку
(defn substitution [expr vals]
  (println expr)
  (cond

    (and (variable? expr) (contains? vals (variable-name expr)))
    (const (get vals (variable-name expr)))

    (negation? expr)
    (negation (substitution (first (args expr)) vals))

    (disjunction? expr)
    (apply disjunction (map (fn [x] (substitution x vals)) (args expr)))

    (conjunction? expr)
    (apply conjunction (map (fn [x] (substitution x vals)) (args expr)))


    :else expr)
  )


;(substitution nx_or_y {:X true :Y true})
;(to-str f)
;
;(to-str (dnf f))