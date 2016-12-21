(ns libreforge.courses.graphql
  (:require [libreforge.courses.services :as courses]
            [libreforge.util.uuid :as uuid]))

(defn list-all
  [context parent args]
  (courses/list-all))

(defn create
  [context parent args]
  (let [input (get args "course")
        pitch (get input "pitch")
        member_limit (get input "member_limit")
        title (get input "title")
        description (get input "description")
        themes (get input "themes")
        id (uuid/random)]
        (courses/create {:title title
                         :pitch pitch
                         :description description
                         :member_limit member_limit
                         :themes themes})))
