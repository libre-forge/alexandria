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

(defn list-all
  "list all courses"
  [filter]
  (let [topic (get filter "byTopic")
        status (get filter "byStatus")
        query (-> (dsl/select)
                  (dsl/from :course)
                  (dsl/where ["course.title ilike '%?%' OR course.description ilike '%?%'" (data/nvl topic "")]
                             ["course.status = ?" (data/nvl status "active")]))
        courses (with-open [conn (db/connection)]
                  (sc/fetch conn query))]
    (map fill-member-count courses)))

(defn create-course
  "creates a new course"
  [course]
  (let [clean-params (dissoc (data/filter-nil course) :subjects)
        query (-> (dsl/insert-into :course)
                  (dsl/insert-values clean-params)
                  (dsl/returning :id))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn create-subject
  "creates a subject from a given title"
  [course-id title]
  (let [id (uuid/random)
        query (-> (dsl/insert-into :subject)
                  (dsl/insert-values {:id id :course course-id :title title})
                  (dsl/returning :id))]
    (with-open [conn (db/connection)]
      (sc/fetch-one conn query))))

(defn create
  [params]
  (let [course-id (uuid/random)
        course (merge params {:id course-id})
        saved (create-course course)
        subjects (:subjects course)]
    (doseq [th subjects]
      (create-subject course-id th))
    saved))

(defn by-id
  [id]
  (let [course (with-open [conn (db/connection)]
                 (sc/fetch-one conn ["select * from course where id = ?" id]))
        members (with-open [conn (db/connection)]
                  (sc/fetch conn ["select * FROM liber l join liber_course lc ON l.id = lc.liber WHERE lc.course = ?" id]))
        course-w-members (assoc course :members members)]
    course-w-members))
