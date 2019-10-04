(ns clojure2.core

  )


(defmacro my_time
  "Evaluates expr.  Returns time of
 expr."
  {:added "1.0"}
  [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (/ (double (- (. System (nanoTime)) start#)) 1000000.0)
     ))

(defn cube
  [x]
  (* x x x)
  )


;(defn max_arities [v]
;  (apply max (->> v meta :arglists (map count))))

(defn it [f, step, a, b]
  (let [n (/ (- b a) step)
        rng (range 1 n)
        fab (/ (+ (f a) (f b)) 2)
        ]

    (* step
       (+ fab
          (reduce + (map (fn [el] (f (+ a (* el step)))) rng))
          )
       )

    )

  )


(defn trap [f a b]
  (let [step (- b a)]

    (* (/ (+ (f a) (f (+ a step))) 2) step)
    )
  )

(def trap-mem (memoize trap))

;(defn itr [f, step, a, b & {:keys [base] :or {base 0}}]
;  (if (>= a b)
;
;    0
;
;    (#'itr f step (+ a step) b :base (+ (trap-mem f a (+ a step))
;                                  base))
;
;    )
;  )

;(defn itr [f_, step, a, b]
;  (letfn [(inner [f, step, a, b]
;            (println a b)
;            (if (> (+ a step) b)
;
;              0
;
;              (+ (trap-mem f (- b step) b)
;                 (inner1 f step 0 (- b step)
;                         ))
;
;              )
;
;            )
;          (inner1 (memoize inner))
;          ]
;
;    (println (inner f_, step, a, b))
;    )
;
;  )

(let [itr_p (memoize (fn [rec, f, step, a, b]
                       (println a b)
                       (if (> (+ a step) b)

                         0

                         (+ (trap-mem f (- b step) b)
                            (rec rec f step 0 (- b step)
                                 ))

                         )
                       ))
      itr (partial itr_p itr_p)]
  )


(defn integralT
  "Integral by Trapezoidal rule"
  ([f]
   (integralT f 1/10)
   )

  ([f step]
   {:pre [(> step 0)]}



   (let [itr_p (memoize (fn [rec, f, step, a, b]
                          (println a b)
                          (if (> (+ a step) b)

                            0

                            (+ (trap-mem f (- b step) b)
                               (rec rec f step a (- b step)
                                    ))

                            )
                          ))
         itr (partial itr_p itr_p)]

     (fn [x]
       (itr f step 0 x)
       )

     )

   )
  )


;(def itr (memoize itr))
;(def itr
;  (memoize (fn [f, step, a, b]
;
;             (if (>= a b)
;
;               0
;               (+ (trap-mem f a (+ a step))
;                  (itr f step (+ a step) b))
;               )
;
;             )
;           )
;  )

;(def itr
;  (memoize (letfn [(my_loop [f, step, a, b & {:keys [base] :or {base 0}}]
;                     (println a b)
;                     (if (< a b)
;                       (recur f step (+ a step) b (+ (trap-mem f a (+ a step))
;                                                     base))
;
;
;                       0
;                       )
;
;                     )]
;
;             )
;           )
;  )

;(def itr
;  (memoize (letfn [(my_loop [f, step, a, b & {:keys [base] :or {base 0}}]
;                     (println a b)
;
;
;                     )]
;
;             )
;           )
;  )




;(def it-mem (memoize it))
;
;(defn integralT
;  "Integral by Trapezoidal rule"
;  ([f]
;   (integralT f 100)
;   )
;
;  ([f step]
;   {:pre [(> step 0)]}
;   (memoize (fn [x]
;              (let [f-mem (memoize f)]
;                (+ (it-mem f-mem step 0 (- x step)) (it-mem f-mem step (- x step) x))
;                )
;
;              )
;            )
;   )
;  )


;
;(it cube 0.001 0 2.0)
;;(println (itr cube 0.001 0 2.0))
;
;(defn itrcube [x]
;  (itr cube 0.001 0 x)
;  )
;
;
;(println (itrcube 2))

(def it-long (integralT cube 1/10))
;(it-long 1)
;(my_time (it-long 1))



