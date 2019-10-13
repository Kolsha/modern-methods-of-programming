(ns clojure4.atoms-test
  (:require [clojure.test :refer :all]
            [clojure4.atoms :refer :all]
            ))



(deftest variable-test
  (testing "variable"
    (let [a (variable :a)
          b (variable :b)
          na (variable :a)
          ]

      (is (variable? a))
      (is (variable? a))
      (is (variable? b))
      (is (not (variable? (list))))

      (is (thrown? AssertionError (variable "var")))

      (is (same-variables? a na))
      (is (= :a (variable-name a)))

      )

    )

  )

(deftest const-test
  (testing "const"
    (let [a (const true)
          b (const false)
          na (const true)
          ]

      (is (const? a))
      (is (const? b))
      (is (const? na))
      (is (= (const-val a) (const-val na)))

      (is (thrown? AssertionError (const "var")))

      )

    )
  )


(deftest negation-const-test
  (testing "neg const"
    (let [a (const true)
          b (const false)
          na (const true)
          ]

      (is (= b (negation a)))

      (is (= a (negation b)))
      (is (= a (negation (negation a))))

      (is (= b (negation na)))

      (is (negation? b))
      (is (not (negation? a)))

      )

    )
  )


(deftest negation-variable-test
  (testing "neg variable"
    (let [a (variable :a)

          na (negation a)
          ]

      (is (not (= a (negation a))))

      (is (= a (negation (negation a))))

      (is (= na (negation (negation (negation a)))))

      (is (same-variables? a (negation na)))
      (is (same-variables? (negation na) a))

      )

    )

  )