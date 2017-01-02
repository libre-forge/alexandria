(ns libreforge.subjects.graphql
  (:require [libreforge.util.uuid :as uuid]
            [libreforge.users.services :as users]
            [libreforge.resources.services :as resources]
            [libreforge.subjects.services :as subjects]))

(defn by-id
  "returns a specific subject detail"
  [context parent args]
  (let [id (uuid/from-string (:id args))]
    (subjects/by-id id)))

;; ###################
;; ## RELATIONSHIPS ##
;; ###################

(defn owner
  "returns the subject owner info"
  [context parent args]
  (let [subject (:id parent)]
    (users/load-creator-of :subject subject)))

(defn resources
  "list all resources of a given subject parent"
  [ctx parent args]
  (let [subject (:id parent)]
    (resources/list-by-subject subject)))
