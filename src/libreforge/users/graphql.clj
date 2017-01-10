(ns libreforge.users.graphql
  (:require [libreforge.users.services :as users]))

(defn list-all
  [{:keys [ctx parent args]}]
  (users/list-all))

(defn find-by-email
  [{:keys [ctx parent args]}]
  (let [email (:email args)]
    (users/find-by-email email)))
