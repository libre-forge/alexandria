(ns libreforge.graphql
  (:require
   [libreforge.util.graphql :as graphql]
   [libreforge.users.graphql :as users]
   [libreforge.auth.graphql :as auth]
   [libreforge.subjects.graphql :as subjects]
   [libreforge.courses.graphql :as courses]
   [libreforge.resources.graphql :as resources]))

(def routes
  "GraphQL routing definition"
  [[auth/add-user-info]
   [:QueryRoot
    [:course courses/by-id]
    [:courses courses/list-all]
    [:subject subjects/by-id]
    [:user [auth/assert-logged-in users/find-by-email]]]
   ;; Relationships
   [:Course
    [:created_by courses/owner]
    [:subjects courses/subjects]
    [:members courses/members]]
   [:Subject
    [:resources subjects/resources]
    [:created_by subjects/owner]]
   [:Resource
    [:created_by resources/owner]]
   ;; Mutations
   [:MutationRoot
    [:login auth/login]
    [auth/assert-logged-in]
    [:course courses/create]
    [:join courses/join]
    [:subject subjects/create]
    [:resource resources/create]]])

(def mappings (graphql/create-mappings routes))
(def schema (graphql/load-schema "graphql/schema.graphql"))
(def resolve (graphql/create-resolver schema mappings))
