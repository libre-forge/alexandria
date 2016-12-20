(ns libreforge.app
  (:require [catacumba.core :as ct]
            [libreforge.handlers :as handlers]
            [libreforge.db.migrations :as mg])
  (:gen-class))

(defn -main
  "application's entry point"
  [& args]
  (ct/run-server handlers/app-routes {:port 3030})
  (mg/migrate))
