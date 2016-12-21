(ns libreforge.courses.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn fill-member-count
  [course]
  (let [id (:id course)
        count (with-open [conn (db/connection)]
                (sc/fetch-one conn ["select count(*) as member_count from liber_course where course = ?" id]))]
    (merge count course)))

(defn list-all
  "list all courses"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :course))
        courses (with-open [conn (db/connection)]
                  (sc/fetch conn query))]
    (map fill-member-count courses)))


(defn create
  [id title description]
  (let [query (-> (dsl/insert-into :course)
                  (dsl/insert-values {:id id
                                      :title title
                                      :description description})
                  (dsl/returning :id))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))
