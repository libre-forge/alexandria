(ns libreforge.util.data)

(defn filter-nil
  [map]
  (into {} (filter (comp some? val) map)))