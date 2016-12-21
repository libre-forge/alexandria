(ns libreforge.courses.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn list-all
  "list all courses"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :course))]
    (with-open [conn (db/connection)]
      (sc/fetch conn query))))

(defn create
  [id title description]
  (let [query (-> (dsl/insert-into :course)
                  (dsl/insert-values {:id id
                                      :title title
                                      :description description})
                  (dsl/returning :id))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))
