(ns clojure3.1)

(def naturals
  (lazy-seq
    (cons 1 (map (fn [x]
                   (println x)
                   (inc x)
                   ) naturals))))


(defn trap [f a b]
  (let [step (- b a)]

    (* (/ (+ (f a) (f (+ a step))) 2) step)
    )
  )

(def trap-mem (memoize trap))


(defn cube
  [x]
  (* x x x)
  )

(def it
  (lazy-seq
    (cons (list (trap-mem cube 0 1) 1) (map (fn [x]
                                              (let [cur_v (first x)
                                                    cur_p (last x)
                                                    next_p (+ (last x) 1)]

                                                (println x)
                                                (list (+ cur_v
                                                         (trap-mem cube cur_p next_p)
                                                         ) next_p)
                                                )
                                              ) it))))

(take 10 it)

(defn integralT
  "Integral by Trapezoidal rule"
  ([f]
   (integralT f 1/10)
   )

  ([f step]
   {:pre [(> step 0)]}


   ;(defn it []
   ;  (lazy-seq
   ;    (cons (list (trap-mem f 0 step) step) (map (fn [x]
   ;                                                 (let [cur_v (first x)
   ;                                                       cur_p (last x)
   ;                                                       next_p (+ (last x) step)]
   ;
   ;                                                   (println x)
   ;                                                   (list (+ cur_v
   ;                                                            (trap-mem cube cur_p next_p)
   ;                                                            ) next_p)
   ;                                                   )
   ;                                                 ) it))))

   (letfn [(lz []
             (lazy-seq (cons (list (trap-mem f 0 step) step) (map (fn [x]
                                                                    (let [cur_v (first x)
                                                                          cur_p (last x)
                                                                          next_p (+ (last x) step)]

                                                                      (println x)
                                                                      (list (+ cur_v
                                                                               (trap-mem cube cur_p next_p)
                                                                               ) next_p)
                                                                      )
                                                                    ) (lz))))

             )
           ]
     (lz)
     )

   )
  )


(def it-long (integralT cube 1/10))

(let [f cube
      step 1/10]
  (list (trap-mem f 0 step) step)
  )
;(time (nth naturals 10))
;(time (nth naturals 21))
;
;(nth naturals 11)
;(take 10 naturals)
; (take 1 (it-long))