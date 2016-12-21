(ns libreforge.courses.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn fill-member-count
  "fills the number of members a given course has"
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

(defn create-course
  "creates a new course"
  [course]
  (let [clean-params (dissoc (data/filter-nil course) :themes)
        query (-> (dsl/insert-into :course)
                  (dsl/insert-values clean-params)
                  (dsl/returning :id))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn create-theme
  "creates a theme from a given title"
  [course-id title]
  (let [id (uuid/random)
        query (-> (dsl/insert-into :theme)
                  (dsl/insert-values {:id id :course course-id :title title})
                  (dsl/returning :id))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn create
  [params]
  (let [course-id (uuid/random)
        course (merge params {:id course-id})
        saved (create-course course)
        themes (:themes course)]
    (doseq [th themes]
      (create-theme course-id th))
    saved))
