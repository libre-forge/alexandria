(ns libreforge.subjects.graphql
  (:require [libreforge.util.uuid :as uuid]
            [libreforge.subjects.services :as subjects]))

(defn list-by-course
  "list all subjects by course"
  [context parent args]
  (let [course (uuid/from-string (get args "course"))]
    (subjects/list-by-course course)))

(defn by-id
  [context parent args]
  (let [id (uuid/from-string (get args "id"))
        subject (subjects/by-id id)]
    subject))
