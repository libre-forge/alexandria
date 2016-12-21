(ns libreforge.courses.graphql
  (:require [libreforge.courses.services :as courses]
            [libreforge.util.uuid :as uuid]))

(defn list-all
  [context parent args]
  (let [filter (get args "filter")
        result (courses/list-all filter)]
    result))

(defn by-id
  [context parent args]
  (let [id (uuid/from-string (get args "id"))]
    (courses/by-id id)))

(defn create
  [context parent args]
  (let [input (get args "course")
        pitch (get input "pitch")
        member_limit (get input "member_limit")
        title (get input "title")
        description (get input "description")
        subjects (get input "subjects")
        id (uuid/random)]
        (courses/create {:title title
                         :pitch pitch
                         :description description
                         :member_limit member_limit
                         :subjects subjects})))
