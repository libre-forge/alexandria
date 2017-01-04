(ns libreforge.resources.services
  (:require [suricatta.dsl :as dsl]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn list-by-subject
  "returns all resources of a given subject"
  [subject]
  (let [query (-> (dsl/select)
                  (dsl/from :resource)
                  (dsl/where ["subject = ?" subject]))]
    (db/fetch query)))

(defn by-id
  "returns a subject resource by its id"
  [id]
  (db/find-by-id :resource id))

(defn create
  [resource]
  (let [id (uuid/random)
        resource (merge resource {:id id})
        q (-> (dsl/insert-into :resource)
              (dsl/insert-values resource)
              (dsl/returning :id))
        r (db/fetch-one q)]
    (by-id (:id r))))
