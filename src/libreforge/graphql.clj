(ns libreforge.graphql
  (:require
   [libreforge.util.graphql :as graphql]
   [libreforge.users.graphql :as users]
   [libreforge.subjects.graphql :as subjects]
   [libreforge.courses.graphql :as courses]))

(def routes
  "GraphQL routing definition"
  [[:QueryRoot
    [:users users/find-by-email]
    [:course courses/by-id]
    [:courseOwner courses/owner]
    [:courseMembers courses/members]
    [:courses courses/list-all]
    [:subject subjects/by-id]
    [:subjects subjects/list-by-course]
    [:users users/list-all]]
   [:MutationRoot
    [:course courses/create]
    [:login users/login]]])

(def mappings (graphql/create-mappings routes))
(def schema (graphql/load-schema "graphql/schema.gql"))
(def resolve (graphql/create-resolver schema mappings))
