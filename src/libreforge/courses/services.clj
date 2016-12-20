(ns libreforge.courses.services
  (:require [suricatta.core :as sc]
            [suricatta.dsl :as dsl]
            [libreforge.db.connection :as db]))

(defn list-all
  "list all courses"
  []
  (let [query (-> (dsl/select)
                  (dsl/from :course))]
    (with-open [conn (db/connection)]
      (sc/fetch conn query))))
