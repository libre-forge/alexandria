(ns libreforge.users.graphql
  (:require
   [graphql-clj.parser :as parser]
   [graphql-clj.type :as type]
   [graphql-clj.validator :as validator]
   [graphql-clj.executor :as executor]
   [libreforge.util.uuid :as uuid]
   [libreforge.users.services :as services]))

(def schema-str "
  type User {
    name: String
    email: String
  }

  type QueryRoot {
    user: User
  }

  schema {
    query: QueryRoot
  }")

(def type-schema
  (-> schema-str parser/parse validator/validate-schema))

(defn user-by-id
  [context parent args]
  (println (str "" parent "|" args "|" context))
  {:name "John Doe"
   :email "john.doe@gmail.com"})

(defn dispatch
  [type-name field-name]
  (cond
    (and (= "QueryRoot" type-name) (= "user" field-name)) user-by-id
    (and (= "QueryRoot" type-name) (= "course" field-name)) user-by-id))

(def context nil)

(defn resolve
  [context, query-str]
  (let [query (-> query-str
                  parser/parse
                  (validator/validate-statement type-schema))]
    (println query)
    (executor/execute context type-schema dispatch query)))
