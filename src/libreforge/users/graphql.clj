(ns libreforge.users.graphql
  (:require [libreforge.users.services :as users]
            [cheshire.core :as json]
            [buddy.sign.jwt :as jwt]))

(defn list-all
  [context parent args]
  (users/list-all))

(defn find-by-email
  [context parent args]
  (let [email (:email args)]
    (users/find-by-email email)))

(def secret "mysecret")

(defn login
  [context parent args]
  (let [credentials (:credentials args)
        username (:username credentials)
        password (:password credentials)
        user (users/find-login username password)]
    (if user
      {:token (jwt/sign {:id (:id user)} secret)
       :user user}
      {"token" "kk"})))
