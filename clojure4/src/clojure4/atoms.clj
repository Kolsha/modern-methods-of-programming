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

  (cond
    (const? expr) (not (const-val expr))

    :else (= ::not (first expr))
    )
  )

(defn negation_internal [expr]
  "Boolean not(!,~) "
  "a => !a, !a => a, true => false and so on"
  "(a & b ...) => !(a & b ...)"
  {:pre [(not (keyword? expr))]}

  (cond
    (const? expr) (const (not (const-val expr)))
    (negation? expr) (first (args expr))
    :else (cons ::not (list expr))
    )

  )


(defn collapse-consts [expr replace-val]
  "finds possible constants and replaces its on replace-val"
  "(a !a b (a | b) !(a | b) c (a & c)) => (b c (a & c) replace-val)"
  (let [
        poss-consts (reduce (fn [acc, el]
                              (if
                                (and (not (const? el)) (in? expr (negation_internal el)))
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

  "finds expr by pred and expand them with replaced duplicated and collapsed constants"
  "(a (pred b c d) (pred d a) (another-pred e d)) => (a b c d (another-pred e d)) "
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