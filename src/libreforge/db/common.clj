(ns libreforge.db.common
  (:require [hikari-cp.core :as hikari]
            [suricatta.dsl :as dsl]
            [suricatta.core :as sc]
            [libreforge.db.connection :as conn]))

(defn fetch-one
  "execute a given query and returns only one result"
  [query]
  (with-open [conn (conn/connection)]
    (sc/fetch-one conn query)))

(defn fetch
  "execute a given query and returns a list of results"
  [query]
  (with-open [conn (conn/connection)]
    (sc/fetch conn query)))

(defn find-by-id
  "execute a query looking for a specific record by table and id"
  [table id]
  (let [qry (-> (dsl/select)
                (dsl/from table)
                (dsl/where ["id = ?" id]))]
    (fetch-one qry)))
