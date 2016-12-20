(ns libreforge.users.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [libreforge.db.connection :as db]))

(defn create-user
  "creates a new user"
  [user]
  (let [params {:name (:name user)}
        query (-> (dsl/insert-into :liber)
                  (dsl/insert-values params)
                  (dsl/returning :id))]
     (with-open [conn (db/connection)]
       (sc/fetch conn query))))

(defn find-login
  "checks credentials provided from login"
  [email password]
  (let [query (-> (dsl/select)
                  (dsl/from :liber)
                  (dsl/where ["email = ?", email]
                             ["password = ?", password]))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn find-by-email
  "find a user by his email"
  [email]
  (let [query (-> (dsl/select)
                  (dsl/from :liber)
                  (dsl/where ["email = ?", email]))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn list-all
  "list all libers"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :liber))]
    (with-open [conn (db/connection)]
      (sc/fetch conn query))))
