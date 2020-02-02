(in-ns 'object-model.core)


(declare get-classes-from-graphs)
(declare cyclic-inc)
(declare cyclic-dec)
(declare indices-edge-values)

(declare perform-suitable-around)


(defn bfs-classes [class]
  (loop [
         acc (list class)
         queue acc
         ]
    (let [
          head (first queue)
          supers (super-class head)
          ]

      (if (empty? queue) acc
                         (recur (concat acc supers)         ;acc
                                (concat (rest queue) supers) ;queue
                                )
                         )
      )
    )
  )


(defmacro def-multi-method
  "This macro declares a multi-method ~name."
  [name]

  `(let [primary_table# (ref {})
         around_table# (ref {})
         before_table# (ref {})
         after_table# (ref {})
         objs# nil                                          ; stub for name resolving below
         args# nil                                          ; stub for name resolving below
         ]
     (defn ~name [objs# & args#]


       ;(println "def-multi-method: " args#)
       (if (is-instance? (first objs#))
         (let [classes# (map instance-class objs#)

               ;; For each parameter class we build a Breadth-first search graph of its predecessors.
               ;; So we have a list of graphs.
               BFS_graphs_not_uniq# (map bfs-classes classes#)

               ;; For each graph we make all its classes-vertices distinct and
               ;; remove all {BaseObjectName} entries.
               BFS_graphs# (map (fn [graph#] (distinct (remove #(= % BaseObjectName) graph#)))
                                BFS_graphs_not_uniq#)

               graphs_count# (count BFS_graphs#)


               max_indices_values# (map #(dec (count %)) BFS_graphs#) ; bottom of the hierarchy
               cur_indices_values# (repeat graphs_count# 0) ; start from top of the hierarchy

               ]
           ; (println BFS_graphs# max_indices_values# graphs_count#)
           ; (println @around_table# @before_table# @primary_table# @after_table#)

           ; (println cur_indices_values# max_indices_values#)

           (apply perform-suitable-around
                  (concat (list around_table# before_table# primary_table# after_table#
                                BFS_graphs# cur_indices_values# max_indices_values# cyclic-inc objs#) args#))
           )

         (if (seq args#)
           (let [support_type# (first args#)]
             (cond
               (= support_type# :around)
               (dosync (alter around_table# assoc (first objs#) (second objs#)))

               (= support_type# :before)
               (dosync (alter before_table# assoc (first objs#) (second objs#)))

               (= support_type# :after)
               (dosync (alter after_table# assoc (first objs#) (second objs#)))

               :default (assert nil "Incorrect type of support.")))
           ;; we add a new version of ~name multi-method to its virtual table.
           (dosync (alter primary_table# assoc (first objs#) (second objs#)))
           )
         )
       )
     )
  )

(defmacro def-method-helper
  "This macro defines a particular version of the multi-method {name}."
  [type name arguments & body]
  ":{before|after|around|nil} super-method [(SuperClass sc) (PuperClass pc) arg1 arg2 & other-arg] body"
  (let [
        all_args# (vec (map #(if (seq? %) (second %) %) arguments)) ; [sc pc arg1 arg2 & other-arg]

        classes_objs# (filter #(seq? %) arguments)          ; (SuperClass sc) (PuperClass pc)
        objs# (map #(second %) classes_objs#)               ; (sc pc)
        classes# (map #(gen-name-fn (first %)) classes_objs#) ; (SuperClass PuperClass)


        ;t# (println name args# classes# body)
        ]

    (assert (= objs# (distinct objs#)) (str "Identical names of the arguments of method " name "."))
    (if (keyword? type)

      `(~name ['~classes# (fn ~all_args# ~@body)] '~type)

      `(~name ['~classes# (fn ~all_args# ~@body)])
      )

    )

  )

(defmacro def-method
  [name arguments & body]
  `(def-method-helper nil ~name ~arguments ~@body)
  )



(defmacro def-support [type name arguments & body]
  "This macro defines support method to corresponding multi-method {name}
   with specified type: around, before or after"
  `(def-method-helper ~type ~name ~arguments ~@body)
  )

;; stub for call-next-method that is defined each time in perform-suitable-command using 'binding.
(def ^:dynamic call-next-method nil)


(defn perform-suitable-command
  "perform-suitable-command performs the multi-method whose virtual versions are all kept in vtable.
   It is recursive and can be called explicitly by a user with (call-next-method ...) construction."
  [v_table BFS_graphs indices indices_to indices-changer objs & args]
  (let [
        classes (get-classes-from-graphs BFS_graphs indices)

        indices_next (indices-changer indices indices_to)
        indices_edge (indices-edge-values indices-changer indices_to)

        perform-suitable-command-next (partial perform-suitable-command
                                               v_table
                                               BFS_graphs
                                               indices_next
                                               indices_to
                                               indices-changer
                                               objs)
        ]
    ;(println "perform-suitable-command:" indices (indices-edge-values indices-changer indices_to) classes)
    (cond
      (contains? v_table classes)
      (binding [call-next-method
                (if (= indices indices_edge)
                  (fn [& _] (assert nil (str "(call-next-method) can not be called from a method if"
                                             "the classes of ALL its arguments are base.")))

                  (fn
                    ; this trick allow (call-next-method)
                    ; without explicit passing params to parent
                    ([& args_passed] (apply perform-suitable-command-next args_passed))
                    ([] (apply perform-suitable-command-next args)))

                  )
                ]

        (dosync (apply (v_table classes) (concat objs args))))

      (= indices indices_edge)
      (assert nil "No suitable method found.")

      :default
      (apply perform-suitable-command-next args)

      )
    )

  )

(defn perform-suitable-before-after
  "Performs before/after support methods contained in v_table. They can not be called explicitly."
  [v_table BFS_graphs indices indices_to indices-changer objs & args]
  (let [
        classes (get-classes-from-graphs BFS_graphs indices)
        indices_next (indices-changer indices indices_to)
        ]
    (binding [call-next-method (fn [& _] (assert nil "(call-next-method) is not allowed in :before :after"))]
      (when (contains? v_table classes)
        (dosync (apply (v_table classes) (concat objs args)))))

    (when (not (= indices (indices-edge-values indices-changer indices_to)))
      (apply perform-suitable-before-after (concat (list v_table
                                                         BFS_graphs
                                                         indices_next
                                                         indices_to
                                                         indices-changer
                                                         objs) args))))
  )



(defn perform-suitable-around
  "Performs support around-methods contained in around_table.
   Each next around-method can be called with (call-next-method).
   When all the around-methods are performed primary-methods can be called with (call-next-method)."
  [around_table before_table primary_table after_table
   BFS_graphs indices indices_to indices-changer objs & args]
  (let [classes (get-classes-from-graphs BFS_graphs indices)
        graphs_number (count BFS_graphs)

        before-primary-after                                ;fn if around table is empty or can't find suitable method
        (fn [& args1]

          (let [
                top_indices (repeat graphs_number 0)


                before_call
                (if (seq @before_table)
                  (apply perform-suitable-before-after (concat (list @before_table
                                                                     BFS_graphs
                                                                     top_indices
                                                                     indices_to
                                                                     cyclic-inc
                                                                     objs) args1)) nil)

                primary_call
                (if (seq @primary_table)
                  (apply perform-suitable-command (concat (list @primary_table
                                                                BFS_graphs
                                                                top_indices
                                                                indices_to
                                                                cyclic-inc
                                                                objs) args1)) nil)

                after_call
                (if (seq @after_table)
                  (apply perform-suitable-before-after (concat (list @after_table
                                                                     BFS_graphs
                                                                     indices_to
                                                                     indices_to
                                                                     cyclic-dec
                                                                     objs) args1)) nil)

                primary_result (second (list before_call primary_call after_call))
                ]
            ;(println args args1)
            primary_result))

        indices_next (indices-changer indices indices_to)
        indices_edge (indices-edge-values indices-changer indices_to)
        ]
    ;(println "perform-suitable-around: " indices classes)
    (if (seq @around_table)
      (if (contains? @around_table classes)
        (binding [call-next-method
                  (if (= indices indices_edge)
                    before-primary-after
                    (partial perform-suitable-around
                             around_table
                             before_table
                             primary_table
                             after_table
                             BFS_graphs
                             indices_next
                             indices_to
                             indices-changer
                             objs))
                  ]
          (dosync (apply (around_table classes) (concat objs args)))
          )
        (if (= indices indices_edge)                        ; around table does not contains classes
          (apply before-primary-after args)                 ; and it is time to call primary
          (apply perform-suitable-around                    ; here we try to find more suitable method
                 (concat (list around_table
                               before_table
                               primary_table
                               after_table
                               BFS_graphs
                               indices_next
                               indices_to
                               indices-changer
                               objs) args)))
        )
      (apply before-primary-after args)                     ; around table empty and it is time to call primary
      )
    )

  )



(defn get-classes-from-graphs
  "Gets a particular set of classes from BFS_graphs corresponding to the indices vector.
  Each index means a class position in a BFS-graph that is a hierarchy of classes."
  [BFS_graphs indices]
  (let [graphs_number (count BFS_graphs)]
    (loop [i (dec graphs_number)
           classes '()]
      (if (< i 0)
        classes
        (let [graph (nth BFS_graphs i)
              index (nth indices i)]
          (recur (dec i) (conj classes (nth graph index))))))))


(defn indices-edge-values
  [indices_changer max_indices_values]

  (if (= indices_changer cyclic-inc) max_indices_values
                                     (repeat (count max_indices_values) 0))
  )


(defn cyclic-inc
  "Increments the indices vector.
  '(0 0) '(1 1) -> '(0 1)
  '(0 1) '(1 1) -> '(1 0)
  ..
  '(1 1) '(1 1) -> '(0 0)
  ...
  '(3 8) '(3 8) -> '(0 0)
  "
  [current_indices max_indices_values]
  {:pre [(= (count current_indices) (count max_indices_values))]}

  (loop [c_i current_indices
         m_i_v max_indices_values
         skipped '()]
    (let [
          index (last c_i)
          max_index (last m_i_v)
          ]

      (cond
        (empty? c_i) skipped

        (< index max_index) (concat (drop-last c_i) [(inc index)] skipped)

        :default (recur (drop-last c_i) (drop-last m_i_v) (concat skipped '(0)))
        )
      )
    )
  )


(defn cyclic-dec
  "Decrements the indices vector.
  '(0 0) '(1 1) -> '(1 1)
  '(0 1) '(1 1) -> '(0 0)
  ..
  '(1 1) '(1 1) -> '(1 0)
  ...
  '(3 8) '(3 8) -> '(3 7)
  "
  [current_indices max_indices_values]
  {:pre [(= (count current_indices) (count max_indices_values))]}

  (loop [c_i current_indices
         m_i_v max_indices_values
         skipped '()]
    (let [
          index (last c_i)
          max_index (last m_i_v)
          ]

      (cond
        (empty? c_i) skipped

        (> index 0) (concat (drop-last c_i) [(dec index)] skipped)

        :default (recur (drop-last c_i) (drop-last m_i_v) (concat (list max_index) skipped))
        )
      )
    )
  )


