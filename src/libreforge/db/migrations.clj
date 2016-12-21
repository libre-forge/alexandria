(ns libreforge.db.migrations
  (:require [migrante.core :as mg :refer (defmigration)]
            [libreforge.db.connection :as db]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Migrations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmigration utils-0000
  "Create student table."
  :up (mg/resource "migrations/0000.init.sql"))

(defmigration utils-0001
  "Add sample courses."
  :up (mg/resource "migrations/0001.courses.sql"))

(defmigration utils-0002
  "Add users to courses."
  :up (mg/resource "migrations/0002.liber_courses.sql"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Entry point
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def +migrations+
  {:name :libreforge-main
   :steps [[:0000 utils-0000]
           [:0001 utils-0001]
           [:0002 utils-0002]]})

(defn migrate
  []
  (let [options (:migrations {})]
    (with-open [mctx (mg/context db/datasource options)]
      (mg/migrate mctx +migrations+)
      nil)))
