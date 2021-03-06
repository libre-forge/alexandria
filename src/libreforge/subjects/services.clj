(ns libreforge.subjects.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.resources.services :as resources]
            [libreforge.db.common :as db]))

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
                              (dsl/field :entry_order)
                              (dsl/field :course)
                              (dsl/field :created_at)
                              (dsl/field :finished_at)
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

(defn create-only-w-title
  "creates a subject from a given title"
  [course-id title]
  (let [id (uuid/random)
        query (-> (dsl/insert-into :subject)
                  (dsl/insert-values {:id id :course course-id :title title})
                  (dsl/returning :id))]
    (db/fetch-one query)))

(defn create
  "creates a new subject"
  [subject]
  (let [id (uuid/random)
        subject (merge subject {:id id})
        q (-> (dsl/insert-into :subject)
              (dsl/insert-values subject)
              (dsl/returning :id))
        r (db/fetch-one q)]
    (by-id (:id r))))
