(ns clojure4.core
  (:require
    [clojure4.atoms :refer :all]
    [clojure4.disjunction :refer :all]
    [clojure4.conjunction :refer :all]
    [clojure4.negation :refer :all]
    )
  )






;(collapse-consts (list (variable :a) (variable :b) (negation (variable :a))))
;

(defn propagate-neg [expr]
  {:pre [(not (keyword? expr))]}
  (println expr)
  (cond
    ;(negation? expr) (propagate-neg (args expr))

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

    ;(println or_terms)
    ;(println other)
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

      ;(reduce (fn [acc, el]
      ;          (let [els (args el)
      ;                ]
      ;
      ;
      ;
      ;            )
      ;          ) or_terms
      ;        )


      :else expr)

    )
  )

;(distribute (conjunction (variable :a) (variable :b) (disjunction (variable :c) (variable :d))))




;(assert (= (invert (variable :a)) (variable :a)))

(def x (variable :X))
(def y (variable :Y))
(def z (variable :Z))

(def nx (negation x))
(def ny (negation y))

(def nx_or_y (disjunction nx y))
(def ny_or_z (disjunction ny z))

(def triple_or (disjunction nx_or_y ny_or_z (conjunction x y z)))


(def pre_f (disjunction nx_or_y (negation ny_or_z)))

(def pre1 (propagate-neg pre_f))

(def pre (distribute pre1))

;(disjunction nx_or_y ny_or_z)

;(distribute disjunction? (rest (disjunction nx_or_y ny_or_z (conjunction x y))))

(let [
      x (variable :X)
      y (variable :Y)
      z (variable :Z)
      nx (negation x)
      ny (negation y)

      nx_or_y (disjunction nx y)
      ny_or_z (disjunction ny z)

      pre_f (disjunction nx_or_y (negation ny_or_z))
      f (negation pre_f)

      ]
  ;(assert (= (invert ab) aorb))

  ;(assert (= (invert ab) aorb))
  (println (distribute (propagate-neg pre_f)))
  ;(map #(negation % ) (rest naorb))
  ;)

  )




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



