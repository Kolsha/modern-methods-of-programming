(ns clojure2.core

  )

;https://stackoverflow.com/questions/12955024/recursion-inside-let-function
;http://danmidwood.com/content/2013/02/24/exploring-clojure-memoization.html
;https://blog.jayway.com/2011/04/02/numerical-integration-with-precision/

(defmacro my_time
  "Evaluates expr. Returns time of
 expr."
  {:added "1.0"}
  [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (list (/ (double (- (. System (nanoTime)) start#)) 1000000.0) ret#)
     ))

(defn cube
  "return x^3"
  [x]
  (* x x x)
  )

(defn ninth [x]
  "return x^9"
  (* (cube x) (cube x))
  )




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



   (let [a 0                                                ;can be changed to any start point
         itr_p (memoize (fn [rec, b]
                          ;(println a b)
                          (let [pos (* (int (/ b step)) step)]

                            (if (> (+ a step) b)

                              0

                              (+ (trap-mem f (- pos step) pos)
                                 (trap-mem f pos b)
                                 (rec rec (- pos step)
                                      ))

                              )
                            )
                          ))
         itr (partial itr_p itr_p)]

     (fn [x]
       (itr x)
       )

     )

   )
  )