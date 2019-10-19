(ns clojure4.junction-test
  (:require [clojure.test :refer :all]

            [clojure4.atoms :refer :all]
            [clojure4.junction :refer :all]
            [clojure4.negation :refer :all]
            )

  )


(deftest disjunction-var-test
  (testing "disjunction var"
    (let [a (variable :a)
          b (variable :b)
          a-or-b (disjunction a b)
          t (const true)
          f (const false)
          ]

      (is (not (disjunction? a)))
      (is (disjunction? a-or-b))

      (is (= a-or-b (disjunction a-or-b a)))
      (is (= a-or-b (disjunction a-or-b a a a a a)))
      (is (= a-or-b (disjunction a-or-b a b a b a)))
      (is (= a-or-b (disjunction a-or-b a b a-or-b a b a a-or-b a)))



      (is (= (negation a-or-b) (negation (disjunction a-or-b a b a-or-b a b a a-or-b a))))


      (is (= t (disjunction a (negation a))))

      (is (= t (disjunction b a (negation a))))

      )

    )

  )


(deftest disjunction-const-test
  (testing "disjunction var"
    (let [a (const true)
          b (const false)
          a-or-b (disjunction a b)

          ]

      (is (not (disjunction? a)))
      (is (not (disjunction? a-or-b)));case true || false => true

      (is (= a-or-b (disjunction a-or-b a)))
      (is (= a-or-b (disjunction a-or-b a a a a a)))
      (is (= a-or-b (disjunction a-or-b a b a b a)))
      (is (= a-or-b (disjunction a-or-b a b a-or-b a b a a-or-b a)))



      (is (= (negation a-or-b) (negation (disjunction a-or-b a b a-or-b a b a a-or-b a))))


      (is (= a (disjunction b a (negation a))))

      )

    )

  )

