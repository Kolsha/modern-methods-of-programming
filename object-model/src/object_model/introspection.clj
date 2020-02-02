(in-ns 'object-model.core)

(defn getf
  "This is the getter common for all classes."
  [obj field]
  (let [state (obj ::state)]
    (assert (contains? state field) "getf: no such field.")
    (deref (state field))
    )
  )



(defn setf
  "This is the setter common for all classes."
  [obj field new_value]
  (let [state (obj ::state)]
    (assert (contains? state field))
    (dosync (ref-set (state field) new_value))
    )
  )




(defn instance-class
  "Returns the class name of the instance."
  [instance]
  (instance ::class))

(defn is-instance?
  "Returns true if obj is an instance of some class."
  [obj]
  (and (map? obj) (contains? obj ::class)))