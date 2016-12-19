(ns libreforge.app
  (:require [catacumba.core :as ct]
            [catacumba.http :as http]
            [catacumba.handlers.auth :as cauth]
            [catacumba.handlers.parse :as parse]
            [libreforge.users.handlers :as users]
            [libreforge.db.migrations :as mg])
  (:gen-class))

(def app
  "all application endpoints"
  (ct/routes [[:any (parse/body-params)]
              [:prefix "api"
               [:post "auth/token" users/login]
               [:any (cauth/auth users/auth-backend)]
               [:any users/authorization]
               [:get "users" users/list-all]]]))

(defn -main
  "application's entry point"
  [& args]
  (ct/run-server app {:port 3030})
  (mg/migrate))
