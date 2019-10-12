(ns clojure4.atoms

  )


(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))


(defn args [expr]
  (rest expr))

(declare const?)
(declare const)
(declare const-val)

(defn negation? [expr]
  {:pre [(not (keyword? expr))]}
  (= ::not (first expr)))

(defn negation [expr]
  "Boolean not(!,~) "
  {:pre [(not (keyword? expr))]}

  (cond
    (const? expr) (const (not (const-val expr)))
    (negation? expr) (args expr)
    :else (cons ::not expr)
    )

  )


(defn collapse-consts [expr replace-val]
  (let [
        poss-consts (reduce (fn [acc, el]
                              (if
                                (and (not (const? el)) (in? expr (negation el)))
                                (conj acc el)
                                acc
                                )
                              )


                            (list)
                            expr)
        ]
    ;(println "possible consts: " poss-consts)
    (map (fn [x]
           (if (in? poss-consts x)
             replace-val
             x
             )
           ) expr)

    )

  )




(defn expand [pred exprs replace-val]
  ;(println exprs)
  (let [
        matched (filter pred exprs)
        other (remove pred exprs)
        other (collapse-consts other replace-val)


        res (apply concat (map (fn [x]
                                 ;(println x)
                                 (args x))
                               matched))
        res (collapse-consts res replace-val)

        ;res (concat res)
        ]
    ;(println (collapse-consts exprs (const true)))
    (if (empty? other)
      (distinct res)
      (distinct (apply concat (cons res (list other))))
      )

    )

  )

(defn const [val]
  {:pre [(boolean? val)]}
  (list
    ::const val)
  )

(defn const? [expr]
  {:pre [(not (keyword? expr))]}
  (= (first expr) ::const)
  )

(defn const-val [c]
  {:pre [(const? c)]}
  (second c)
  )


(defn variable [name]
  {:pre [(keyword? name)]}
  (list ::var name))

(defn variable? [expr]
  {:pre [(not (keyword? expr))]}
  (= (first expr) ::var)
  )

(assert (variable? (variable :a)))


(defn variable-name [v]
  {:pre [(not (keyword? v))]}
  (second v))

(defn same-variables? [v1 v2]
  (and
    (variable? v1)
    (variable? v2)
    (= (variable-name v1)
       (variable-name v2))))