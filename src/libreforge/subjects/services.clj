(ns libreforge.subjects.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn resource-count
  "fills the number of resources a given subject has"
  [subject]
  (let [id (:id subject)
        count (with-open [conn (db/connection)]
                (sc/fetch-one conn ["select count(*) as resource_count from resource where subject = ?" id]))]
    (merge count subject)))

(defn list-by-course
  "list all courses"
  [course]
  (let [query (-> (dsl/select)
                  (dsl/from :subject)
                  (dsl/where ["course = ?" course]))
        subjects (with-open [conn (db/connection)]
                   (sc/fetch conn query))]
        (map resource-count subjects)))
