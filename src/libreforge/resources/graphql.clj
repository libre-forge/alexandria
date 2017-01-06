(ns libreforge.resources.graphql
  (:require [libreforge.util.uuid :as uuid]
            [libreforge.users.services :as users]
            [libreforge.resources.services :as resources]))

(defn create
  "creates a new subject's resource"
  [{:keys [ctx parent args]}]
  (let [resource (:resource args)
        subject-id (uuid/from-string (:subject resource))
        created_by (uuid/from-string (:created_by resource))
        resource (merge resource {:subject subject-id
                                  :created_by created_by})]
    (resources/create resource)))

;; ###################
;; ## RELATIONSHIPS ##
;; ###################

(defn owner
  "returns the resource owner info"
  [{:keys [ctx parent args]}]
  (let [resource (:id parent)]
    (users/load-creator-of :resource resource)))
