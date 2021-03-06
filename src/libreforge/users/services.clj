(ns libreforge.users.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [libreforge.db.common :as db]))

(defn create-user
  "creates a new user"
  [user]
  (let [params {:name (:name user)}
        query (-> (dsl/insert-into :liber)
                  (dsl/insert-values params)
                  (dsl/returning :id))]
    (db/fetch query)))

(defn find-by-email
  "find a user by his email"
  [email]
  (let [query (-> (dsl/select)
                  (dsl/from :liber)
                  (dsl/where ["email = ?", email]))]
    (db/fetch-one query)))

(defn find-by-id
  "find a user by his id"
  [id]
  (let [query (-> (dsl/select)
                  (dsl/from :liber)
                  (dsl/where ["id = ?", id]))]
    (db/fetch-one query)))

(defn list-all
  "list all libers"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :liber))]
    (db/fetch query)))

(defn load-creator-of
  "loads the created_by user info of a given record"
  [type id]
  (let [plt (name type)
        qry (-> (dsl/select)
                (dsl/from :liber)
                (dsl/join type)
                (dsl/on (str "liber.id = " plt ".created_by"))
                (dsl/where [(str plt ".id = ?") id]))]
    (db/fetch-one qry)))
