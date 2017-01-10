(ns libreforge.auth.graphql
  (:require [libreforge.users.services :as users]
            [libreforge.util.graphql :as g]
            [clojure.string :as st]
            [cheshire.core :as json]
            [buddy.sign.jwt :as jwt]))

;; application's secret
(def secret "mysecret")

(defn create-token
  "creates a token out of the secret and the user info"
  [user secret]
  (jwt/sign {:id (:id user)} secret))

(defn login
  "application authentication mechanism"
  [{{:keys [credentials]} :args}]
  (let [uname (:username credentials)
        pword (:password credentials)
        user (users/find-login uname pword)]
    (if (nil? user)
      (g/error {:error "invalid username or password"})
      (g/response {:token (create-token user secret) :user user}))))


;; ####################
;; #### DELEGATES #####
;; ####################

(defn unsign-authorization
  [authorization]
  (let [token (st/replace-first authorization "Token " "")
        user (jwt/unsign token secret)]
    (g/delegate {:user user})))

(defn add-user-info
  [{{:keys [authorization]} :ctx}]
  (if (not (nil? authorization))
    (try
      (unsign-authorization authorization)
      (catch Throwable th (g/error {:error "invalid signature"})))
    (g/delegate)))

;; ####################
;; #### ASSERTIONS ####
;; ####################

(defn assert-logged-in
  "checks whether the user is logged-in or not"
  [env]
  (let [user (get-in env [:ctx :user])]
    (if (nil? user)
      (g/error {:error "need to be logged in!"})
      (g/delegate))))
