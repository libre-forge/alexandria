(ns libreforge.courses.graphql
  (:require [libreforge.courses.services :as courses]
            [libreforge.util.uuid :as uuid]))

(defn list-all
  [context parent args]
  (courses/list-all))

(defn create
  [context parent args]
  (let [input (get args "course")
        title (get input "title")
        description (get input "description")
        id (uuid/random)]
    (courses/create id title description)))
