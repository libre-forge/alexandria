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

(defn now
  []
  (java.util.Date.))

(defn set-status
  [subject]
  (let [date (:finished_at subject)
        stat (if (nil? date)
               "active"
               (if (> (compare (now) date) 0)
                 "finished"
                 "ongoing"))]
    (merge subject {:status stat})))

(defn set-course
  [subject]
  (let [course (:course subject)
        query (-> (dsl/select)
                  (dsl/from :course)
                  (dsl/where ["id = ?" course]))
        course (with-open [conn (db/connection)]
               (sc/fetch-one conn query))
        enriched (merge subject {:course course})]
    enriched))

(defn list-by-course
  "list all subjects by course"
  [course]
  (let [query (-> (dsl/select)
                  (dsl/from :subject)
                  (dsl/where ["course = ?" course]))
        subjects (with-open [conn (db/connection)]
                   (sc/fetch conn query))]
    (->>(map resource-count subjects)
        (map set-status))))

(defn by-id
  [id]
  (let [query (-> (dsl/select)
                  (dsl/from :subject)
                  (dsl/where ["id = ?" id]))
        resources {:resources (resources/list-by-subject id)}
        subject (with-open [conn (db/connection)]
                  (sc/fetch-one conn query))]
        (-> (merge subject resources)
            (resource-count)
            (set-status)
            (set-course))))
