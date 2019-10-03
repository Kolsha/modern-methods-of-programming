(ns clojure1.core
  (:require [clojure.string :as str])
  )

(defn solution [s, n]
  {:pre [(and (> n 0) (not-empty (distinct s)))]}

  (let [s_a (take n (repeat (distinct s)))

        res (reduce                                         ;reduce s_a begin
              (fn [acc el]
                ;(println "reduce:s_a" acc el)
                (apply concat
                       (map                                 ; map acc begin
                         (fn [a]
                           ;(println "map:acc:" a)
                           (map
                             (fn [e]
                               ;(println "map:el:" e)
                               ;(println (first a) a e)
                               ;(println (conj a e))
                               (conj a e)

                               )
                             (filter                        ;   filtered el
                               (fn [e]
                                 (not= (first a) e)
                                 )
                               el)                          ;   filtered el
                             )
                           )
                         acc
                         )                                  ; map acc end
                       )
                )

              (list (list))                                 ;reduce acc
              s_a                                           ;reduce arr
              )                                             ;reduce s_a end

        ]


    (sort (map (fn [e] (str/join "" e))
               (reduce
                 (fn [a, e]
                   (conj a e)
                   )
                 (list)
                 res)
               ))

    )

  )

;(println (solution "abc" 3))
