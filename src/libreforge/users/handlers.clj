(ns libreforge.users.handlers
    (:require [catacumba.core :as ct]
              [catacumba.http :as http]
              [catacumba.handlers.auth :as cauth]
              [cuerdas.core :as str]
              [cheshire.core :as json]
              [buddy.sign.jwt :as jwt]
              [libreforge.util.uuid :as uuid]
              [libreforge.users.services :as services]
              [libreforge.users.graphql :as graphql]))

(def secret "mysecret")

(def auth-backend
  (cauth/jws-backend {:secret secret
                      :auth-options {:alg :a256kw
                                     :enc :a128cbc-hs256}}))

(defn authorization
  "checks whether the current request is legitimate or not
  and sends a 403 forbidden response if it is not"
  [{:keys [identity] :as context}]
  (if identity
    (ct/delegate {:identity (uuid/from-string (:id identity))})
    (http/forbidden)))

(def json-type "application/json")

(defn login
  "users can get a valid token providing their credentials
  against this endpoint"
  [context]
  (let [body (:data context)
        username (:username body)
        password (:password body)
        user (services/find-login username password)]
    (if user
      (-> (json/encode {:token (jwt/sign {:id (:id user)} secret)})
          (http/ok {:content-type json-type}))
      (http/not-found))))

(defn queries
  "lists all users"
  [context]
  (let [query (:query (:query-params context))
        result (graphql/resolve nil query)]
    (-> (json/encode result)
        (http/ok {:content-type json-type}))))
