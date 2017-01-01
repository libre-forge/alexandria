(ns libreforge.util.graphql
  (:require
   [libreforge.util.io :as io]
   [cheshire.core :as json]
   [graphql-clj.executor :as executor]
   [graphql-clj.parser :as parser]
   [graphql-clj.type :as type]
   [graphql-clj.validator :as validator]))

(defn update-fns
  "updates current aggregated functions to the pipeline"
  [m fns]
  (update-in m [:fns] concat fns))

(defn collect-mappings
  "collect pair of mappings/functions for a given type field"
  [acc val]
  (let [field (first val)
        fns (:fns acc)]
    (if (not (keyword? field))
      (update-fns acc val)
      (assoc acc field (concat fns (flatten [(rest val)]))))))

(defn parse-type
  "collects mappings for a given type"
  [type mfns]
  (let [type-name (first type)
        fields (rest type)
        mappings (reduce collect-mappings mfns fields)]
    (update-in {type-name mappings} [type-name] dissoc :fns)))

(defn parse-routes
  "parses vector entries passed as parameter as graphql decision tree"
  [routes]
  (reduce (fn [acc val]
            (let [type-or-fn (first val)
                  general-fn (:fns acc)]
              (if (not (keyword? type-or-fn))
                (update-fns acc [type-or-fn])
                (merge acc (parse-type val {:fns general-fn}))))) {} routes))

(defn load-schema
  [class-path]
  (let [schema-str (io/read-resource class-path)]
    (-> schema-str
        parser/parse
        validator/validate-schema)))

(defn keywordize-args
  [fnx]
  (if (not (nil? fnx))
    (fn [ctx parent args]
      (fnx ctx parent (clojure.walk/keywordize-keys args)))
    fnx))

(defn create-mappings
  "builds a dispatcher from the routing information"
  [routes]
  (let [decision-tree (parse-routes routes)]
    (fn [type field]
      (let [pth (map keyword [type field])
            fns (get-in decision-tree pth)
            ;; #TODO (first should be transformed in a execution pipeline)
            fnx (keywordize-args (first fns))]
        fnx))))

(defn create-resolver
  [schema mappings]
  (fn [ctx query vars]
    (executor/execute ctx schema mappings query vars)))
