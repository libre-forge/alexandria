(ns libreforge.courses.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.users.services :as users]
            [libreforge.subjects.services :as subjects]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.common :as db]))

(defn add-pagination
  "abstracts how pagination is done"
  [query pagination]
  (let [fst (:first pagination)
        aft (:after pagination)
        las (:last pagination)
        bef (:before pagination)]
    (cond
      ;; both last & before are present
      (and (not (nil? las)) (not (nil? bef)))
      (-> query
          (dsl/where ["id < ?" (uuid/from-string bef)])
          (dsl/limit las)
          (dsl/order-by [:id :asc]))
      ;; only before is present
      (not (nil? bef))
      (-> query
          (dsl/where ["id < ?" (uuid/from-string bef)])
          (dsl/limit 10)
          (dsl/order-by [:id :asc]))
      ;; only last is present
      (not (nil? las))
      (-> query
          (dsl/limit las)
          (dsl/order-by [:id :desc]))
      ;; both first & after are present
      (and (not (nil? fst)) (not (nil? aft)))
      (-> query
          (dsl/where ["id > ?" (uuid/from-string aft)])
          (dsl/limit fst)
          (dsl/order-by [:id :asc]))
      ;; only after is present
      (not (nil? aft))
      (-> query
          (dsl/where ["id > ?" (uuid/from-string aft)])
          (dsl/limit 10)
          (dsl/order-by [:id :asc]))
      ;; only first is present
      (not (nil? fst))
      (-> query
          (dsl/limit fst)
          (dsl/offset 0)
          (dsl/order-by [:id :asc]))
      ;; no client pagination is present
      :else
      (-> query
          (dsl/limit 10)
          (dsl/offset 0)
          (dsl/order-by [:id :asc])))))


(defn get-list-query
  "builds basic list query using just filters"
  [topic status]
  (-> (dsl/select (dsl/field :id)
                  (dsl/field :title)
                  (dsl/field :pitch)
                  (dsl/field :description)
                  (dsl/field :status)
                  (dsl/field :member_limit)
                  (dsl/field :created_at)
                  (dsl/field :created_by)
                  (dsl/field "count(*) OVER ()" "total_count")
                  (dsl/field "(select count(*) from liber_course where liber_course.course = course.id)" "member_count"))
      (dsl/from :course)
      (dsl/where ["course.title ilike ? OR course.description ilike ?" topic topic]
                 ["course.status = ?" status])))

(defn list-all
  "list all courses"
  [filter pagination]
  (let [topic (data/wild (:byTopic filter ""))
        stats (:byStatus filter "active")
        query (-> (get-list-query topic stats)
                  (add-pagination pagination))]
    (db/fetch query)))

(defn create-course
  "creates a new course"
  [course]
  (let [clean-params (dissoc (data/filter-nil course) :subjects)
        query (-> (dsl/insert-into :course)
                  (dsl/insert-values clean-params)
                  (dsl/returning :id))]
    (db/fetch-one query)))

(defn create
  "creates a new course"
  [params]
  (let [course-id (uuid/random)
        course (merge params {:id course-id})
        saved (create-course course)
        subjects (:subjects course)]
    (doseq [th subjects]
      (subjects/create-only-w-title course-id th))
    saved))

(defn members
  "returns course members"
  [course]
  (let [qry (-> (dsl/select)
                (dsl/from :liber)
                (dsl/join :liber_course)
                (dsl/on "liber.id = liber_course.liber")
                (dsl/where ["liber_course.course = ?" course]))]
    (db/fetch qry)))

(defn by-id
  "returns a given course by id"
  [id]
  (db/find-by-id :course id))

(defn join
  [course member]
  (let [params {:course course :liber member}
        result (db/fetch-one ["insert into liber_course (liber, course) VALUES (?, ?) ON CONFLICT (liber, course) DO UPDATE SET liber = EXCLUDED.liber returning course" member course])]
    (by-id (:course result))))
