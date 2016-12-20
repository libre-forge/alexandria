(ns libreforge.users.graphql
  (:require [libreforge.users.services :as users]))

(defn list-all
  [context parent args]
  (users/list-all))

(defn find-by-email
  [context parent args]
  (let [email (get args "email")]
    (users/find-by-email email)))
