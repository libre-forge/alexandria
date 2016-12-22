(ns libreforge.subjects.graphql
  (:require [libreforge.util.uuid :as uuid]
            [libreforge.subjects.services :as subjects]))

(defn list-by-course
  "list all subjects by course"
  [context parent args]
  (let [:course (->(get args "course")
                   (uuid/from-string))]
    (subjects/list-by-course course)))
