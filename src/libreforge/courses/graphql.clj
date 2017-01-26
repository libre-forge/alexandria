(ns libreforge.courses.graphql
  (:require
   [libreforge.util.uuid :as uuid]
   [libreforge.util.graphql :as g]
   [libreforge.users.services :as users]
   [libreforge.subjects.services :as subjects]
   [libreforge.courses.services :as courses]
   [libreforge.auth.graphql :as auth]))

(defn to-node
  [record]
  {:node record})

(defn convert-to-edges
  [result]
  (let [totalCount (if (empty? result)
                     0
                     (:total_count (first result)))
        edges (map to-node result)
        firstRow (:id (first result))
        lastRow (:id (last result))]
    {:totalCount totalCount
     :edges edges
     :pageInfo {:startCursor firstRow
                :endCursor lastRow}}))

(defn list-all
  [env]
  (let [filter (get-in env [:args :filter])]
    (let [res (courses/list-all filter)]
      (convert-to-edges res))))

(defn by-id
  [{{:keys [id]} :args}]
  (let [uuid (uuid/from-string id)]
    (courses/by-id uuid)))

(defn create
  [{:keys [args ctx]}]
  (let [owner (auth/user-id ctx)
        input (:course args)
        course (merge input {:created_by owner})]
    (courses/create course)))

(defn join
  [{:keys [args ctx]}]
  (let [member (auth/user-id ctx)
        course (uuid/from-string (:course args))]
    (courses/join course member)))

;; ###################
;; ## RELATIONSHIPS ##
;; ###################

(defn owner
  "returns the course owner info"
  [{{:keys [id]} :parent}]
  (users/load-creator-of :course id))

(defn members
  [{{:keys [id]} :parent}]
  (courses/members id))

(defn subjects
  "list all subjects by parent course"
  [{{:keys [id]} :parent}]
  (subjects/list-by-course id))
