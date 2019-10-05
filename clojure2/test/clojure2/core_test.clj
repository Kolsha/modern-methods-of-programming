(ns clojure2.core-test
  (:require [clojure.test :refer :all]
            [clojure2.core :refer :all]))







(deftest time-small-test
  (testing "Timing small"
    (let [int_cube (integralT cube 1/10)
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
  (testing "Timing small"
    (let [int_cube (integralT cube 1/10)
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