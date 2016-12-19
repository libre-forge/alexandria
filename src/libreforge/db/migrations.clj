(ns libreforge.db.migrations
  (:require [migrante.core :as mg :refer (defmigration)]
            [libreforge.db.connection :as db]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Migrations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmigration utils-0000
  "Create student table."
  :up (mg/resource "migrations/0000.init.sql"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Entry point
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def +migrations+
  {:name :libreforge-main
   :steps [[:0000 utils-0000]]})

(defn migrate
  []
  (let [options (:migrations {})]
    (with-open [mctx (mg/context db/datasource options)]
      (mg/migrate mctx +migrations+)
      nil)))
