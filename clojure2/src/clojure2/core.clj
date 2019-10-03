(ns clojure2.core)


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

(defn it [f, n, a, b]
  (let [step (/ (- b a) n)
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

(def it-mem (memoize it))

(defn integralT
  "Integral by Trapezoidal rule"
  ([f]
   (integralT f 100)
   )

  ([f n]
   {:pre [(> n 0)]}
   (memoize (fn [x]
              (let [f-mem (memoize f)]
                (+ (it-mem f-mem n 0 (/ x 2)) (it-mem f-mem n (/ x 2) x))
                )

              )
            )
   )
  )



(def it-long (integralT cube 100000))
;(it-long 1.0)
(my_time (it-long 1.0))



