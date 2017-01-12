(ns libreforge.handlers
  (:require [catacumba.core :as ct]
            [catacumba.http :as http]
            [catacumba.handlers.auth :as cauth]
            [catacumba.handlers.misc :as misc]
            [cheshire.core :as json]
            [catacumba.http :as http]
            [libreforge.util.handlers :as parse]
            [libreforge.util.http :as http-util]
            [libreforge.graphql :as graphql]
            [libreforge.users.handlers :as users]
            [libreforge.db.migrations :as mg])
  (:gen-class))

(def cors-conf {:origin "*"                                         ;; mandatory
                :max-age 3600                                       ;; optional
                :allow-methods #{:option :post :put :get :delete}   ;; optional
                :allow-headers #{:x-requested-with :content-type}}) ;; optional

(defn execute-graphql
  "GraphQL endpoint"
  [ctx]
  (let [mp (:data ctx)
        hs (:headers ctx)
        qy (get-in mp ["query"])
        vs (-> (get-in mp ["variables"])
               (json/parse-string))
        rs (graphql/resolve hs qy vs)]
    (-> (json/encode rs)
        (http/ok {:content-type http-util/json-type}))))

(def app-routes
  "all application endpoints"
  (ct/routes [[:assets "" {:dir "public"
                           :indexes ["index.html"]}]
              [:assets "graphql/schema" {:dir "graphql"
                                         :indexes ["schema.graphql"]}]
              [:any (parse/body-params)]
              [:any (misc/cors cors-conf)]
              [:post "graphql" execute-graphql]]))
