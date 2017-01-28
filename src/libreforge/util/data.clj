(ns libreforge.util.data
  (:require
   [buddy.core.codecs.base64 :as buddy-64]
   [buddy.core.codecs :as buddy-enc]))

(defn filter-nil
  [map]
  (into {} (filter (comp some? val) map)))

(defn nvl
  [val alt]
  (if (clojure.string/blank? val) alt val))

(defn wild
  [param]
  (str "%" param "%"))

(defn base64->str
  [base]
  (if (not (nil? base))
    (-> (buddy-64/decode base)
        (buddy-enc/bytes->str))
    nil))

(defn str->base64
  [source]
  (if (not (nil? source))
    (-> (buddy-64/encode source)
        (buddy-enc/bytes->str))
    nil))
