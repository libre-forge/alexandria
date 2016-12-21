(ns libreforge.util.data)

(defn filter-nil
  [map]
  (into {} (filter (comp some? val) map)))

(defn nvl
  [val alt]
  (if (nil? val) val alt))
