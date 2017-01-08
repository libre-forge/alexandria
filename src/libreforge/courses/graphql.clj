(ns libreforge.courses.graphql
  (:require
   [libreforge.util.uuid :as uuid]
   [libreforge.util.graphql :as g]
   [libreforge.users.services :as users]
   [libreforge.subjects.services :as subjects]
   [libreforge.courses.services :as courses]))

(defn list-all
  [env]
  (let [filter (get-in env [:args :filter])]
    (courses/list-all filter)))

(defn by-id
  [{{:keys [id :id]} :args}]
  (let [uuid (uuid/from-string id)]
    (courses/by-id uuid)))

(defn create
  [{{:keys [course :course]} :args}]
  (let [owner (:created_by course)
        course (merge course {:created_by (uuid/from-string owner)})]
    (courses/create course)))

(defn join
  [{:keys [args :args]}]
  (let [course (uuid/from-string (:course args))
        member (uuid/from-string (:member args))]
    (courses/join course member)))

;; ###################
;; ## RELATIONSHIPS ##
;; ###################

(defn owner
  "returns the course owner info"
  [{{:keys [id]} :parent}]
  (users/load-creator-of :course id))

(defn members
  [{{:keys [id]} :parent}]
  (courses/members id))

(defn subjects
  "list all subjects by parent course"
  [{{:keys [id]} :parent}]
  (subjects/list-by-course id))
