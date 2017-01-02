(ns libreforge.resources.graphql
  (:require [libreforge.resources.services :as resources]))

(defn list-by-subject
  "list all resources of a given subject parent"
  [ctx parent args]
  (let [subject (:id parent)]
    (resources/list-by-subject subject)))
