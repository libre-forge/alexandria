(ns libreforge.resources.graphql
  (:require [libreforge.resources.services :as resources]))

(defn list-by-subject
  "list all resources of a given subject"
  [ctx parent args]
  (let [subject (:subject args)]
    (resources/list-by-subject subject)))
