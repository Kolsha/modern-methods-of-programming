(ns clojure4.core-test
  (:require [clojure.test :refer :all]
            [clojure4.core :refer :all]
            [clojure4.atoms :refer :all]
            [clojure4.junction :refer :all]
            [clojure4.negation :refer :all]))

(deftest to-str-test
  (testing "to-str test"
    (let [a (variable :a)
          b (variable :b)
          a-or-b (disjunction a b)
          a-and-b (conjunction a b)
          na (negation a)
          n_a-and-b (negation a-and-b)
          t (const true)
          f (const false)
          ]

      (is (= "true" (to-str t)))
      (is (= "false" (to-str f)))
      (is (= "a" (to-str a)))
      (is (= "b" (to-str b)))

      (is (= "!a" (to-str na)))

      (is (= "(a | b)" (to-str a-or-b)))
      (is (= "(a & b)" (to-str a-and-b)))


      (is (= "(!a | !b)" (to-str n_a-and-b)))

      )

    )

  )



(deftest dnf-test
  (testing "dnf"
    (let [a (variable :a)
          b (variable :b)
          a-or-b (disjunction a b)
          a-and-b (conjunction a b)
          na (negation a)
          n_a-and-b (negation a-and-b)

          ]


      (is (= "a" (to-str (dnf a))))
      (is (= "b" (to-str (dnf b))))

      (is (= "!a" (to-str (dnf na))))

      (is (= "(a | b)" (to-str (dnf a-or-b))))

      (is (= "(a & b)" (to-str (dnf a-and-b))))


      (is (= "(!a | !b)" (to-str (dnf n_a-and-b))))

      )

    )

  )



(deftest dnf-wiki-test
  (testing "dnf from wikipedia"
    (let [x (variable :X)
          y (variable :Y)
          z (variable :Z)

          nx (negation x)
          ny (negation y)

          nx_or_y (disjunction nx y)
          ny_or_z (disjunction ny z)

          pre_f (disjunction nx_or_y (negation ny_or_z))

          f (negation pre_f)

          ]
      (is (= "(X & !Y & (!Y | Z))" (to-str f)))
      (is (= "((!Y & X) | (Z & X & !Y))" (to-str (dnf f))))
      )

    )

  )