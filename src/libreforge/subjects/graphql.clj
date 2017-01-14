(ns libreforge.subjects.graphql
  (:require [libreforge.util.uuid :as uuid]
            [libreforge.users.services :as users]
            [libreforge.resources.services :as resources]
            [libreforge.subjects.services :as subjects]
            [libreforge.auth.graphql :as auth]))

(defn by-id
  "returns a specific subject detail"
  [{:keys [ctx parent args]}]
  (let [id (uuid/from-string (:id args))]
    (subjects/by-id id)))

(defn create
  "creates a new course's subject"
  [{:keys [ctx parent args]}]
  (let [input (:subject args)
        course (:course input)
        owner (auth/user-id ctx)
        subject (merge input {:created_by owner
                              :course (uuid/from-string course)})]
    (subjects/create subject)))

;; ###################
;; ## RELATIONSHIPS ##
;; ###################

(defn owner
  "returns the subject owner info"
  [{:keys [ctx parent args]}]
  (let [subject (:id parent)]
    (users/load-creator-of :subject subject)))

(defn resources
  "list all resources of a given subject parent"
  [{:keys [ctx parent args]}]
  (let [subject (:id parent)]
    (resources/list-by-subject subject)))
