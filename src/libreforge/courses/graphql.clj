(ns libreforge.courses.graphql
  (:require
   [libreforge.util.uuid :as uuid]
   [libreforge.users.services :as users]
   [libreforge.subjects.services :as subjects]
   [libreforge.courses.services :as courses]))

(defn list-all
  [context parent args]
  (let [filter (:filter args)]
    (courses/list-all filter)))

(defn by-id
  [context parent args]
  (let [id (uuid/from-string (:id args))]
    (courses/by-id id)))

(defn create
  [context parent args]
  (let [input (:course args)
        owner (:created_by input)
        course (merge input {:created_by (uuid/from-string owner)})]
    (courses/create course)))

(defn join
  [context parent args]
  (let [course (uuid/from-string (:course args))
        member (uuid/from-string (:member args))]
    (courses/join course member)))

;; ###################
;; ## RELATIONSHIPS ##
;; ###################

(defn owner
  "returns the course owner info"
  [context parent args]
  (let [course (:id parent)]
    (users/load-creator-of :course course)))

(defn members
  [context parent args]
  (let [id (uuid/from-string (:id parent))]
    (courses/members id)))

(defn subjects
  "list all subjects by parent course"
  [context parent args]
  (let [course (:id parent)]
    (subjects/list-by-course course)))
