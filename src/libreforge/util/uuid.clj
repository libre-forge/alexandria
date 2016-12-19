(ns libreforge.util.uuid
  (:import java.util.UUID))

(defn from-string
  [uuid-as-string]
  (UUID/fromString uuid-as-string))

(defn random
  []
  (UUID/randomUUID))
