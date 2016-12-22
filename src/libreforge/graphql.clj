(ns libreforge.graphql
  (:require
   [graphql-clj.parser :as parser]
   [graphql-clj.type :as type]
   [graphql-clj.validator :as validator]
   [graphql-clj.executor :as executor]
   [cheshire.core :as json]
   [catacumba.http :as http]
   [libreforge.util.http :as http-util]
   [libreforge.util.uuid :as uuid]
   [libreforge.util.io :as io]
   [libreforge.users.graphql :as users]
   [libreforge.subjects.graphql :as subjects]
   [libreforge.courses.graphql :as courses]))

(def schema-str
  "loads this application's GraphQL schema"
  (io/read-resource "graphql/schema.gql"))

(def type-schema
  "returns a validated GraphQL schema"
  (-> schema-str
      parser/parse
      validator/validate-schema))

(defn dispatch
  "dispatches incoming requests to the correspondent GraphQL handler"
  [type-name field-name]
  (cond
    (and (= "QueryRoot" type-name) (= "user" field-name)) users/find-by-email
    (and (= "QueryRoot" type-name) (= "users" field-name)) users/list-all
    (and (= "QueryRoot" type-name) (= "courses" field-name)) courses/list-all
    (and (= "QueryRoot" type-name) (= "course" field-name)) courses/by-id
    (and (= "QueryRoot" type-name) (= "subjects" field-name)) subjects/list-by-course
    (and (= "MutationRoot" type-name) (= "login" field-name)) users/login
    (and (= "MutationRoot" type-name) (= "join" field-name)) courses/join
    (and (= "MutationRoot" type-name) (= "login" field-name)) users/login))

(def context nil)

(defn restore
  "by default json handler converts keys in keywords and GraphQL
  variable keys need to get to the backend unaltered"
  [m]
  (let [json-str (json/generate-string m)
        json-map (json/parse-string json-str)]
    json-map))

(defn resolve
  "validates and executes GraphQL query"
  [ctx query variables]
  (let [vars (restore variables)]
    (executor/execute ctx type-schema dispatch query vars)))

(defn mutations
  "GraphQL endpoint"
  [context]
  (let [query (:query (:data context))
        vars (:variables (:data context))
        result (resolve nil query vars)]
    (-> (json/encode result)
        (http/ok {:content-type http-util/json-type}))))
