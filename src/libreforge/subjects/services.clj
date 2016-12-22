(ns libreforge.subjects.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.resources.services :as resources]
            [libreforge.db.connection :as db]))

(defn resource-count
  "fills the number of resources a given subject has"
  [subject]
  (let [id (:id subject)
        count (resources/count-by-subject id)]
    (merge count subject)))

(defn list-by-course
  "list all subjects by course"
  [course]
  (let [query (-> (dsl/select)
                  (dsl/from :subject)
                  (dsl/where ["course = ?" course]))
        subjects (with-open [conn (db/connection)]
                   (sc/fetch conn query))]
    (map resource-count subjects)))

(defn by-id
  [id]
  (let [query (-> (dsl/select)
                  (dsl/from :subject)
                  (dsl/where ["id = ?" id]))
        resources {:resources (resources/list-by-subject id)}
        subject (with-open [conn (db/connection)]
                  (sc/fetch-one conn query))
        subject-w-resources (merge subject resources)]
    subject-w-resources))
