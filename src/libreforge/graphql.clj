(ns libreforge.graphql
  (:require
   [libreforge.util.graphql :as graphql]
   [libreforge.users.graphql :as users]
   [libreforge.subjects.graphql :as subjects]
   [libreforge.courses.graphql :as courses]
   [libreforge.resources.graphql :as resources]))

(def routes
  "GraphQL routing definition"
  [[:QueryRoot
    [:user users/find-by-email]
    [:course courses/by-id]
    [:courses courses/list-all]
    [:subject subjects/by-id]]
   ;; Relationships
   [:Course
    [:created_by courses/owner]
    [:subjects courses/subjects]
    [:members courses/members]]
   [:Subject
    [:resources subjects/resources]
    [:created_by subjects/owner]]
   ;; Mutations
   [:MutationRoot
    [:course courses/create]
    [:subject subjects/create]
    [:login users/login]]])

(def mappings (graphql/create-mappings routes))
(def schema (graphql/load-schema "graphql/schema.gql"))
(def resolve (graphql/create-resolver schema mappings))
