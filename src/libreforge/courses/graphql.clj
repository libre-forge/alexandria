(ns libreforge.courses.graphql
  (:require [libreforge.courses.services :as courses]))

(defn list-all
  [context parent args]
  (courses/list-all))
