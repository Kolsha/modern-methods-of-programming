(ns clojure2.core

  )

;https://stackoverflow.com/questions/12955024/recursion-inside-let-function
;http://danmidwood.com/content/2013/02/24/exploring-clojure-memoization.html
;https://blog.jayway.com/2011/04/02/numerical-integration-with-precision/

(defmacro my_time
  "Evaluates expr.  Returns time of
 expr."
  {:added "1.0"}
  [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (list (/ (double (- (. System (nanoTime)) start#)) 1000000.0) ret#)
     ))

(defn cube
  [x]
  (* x x x)
  )

(defn ninth [x]
  (* (cube x) (cube x))
  )



;(defn it [f, step, a, b]
;  (let [n (/ (- b a) step)
;        rng (range 1 n)
;        fab (/ (+ (f a) (f b)) 2)
;        ]
;
;    (* step
;       (+ fab
;          (reduce + (map (fn [el] (f (+ a (* el step)))) rng))
;          )
;       )
;
;    )
;
;  )

(def it-mem (memoize it))


(defn trap [f a b]
  (let [step (- b a)]

    (* (/ (+ (f a) (f (+ a step))) 2) step)
    )
  )

(def trap-mem (memoize trap))



(defn integralT
  "Integral by Trapezoidal rule"
  ([f]
   (integralT f 1/10)
   )

  ([f step]
   {:pre [(> step 0)]}

   ; set less params

   (let [itr_p (memoize (fn [rec, f, step, a, b]
                          ;(println a b)
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


;(let [
;      itr1 (fn [base, rec, f, step, a, b]
;
;             (+
;                base (rec rec f step a (- b step)))
;             )
;
;      itr_p (memoize (fn [rec, f, step, a, b]
;                       ;(println a b)
;                       (if (> (+ a step) b)
;
;                         0
;                         (itr1 (trap-mem f (- b step) b) rec f step a b)
;                         )
;                       )
;                     )
;      itr (partial itr_p itr_p)]
;
;  (my_time (println (itr cube 1/1000 0 6)))
;  (my_time (println (itr cube 1/1000 0 6)))
;
;
;
;
;  )

;(letfn [(inter [f, step, a, b]
;          (println a b)
;
;          (if (> (+ a step) b)
;
;            0
;
;            (recur  f step a (- b step)
;
;                 :base (+ (trap-mem f (- b step) b)
;                          base)
;                 )
;            )
;
;          )])




;(def it-long (integralT cube 1/10))
;(it-long 1)
;(my_time (it-long 1))



