(ns clojure1.core-test
  (:require [clojure.test :refer :all]
            [clojure1.core :refer :all]))

(deftest abc-test
  (testing "abc n"

    (is (= (solution "abc" 1) (list "a" "b" "c")))
    (is (= (solution "abc" 2)
           (list "ab" "ac" "ba" "bc" "ca" "cb")
           ))

    )
  )
