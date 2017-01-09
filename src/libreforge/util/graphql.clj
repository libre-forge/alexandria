(ns libreforge.util.graphql
  (:require
   [libreforge.util.io :as io]
   [cheshire.core :as json]
   [graphql-clj.executor :as executor]
   [graphql-clj.parser :as parser]
   [graphql-clj.type :as type]
   [graphql-clj.validator :as validator]))

;; ##############################
;; ## Pipeline Response Types ###
;; ##############################

;; Pipeline objetive is to apply set of rules to a given set of
;; mappings. That would make it easier to apply, for instance
;; authorization policies to a specific set of mappings, or maybe to
;; add extra data to a subset of mappings.
;;
;; Pipeline will handle three types of function response:
;;
;; - Response: normal response. Ends pipeline.
;; - ResponseError: error response. Ends pipeline.
;; - Delegate: Adds data to context and make it available to further
;; functions in the pipeline
;;

(defrecord Response [data])
(defrecord ResponseError [errors])
(defrecord Delegate [data])

(defn response
  "builds a pipeline response"
  [data]
  (Response. data))

(defn error
  "builds a pipeline error"
  [error]
  (let [errors [error]]
    (ResponseError. errors)))

(defn errors
  "builds a set of pipeline errors"
  [errors]
  (ResponseError. errors))

(defn delegate
  "builds a pipeline delegate"
  ([] (Delegate. {}))
  ([data] (Delegate. data)))

(defn is-response
  "whether the result is a Response or not"
  [r]
  (= (type r) Response))

(defn is-error
  "whether the result is a ResponseError or not"
  [r]
  (= (type r) ResponseError))

(defn is-delegate
  "whether the result is a Delegate or not"
  [r]
  (= (type r) Delegate))

(defn execute-pipeline
  "compose all functions in the pipeline returning the combined outcome"
  [fs env]
  (reduce (fn [acc f]
            (let [r (f acc)]
              (cond
                (is-response r) (reduced r)
                (is-error r) (throw (ex-info "" r))
                (is-delegate r) (update-in acc [:ctx] merge (:data r))
                :else (reduced (response r))))) env fs))

;; ##############################
;; ## GraphQL mappings building #
;; ##############################

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
  "loads schema from classpath"
  [class-path]
  (let [schema-str (io/read-resource class-path)]
    (-> schema-str
        parser/parse
        validator/validate-schema)))

(defn to-env
  "combines context parent and arguments in an environment map"
  [ctx parent args]
  {:ctx ctx
   :parent parent
   :args (clojure.walk/keywordize-keys args)})

(defn build-graphql-fn
  "converts a graphql-clj call to a env-response function execution"
  [fns]
  (fn [ctx parent args]
    (let [env (to-env ctx parent args)]
      (:data (execute-pipeline fns env)))))

(defn create-mappings
  "builds a dispatcher from the routing information"
  [routes]
  (let [decision-tree (parse-routes routes)]
    (fn [type field]
      (let [pth (map keyword [type field])
            fns (get-in decision-tree pth)]
        (if (nil? fns)
          nil
          (build-graphql-fn fns))))))

(defn create-resolver
  "Creates a function resolver depending on schema and mappings"
  [schema mappings]
  (fn [ctx query vars]
    (executor/execute ctx schema mappings query vars)))
