(ns libreforge.subjects.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.resources.services :as resources]
            [libreforge.db.connection :as db]))

(defn now
  "returns the current date"
  []
  (java.util.Date.))

(defn set-status
  "sets status of a given subject"
  [subject]
  (let [date (:finished_at subject)
        stat (if (nil? date)
               "active"
               (if (> (compare (now) date) 0)
                 "finished"
                 "ongoing"))]
    (merge subject {:status stat})))

(defn list-by-course
  "list all subjects by course"
  [course]
  (let [query (-> (dsl/select (dsl/field :id)
                              (dsl/field :title)
                              (dsl/field :description)
                              (dsl/field :mime)
                              (dsl/field :subject)
                              (dsl/field :created_at)
                              (dsl/field :created_by)
                              (dsl/field "(select count(*) from resource where subject = subject.id)" "resource_count"))
                  (dsl/from :subject)
                  (dsl/where ["course = ?" course]))
        subjects (db/fetch query)]
    (map set-status subjects)))

(defn by-id
  "returns a subject detail by its id"
  [id]
  (db/find-by-id :subject id))
