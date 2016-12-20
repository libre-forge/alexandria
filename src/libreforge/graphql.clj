(ns libreforge.graphql
  (:require
   [cheshire.core :as json]
   [graphql-clj.parser :as parser]
   [graphql-clj.type :as type]
   [graphql-clj.validator :as validator]
   [graphql-clj.executor :as executor]
   [libreforge.util.uuid :as uuid]
   [libreforge.users.graphql :as users]))

(def schema-str "
  type User {
    id: String
    name: String
    email: String
  }

  type Course {
    id: String
    title: String
    createdAt: String
    createdBy: User
  }

  type Theme {
    id: String
    title: String
    course: Course
    createdAt: String
    createdBy: User
  }

  type QueryRoot {
    user(email: String): User
    users: [User]
    course: Course
    theme: Theme
  }

  schema {
    query: QueryRoot
  }")

(def type-schema
  (-> schema-str parser/parse validator/validate-schema))

(defn dispatch
  [type-name field-name]
  (cond
    (and (= "QueryRoot" type-name) (= "user" field-name)) users/find-by-email
    (and (= "QueryRoot" type-name) (= "users" field-name)) users/list-all))

(def context nil)

(defn resolve
  [context, query-str var-str]
  (let [variables (json/parse-string var-str)
        query (-> query-str
                  parser/parse
                  (validator/validate-statement type-schema))]
    (executor/execute context type-schema dispatch query variables)))
