(ns libreforge.handlers
  (:require [catacumba.core :as ct]
            [catacumba.http :as http]
            [catacumba.handlers.auth :as cauth]
            [catacumba.handlers.parse :as parse]
            [catacumba.handlers.misc :as misc]
            [libreforge.graphql :as graphql]
            [libreforge.users.handlers :as users]
            [libreforge.db.migrations :as mg])
  (:gen-class))

(def cors-conf {:origin "*"                     ;; mandatory
                :max-age 3600                                       ;; optional
                :allow-methods #{:option :post :put :get :delete}           ;; optional
                :allow-headers #{:x-requested-with :content-type}}) ;; optional

(def app-routes
  "all application endpoints"
  (ct/routes [[:any (parse/body-params)]
              [:any (misc/cors cors-conf)]
              [:prefix "api"
               [:post "auth/token" users/login]
;;               [:any (cauth/auth users/auth-backend)]
;;               [:any users/authorization]
               [:post "graphql" graphql/mutations]]]))
