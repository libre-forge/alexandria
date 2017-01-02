(ns libreforge.db.connection
  (:require [hikari-cp.core :as hikari]
            [suricatta.dsl :as dsl]
            [suricatta.core :as sc]))

(def ^javax.sql.DataSource
  datasource (hikari/make-datasource
              {:connection-timeout 30000
               :idle-timeout 600000
               :max-lifetime 1800000
               :minimum-idle 10
               :maximum-pool-size  10
               :adapter "postgresql"
               :username "libreforge"
               :password "libreforge"
               :database-name "libreforge"
               :server-name "localhost"
               :port-number 5432}))

(defn connection
  []
  (sc/context datasource))

(defn fetch-one
  [query]
  (with-open [conn (connection)]
    (sc/fetch-one conn query)))

(defn fetch
  [query]
  (with-open [conn (connection)]
    (sc/fetch conn query)))

(defn find-by-id
  [table id]
  (let [qry (-> (dsl/select)
                (dsl/from table)
                (dsl/where ["id = ?" id]))]
    (fetch-one qry)))
