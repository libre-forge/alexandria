(ns libreforge.resources.services
  (:require [suricatta.dsl :as dsl]
            [libreforge.db.connection :as db]))

(defn list-by-subject
  "returns all resources of a given subject"
  [subject]
  (let [query (-> (dsl/select)
                  (dsl/from :resource)
                  (dsl/where ["subject = ?" subject]))]
    (db/fetch query)))
