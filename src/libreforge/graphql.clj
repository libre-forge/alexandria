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
   [libreforge.users.graphql :as users]
   [libreforge.courses.graphql :as courses]))

(def schema-str "
  input Credentials {
     username: String
     password: String
    __typename:String
  }

  input CreateCourse {
     title: String
     pitch: String
     description: String
     member_limit: Int
     subjects: [String!]
    __typename:String
  }

  type Login {
    token: String
    __typename:String
  }

  type User {
    id: String
    name: String
    email: String
    __typename:String
  }

  type Course {
    id: String
    title: String
    pitch: String
    description: String
    member_count: Int
    member_limit: Int
    members: [User]
    created_at: String
    created_by: String
    __typename:String
  }

  type QueryRoot {
    user(email: String): User
    users: [User]
    courses: [Course]
    course(id: String): Course
  }

  type MutationRoot {
     login(credentials: Credentials): Login
     course(course: CreateCourse): Course
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
    (and (= "QueryRoot" type-name) (= "course" field-name)) courses/by-id
    (and (= "MutationRoot" type-name) (= "login" field-name)) users/login
    (and (= "MutationRoot" type-name) (= "course" field-name)) courses/create))


(def context nil)

(defn restore
  [m]
  (let [json-str (json/generate-string m)
        json-map (json/parse-string json-str)]
    json-map))

(defn resolve
  "validates and executes GraphQL query"
  [context query variables]
  (let [vars (restore variables)]
    (executor/execute context type-schema dispatch query vars)))

(defn mutations
  "GraphQL endpoint"
  [context]
  (let [query (:query (:data context))
        vars (:variables (:data context))
        result (resolve nil query vars)]
    (-> (json/encode result)
        (http/ok {:content-type http-util/json-type}))))
