(ns clojure3.1-test
  (:require [clojure.test :refer :all]
            [clojure3.1 :refer :all]))



(deftest value-small-test
  (testing "Timing small"
    (let [
          step 1/100
          int_cube (integralT cube step)
          first_call (nth int_cube 9)
          second_call (nth int_cube 19)
          third_call (nth int_cube 199)

          ]
      (println first_call second_call third_call)

      (is (< (- (first first_call) 1/4) step))

      (is (< (- (first second_call) 4) step))

      (is (< (- (first third_call) 40000) step))


      )

    )

  )

(deftest time-small-test
  (testing "Timing small"
    (let [int_cube (integralT cube 1/10)
          first_call (first (my_time (take 3 int_cube)))
          second_call (first (my_time (take 5 int_cube)))
          third_call (first (my_time (take 3 int_cube)))

          ]

      (is (> first_call second_call))
      (is (> first_call third_call))

      (is (> second_call third_call))

      )

    )

  )
