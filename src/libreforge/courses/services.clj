(ns libreforge.courses.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.users.services :as users]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn fill-member-count
  "fills the number of members a given course has"
  [course]
  (let [id (:id course)
        count (db/fetch-one ["select count(*) as member_count from liber_course where course = ?" id])]
    (merge count course)))

(defn list-all
  "list all courses"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :course))
        courses (db/fetch query)]
    (map fill-member-count courses)))

(defn wild
  [param]
  (str "%" param "%"))

(defn list-all
  "list all courses"
  [filter]
  (let [topic (->> (get filter "byTopic")
                   (data/nvl "")
                   (wild))
        status (->> (get filter "byStatus")
                    (data/nvl "active"))
        query (-> (dsl/select)
                  (dsl/from :course)
                  (dsl/where ["course.title ilike ? OR course.description ilike ?" topic topic]
                             ["course.status = ?" status]))
        courses (db/fetch query)]
    (map fill-member-count courses)))

(defn create-course
  "creates a new course"
  [course]
  (let [clean-params (dissoc (data/filter-nil course) :subjects)
        query (-> (dsl/insert-into :course)
                  (dsl/insert-values clean-params)
                  (dsl/returning :id))]
    (db/fetch-one query)))

(defn create-subject
  "creates a subject from a given title"
  [course-id title]
  (let [id (uuid/random)
        query (-> (dsl/insert-into :subject)
                  (dsl/insert-values {:id id :course course-id :title title})
                  (dsl/returning :id))]
    (db/fetch-one query)))

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
  (let [course (db/fetch-one ["select * from course where id = ?" id])
        members (db/fetch ["select * FROM liber l join liber_course lc ON l.id = lc.liber WHERE lc.course = ?" id])
        created-by (users/find-by-id (:created_by course))
        course-w-owner (assoc course :created_by created-by)
        course-w-members (assoc course-w-owner :members members)]
    course-w-members))

(defn join
  [course member]
  (let [params {:course course :liber member}
        result (db/fetch-one ["insert into liber_course (liber, course) VALUES (?, ?) returning course" member course])]
    (by-id (:course result))))
