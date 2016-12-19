(ns libreforge.users.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [libreforge.db.connection :as db]))

(defn create-user
  "creates a new student"
  [user]
  (let [params {:name (:name user)}
        query (-> (dsl/insert-into :student)
                  (dsl/insert-values params)
                  (dsl/returning :id))]
     (with-open [conn (db/connection)]
       (sc/fetch conn query))))

(defn find-login
  "checks credentials provided from login"
  [username password]
  (let [query (-> (dsl/select)
                  (dsl/from :student)
                  (dsl/where ["username = ?", username]
                             ["password = ?", password]))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn list-all
  "list all students"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :student))]
    (with-open [conn (db/connection)]
      (sc/fetch conn query))))
