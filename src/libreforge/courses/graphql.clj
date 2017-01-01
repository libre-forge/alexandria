(ns libreforge.courses.graphql
  (:require
   [libreforge.util.uuid :as uuid]
   [libreforge.courses.services :as courses]))

(defn list-all
  [context parent args]
  (let [filter (:filter args)]
    (courses/list-all filter)))

(defn by-id
  [context parent args]
  (let [id (uuid/from-string (:id args))]
    (courses/by-id id)))

(defn owner
  [context parent args]
  (let [id (uuid/from-string (:course args))]
    (courses/owner id)))

(defn members
  [context parent args]
  (let [id (uuid/from-string (:course args))]
    (courses/members id)))

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
