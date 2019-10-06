(ns clojure2.core-test
  (:require [clojure.test :refer :all]
            [clojure2.core :refer :all]))




(deftest value-small-test
  (testing "Value test small"
    (let [
          step 1/10
          int_cube (integralT cube step)
          first_call (int_cube 1)
          second_call (int_cube 2)
          third_call (int_cube 3)

          ]
      ;(println first_call second_call third_call)

      (is (< (- first_call 1/4) step))

      (is (< (- second_call 4) step))

      (is (< (- third_call 81/4) step))

      )

    )

  )


(deftest point-dont-hit-step
  (testing "Point shifted test"
    (let [int_cube (integralT cube 1/8)
          first_call (first (my_time (int_cube 3)))
          second_call (first (my_time (int_cube 5)))
          third_call (first (my_time (int_cube (+ 6 1/4))))

          ]

      (is (> first_call second_call))
      (is (> first_call third_call))

      (is (> second_call third_call))

      )

    )

  )



(deftest time-small-test
  (testing "Timing small"
    (let [int_cube (integralT cube 1/11)
          first_call (first (my_time (int_cube 3)))
          second_call (first (my_time (int_cube 5)))
          third_call (first (my_time (int_cube 6)))

          ]

      (is (> first_call second_call))
      (is (> first_call third_call))

      (is (> second_call third_call))

      )

    )

  )

(deftest time-big-test
  (testing "Timing big"
    (let [int_cube (integralT cube 1/12)
          first_call (first (my_time (int_cube 10)))
          second_call (first (my_time (int_cube 5)))
          third_call (first (my_time (int_cube 15)))

          ]

      (is (> first_call second_call))
      (is (> first_call third_call))

      (is (< second_call third_call))

      )

    )

  )