(ns libreforge.subjects.graphql
  (:require [libreforge.util.uuid :as uuid]
            [libreforge.users.services :as users]
            [libreforge.subjects.services :as subjects]))

(defn list-by-course
  "list all subjects by course"
  [context parent args]
  (let [course (uuid/from-string (:course args))]
    (subjects/list-by-course course)))

(defn by-id
  "returns a specific subject detail"
  [context parent args]
  (let [id (uuid/from-string (:id args))]
    (subjects/by-id id)))

(defn owner
  "returns the subject owner info"
  [context parent args]
  (let [subject (:id parent)]
    (users/load-creator-of :subject subject)))
