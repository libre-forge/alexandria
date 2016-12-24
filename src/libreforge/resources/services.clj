(ns libreforge.resources.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [suricatta.format :as fmt]
            [libreforge.util.data :as data]
            [libreforge.util.uuid :as uuid]
            [libreforge.db.connection :as db]))

(defn count-by-subject
  [subject]
  (let [count (db/fetch-one ["select count(*) as resource_count from resource where subject = ?" subject])]
    count))

(defn list-by-subject
  [subject]
  (let [query (-> (dsl/select)
                  (dsl/from :resource)
                  (dsl/where ["subject = ?" subject]))]
    (db/fetch query)))
