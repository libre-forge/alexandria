(ns libreforge.util.io
  (:require [clojure.java.io :as io]))

(defn read-resource
  "reads the content of a file in the class path"
  [class-path]
  (-> (io/resource class-path)
      (slurp)))
