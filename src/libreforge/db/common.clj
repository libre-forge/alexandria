(ns libreforge.db.common
  (:require [hikari-cp.core :as hikari]
            [suricatta.dsl :as dsl]
            [suricatta.core :as sc]
            [libreforge.util.uuid :as uuid]
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

(defn add-pagination
  "abstracts how pagination is done"
  [query pagination]
  (let [fst (:first pagination)
        aft (:after pagination)
        las (:last pagination)
        bef (:before pagination)]
    (cond
      ;; both last & before are present
      (and (not (nil? las)) (not (nil? bef)))
      (-> query
          (dsl/where ["id < ?" (uuid/from-string bef)])
          (dsl/limit las)
          (dsl/order-by [:id :asc]))
      ;; only before is present
      (not (nil? bef))
      (-> query
          (dsl/where ["id < ?" (uuid/from-string bef)])
          (dsl/limit 10)
          (dsl/order-by [:id :asc]))
      ;; only last is present
      (not (nil? las))
      (-> query
          (dsl/limit las)
          (dsl/order-by [:id :desc]))
      ;; both first & after are present
      (and (not (nil? fst)) (not (nil? aft)))
      (-> query
          (dsl/where ["id > ?" (uuid/from-string aft)])
          (dsl/limit fst)
          (dsl/order-by [:id :asc]))
      ;; only after is present
      (not (nil? aft))
      (-> query
          (dsl/where ["id > ?" (uuid/from-string aft)])
          (dsl/limit 10)
          (dsl/order-by [:id :asc]))
      ;; only first is present
      (not (nil? fst))
      (-> query
          (dsl/limit fst)
          (dsl/offset 0)
          (dsl/order-by [:id :asc]))
      ;; no client pagination is present
      :else
      (-> query
          (dsl/limit 10)
          (dsl/offset 0)
          (dsl/order-by [:id :asc])))))
