(ns object-model.core-test
  (:require [clojure.test :refer :all]
            [object-model.core :refer :all]))

(deftest cyclic-test


  (let [
        ;cyclic-inc #'object-model.core/cyclic-inc
        ;cyclic-dec #'object-model.core/cyclic-dec
        ]

    (testing "cyclic-inc"
      ;current_indices max_indices_values
      (is (= (cyclic-inc '(0 0) '(1 1)) '(0 1)))

      (is (= (cyclic-inc '(1 1) '(1 1)) '(0 0)))

      (is (thrown? AssertionError (cyclic-inc '(1 2) '(1 2 3))))


      (is (= (cyclic-inc '(0 0 1 0 1 0 6) '(1 1 2 3 4 5 6)) '(0 0 1 0 1 1 0)))

      )


    (testing "cyclic-dec"
      ;current_indices max_indices_values
      (is (= (cyclic-dec '(0 0) '(1 1)) '(1 1)))

      (is (= (cyclic-dec '(1 1) '(1 1)) '(1 0)))

      (is (thrown? AssertionError (cyclic-inc '(1 2) '(1 2 3))))

      (is (= (cyclic-dec '(0 0 1 0 1 0 0) '(1 1 2 3 4 5 6)) '(0 0 1 0 0 5 6)))

      )

    )

  )
