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
    [:users users/find-by-email]
    [:course courses/by-id]
    [:courseMembers courses/members]
    [:courses courses/list-all]
    [:subject subjects/by-id]
    [:subjectsByCourse subjects/list-by-course]
    [:users users/list-all]]
   ;; Relationships
   [:Course
    [:created_by courses/owner]]
   [:Subject
    [:resources resources/list-by-subject]
    [:created_by subjects/owner]]
   ;; Mutations
   [:MutationRoot
    [:course courses/create]
    [:login users/login]]])

(def mappings (graphql/create-mappings routes))
(def schema (graphql/load-schema "graphql/schema.gql"))
(def resolve (graphql/create-resolver schema mappings))
