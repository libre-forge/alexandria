(ns libreforge.graphql
  (:require
   [cheshire.core :as json]
   [graphql-clj.parser :as parser]
   [graphql-clj.type :as type]
   [graphql-clj.validator :as validator]
   [graphql-clj.executor :as executor]
   [cheshire.core :as json]
   [catacumba.http :as http]
   [libreforge.util.http :as http-util]
   [libreforge.util.uuid :as uuid]
   [libreforge.users.graphql :as users]
   [libreforge.courses.graphql :as courses]))

(def schema-str "
  input Credentials {
     username: String
     password: String
  }

  type Login {
    token: String
  }

  type User {
    id: String
    name: String
    email: String
  }

  type Course {
    id: String
    title: String
    description: String
    createdAt: String
  }

  type QueryRoot {
    user(email: String): User
    users: [User]
    courses: [Course]
  }

  type MutationRoot {
     login(credentials: Credentials): Login
  }

  schema {
    query: QueryRoot,
    mutation: MutationRoot
  }")

(def type-schema
  (-> schema-str parser/parse validator/validate-schema))

(defn dispatch
  [type-name field-name]
  (cond
    (and (= "QueryRoot" type-name) (= "user" field-name)) users/find-by-email
    (and (= "QueryRoot" type-name) (= "users" field-name)) users/list-all
    (and (= "QueryRoot" type-name) (= "courses" field-name)) courses/list-all
    (and (= "MutationRoot" type-name) (= "login" field-name)) users/login))


(def context nil)

(defn resolve
  [context, query-str var-str]
  (let [variables (json/parse-string var-str)
        query (-> query-str
                  parser/parse
                  (validator/validate-statement type-schema))]
    (executor/execute context type-schema dispatch query variables)))


(defn queries
  "GraphQL endpoint"
  [context]
  (let [query (:query (:data context))
        vars (:variables (:query-params context))
        result (resolve nil query vars)]
    (-> (json/encode result)
        (http/ok {:content-type http-util/json-type}))))

(defn mutations
  "GraphQL endpoint"
  [context]
  (let [query (:query (:query-params context))
        vars (:variables (:query-params context))
        result (resolve nil query vars)]
    (-> (json/encode result)
        (http/ok {:content-type http-util/json-type}))))
