(ns object-model.core

  )



(defn gen-name-fn [sym]

  (cond
    (nil? sym) sym
    (keyword? sym) sym
    :default (keyword ":" (name sym))
    )
  )




(defmacro gen-name [sym]
  "convert-sym-to-keyword"
  `(gen-name-fn '~sym)
  )



(def ^:const BaseObjectName (gen-name BaseObject))


(def classes-hierarchy
  "Storage of a hierarchy of classes."
  (ref {BaseObjectName {::super  nil
                        ::fields {}
                        ::init   nil}}))
(def ^:const BaseObject (get @classes-hierarchy BaseObjectName))



(defn super-class
  "Returns a super class of the class."
  [class]
  (get (get @classes-hierarchy (gen-name-fn class)) ::super)
  )


(defn init [field value & map]
  "The function allows a user to fill in the init section of a class when defining it with def-class."
  {:init (apply hash-map field value map)}
  )

(defmacro def-class [name supers fields & sections]
  "This macro creates a class declaration."
  (let [

        sections (apply merge (map eval sections))
        init (get sections :init)


        supers_clear
        (remove #(or (= % 'BaseObjectName) (= % 'BaseObject)) (distinct supers))

        supers
        (if (empty? supers_clear)
          (list BaseObjectName)
          (map gen-name-fn supers_clear)
          ;possible check super class exist in hierarchy
          )
        ;name (if (and (resolve 'name) (fn? name)) (name) name)
        ]

    `(let [
           class_name# (gen-name ~name)
           super# '~supers

           fields# (distinct '~fields)]

       (dosync

         (assert (not (contains? @classes-hierarchy class_name#))
                 (format "Class [%s] already exist." '~name))

         (doseq [super_name# super#]
           ;(println super_name#)
           (assert (contains? @classes-hierarchy super_name#)
                   (format "Super class [%s] does not exist." super_name#)
                   ))

         (alter classes-hierarchy assoc class_name# {::super  super#
                                                     ::fields fields#
                                                     ::init   '~init})
         )
       )
    )
  )







(defn get-all [key class]
  "Extracts all {key} of the class including those of the predecessor classes."
  (let [

        self_call (partial get-all key)

        class_name (gen-name-fn class)
        class_def (get @classes-hierarchy class_name)       ;here we ignore if class does not exist, but we can check it
        class_fields (get class_def key)
        supers (super-class class_name)

        result (cond
                 ; return merge self fields and supers
                 (and (seq supers) (seq class_fields))
                 (concat

                   (apply concat (map self_call supers))

                   class_fields

                   )
                 ; return only self
                 (seq class_fields)
                 class_fields

                 ; return supers fields
                 (seq supers)
                 (apply concat (map self_call supers))

                 ; return ()
                 :default (list))

        ]

    (distinct result)
    )
  )



(defmacro new-inst [class & values]
  `(let [
         class_name# (gen-name ~class)
         fields_value# (if (empty? '~values) '() (list ~@values))
         ;all_fields# (get-all ::fields class_name#)
         all_inits# (into {} (get-all ::init class_name#))
         fields_value_map# (merge all_inits# (apply hash-map fields_value#))
         state# (into {} (map (fn [[k# v#]] {k# (ref v#)}) fields_value_map#))
         ]


     ;(assert (some #(filter all_fields#) (keys fields_value_map#)) "new-inst: wrong fields.")
     {::class class_name#, ::state state#}
     ;(println all_fields# (keys fields_value_map#))
     )
  )

(load "introspection")
(load "methods")
