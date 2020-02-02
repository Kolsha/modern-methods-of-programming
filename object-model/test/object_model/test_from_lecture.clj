(ns object-model.test-from-lecture
  (:require [clojure.test :refer :all]
            [object-model.core :refer :all]))

(deftest lecture-test

  (testing "def-class"


    (is (thrown? AssertionError (def-class BaseObject () () ())))

    (is (not= (def-class Vehicle ()
                         (:name)
                         (init :name "Vehicle Unnamed")) nil))

    (is (not= (def-class LandVehicle (Vehicle) ()) nil))
    (is (not= (def-class FloatingVehicle (Vehicle) ()) nil))
    (is (not= (def-class FlyingVehicle (Vehicle) ()) nil))
    (is (not= (def-class SimpleVehicle (Vehicle) ()) nil))
    (is (not= (def-class AmphibianVehicle (LandVehicle FloatingVehicle) ()) nil))
    (is (not= (def-class Armour (AmphibianVehicle) ()) nil))
    (is (not= (def-class Bicycle (LandVehicle SimpleVehicle) ()) nil))
    (is (not= (def-class Plane (FlyingVehicle) ()) nil))

    ;; Drivers
    (is (not= (def-class Driver ()
                         (:name)
                         (init :name "Driver Unnamed")) nil))

    (is (not= (def-class AnimalDriver (Driver) ()) nil))
    (is (not= (def-class HumanDriver (Driver) ()) nil))
    (is (not= (def-class Pilot (HumanDriver) ()) nil))

    )


  (testing "def-method"

    (is (not=
          (def-multi-method capabilities) nil))


    (is (not=
          (def-method capabilities [(Vehicle v) arg1]

                      (list (getf v :name) (inc arg1))
                      )

          nil))

    (is (not=
          (def-method capabilities [(FlyingVehicle v) arg1]


                      (concat (call-next-method) (list "fly" (dec arg1)))
                      )
          nil))

    (is (not=
          (def-method capabilities [(LandVehicle v) arg1]


                      (concat (call-next-method) (list "move on land" (inc arg1)))
                      )
          nil))
    (is (not=
          (def-method capabilities [(FloatingVehicle v) arg1]


                      (concat (call-next-method arg1) (list "sail" (dec arg1)))
                      )
          nil))

    (is (not=
          (def-method capabilities [(SimpleVehicle v) arg1]

                      (concat (call-next-method) (list "be driven by anyone" (inc arg1)))
                      )
          nil))




    (is (not=
          (def-multi-method can-ride)
          nil))

    (is (not=
          (def-method can-ride [(Driver d) (Vehicle v)] true)
          nil))

    (is (not=
          (def-method can-ride [(AnimalDriver d) (SimpleVehicle v)] true)
          nil))

    (is (not=
          (def-method can-ride [(AnimalDriver d) (Vehicle v)] false)
          nil))

    (is (not=
          (def-method can-ride [(Driver d) (FlyingVehicle v)] false)
          nil))

    (is (not=

          (def-method can-ride [(Pilot d) (FlyingVehicle v)] true)
          nil))






    (is (not=
          (def-multi-method ride)
          nil))

    (is (not=
          (def-method ride [(Driver d) (Vehicle v)]
                      (println (getf d :name) "rides" (getf v :name)))
          nil))

    (is (not=
          (def-method ride [(AnimalDriver d) (SimpleVehicle v)]
                      (println (getf d :name) "is smart and rides" (getf v :name)))
          nil))

    (is (not=
          (def-method ride [(AnimalDriver d) (Vehicle v)]
                      (println (getf d :name) "is not smart enough to ride" (getf v :name)))
          nil))

    (is (not=
          (def-method ride [(Driver d) (FlyingVehicle v)]
                      (println (getf d :name) "requires special training to fly" (getf v :name)))
          nil))

    (is (not=
          (def-method ride [(Pilot d) (FlyingVehicle v)]
                      (println (getf d :name) "flies on" (getf v :name)))
          nil))

    (is (not=
          (def-support :before ride [(Driver d) (LandVehicle v)]
                       (println "Fuel the tank"))
          nil))

    (is (not=
          (def-support :after ride [(Driver d) (LandVehicle v)]
                       (println "Turn on alarm"))
          nil))

    (is (not=
          (def-support :before ride [(Driver d) (FloatingVehicle v)]
                       (println "Set sails"))
          nil))

    (is (not=
          (def-support :after ride [(Driver d) (FloatingVehicle v)]
                       (println "Take in sails"))
          nil))

    (is (not=
          (def-support :before ride [(Driver d) (FlyingVehicle v)]
                       (println "Check parachute"))
          nil))

    (is (not=
          (def-support :after ride [(Driver d) (FlyingVehicle v)]
                       (println "Be happy with successful landing"))
          nil))

    (is (not=
          (def-support :before ride [(Driver d) (Armour v)]
                       (println "Load ammunition"))
          nil))

    (is (not=
          (def-support :after ride [(Driver d) (Armour v)]
                       (println "Leave vehicle without hurts"))
          nil))

    (is (not=
          (def-support :around ride [(Driver d) (Vehicle v)]
                       (println "Start observing:")
                       (let [result (if (can-ride [d v])
                                      (call-next-method)
                                      (println (getf d :name) "cannot ride on" (getf v :name)))]
                         (println "Finish observing;")
                         result))
          nil))

    (is (not=
          (def-support :around ride [(AnimalDriver d) (SimpleVehicle v)]
                       (println "Allow" (getf d :name) "to ride")
                       (let [result (call-next-method)]
                         (println "We allowed" (getf d :name) "to ride and it was all okay")
                         result))
          nil))

    )


  (testing "new-inst"

    (is (not=
          (def t-90 (new-inst Armour :name "T-90"))
          nil))
    (is (not=
          (def my-bicycle (new-inst Bicycle :name "My bicycle"))
          nil))
    (is (not=
          (def il-86 (new-inst Plane :name "Il-86"))
          nil))

    (is (not=
          (def monkey (new-inst AnimalDriver :name "Monkey"))
          nil))
    (is (not=
          (def anonymous (new-inst HumanDriver :name "Anonymous"))
          nil))
    (is (not=
          (def pirx (new-inst Pilot :name "Pirx"))
          nil))

    (is (is-instance? t-90))
    (is (is-instance? my-bicycle))
    (is (is-instance? il-86))
    (is (is-instance? monkey))
    (is (is-instance? anonymous))
    (is (is-instance? pirx))

    (is (= '("T-90" 124 "sail" 122 "move on land" 124) (capabilities [t-90] 123)))

    (is (= '("My bicycle" 112 "be driven by anyone" 112 "move on land" 112) (capabilities [my-bicycle] 111)))

    (is (= '("Il-86" 778 "fly" 776) (capabilities [il-86] 777)))

    )


  (testing "get/set field"

    (is (not=
          (def t-70 (new-inst Armour :name "T-90"))
          nil))

    (is (= (getf t-70 :name) "T-90"))

    (is (not= (setf t-70 :name "T-70") nil))

    (is (= (getf t-70 :name) "T-70"))

    )

  (testing "can-ride"
    (is (false? (can-ride [monkey t-90])))
    (is (false? (can-ride [monkey il-86])))
    (is (true? (can-ride [monkey my-bicycle])))


    (is (true? (can-ride [anonymous t-90])))
    (is (false? (can-ride [anonymous il-86])))
    (is (true? (can-ride [anonymous my-bicycle])))


    (is (true? (can-ride [pirx t-90])))
    (is (true? (can-ride [pirx il-86])))
    (is (true? (can-ride [pirx my-bicycle])))

    (ride [monkey t-90])
    (ride [monkey il-86])
    (ride [monkey my-bicycle])
    (println)

    (ride [anonymous t-90])
    (ride [anonymous il-86])
    (ride [anonymous my-bicycle])
    (println)

    (ride [pirx t-90])
    (ride [pirx il-86])
    (ride [pirx my-bicycle])

    )
  )