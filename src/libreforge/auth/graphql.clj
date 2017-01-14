(ns libreforge.auth.graphql
  (:require [libreforge.users.services :as users]
            [libreforge.util.graphql :as g]
            [libreforge.util.uuid :as uuid]
            [clojure.string :as st]
            [cheshire.core :as json]
            [buddy.hashers :as hs]
            [buddy.sign.jwt :as jwt]))

;; application's secret
(def secret "mysecret")

(defn create-token
  "creates a token out of the secret and the user info"
  [user secret]
  (jwt/sign {:id (:id user)} secret))

(defn login
  "application's authentication mechanism"
  [{{:keys [credentials]} :args}]
  (let [uname (:username credentials)
        pword (:password credentials)
        user (users/find-by-email uname)
        valid (hs/check pword (:password user))]
    (if valid
      (g/response {:token (create-token user secret) :user user})
      (g/error {:error "invalid username or password"}))))

(defn user-id
  "gets user UUID from GraphQL context"
  [ctx]
  (let [id (get-in ctx [:user :id])]
    (if (not (nil? id))
      (uuid/from-string id)
      id)))

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
