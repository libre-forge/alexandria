(ns libreforge.util.handlers
  (:require [catacumba.impl.context :as ctx]
            [cheshire.core :as json]
            [promesa.core :as p]
            [cognitect.transit :as transit]
            [clojure.edn :as edn])
  (:import ratpack.http.TypedData
           ratpack.handling.Context
           (java.io InputStreamReader
                    PushbackReader)))

(defmulti parse-body
  "A polymorophic method for parse body into clojure
  friendly data structure."
  (fn [^Context ctx ^TypedData body]
    (let [^String contenttype (.. body getContentType getType)]
      (if contenttype
        (keyword (.toLowerCase contenttype))
        :application/octet-stream)))
  :default :application/octet-stream)

(defmethod parse-body :multipart/form-data
  [^Context ctx ^TypedData body]
  (ctx/get-formdata* ctx body))

(defmethod parse-body :application/x-www-form-urlencoded
  [^Context ctx ^TypedData body]
  (ctx/get-formdata* ctx body))

(defmethod parse-body :application/json
  [^Context ctx ^TypedData body]
  (let [^String data (slurp body)
        json (json/parse-string data false)]
    json))

(defmethod parse-body :application/octet-stream
  [^Context ctx ^TypedData body]
  nil)

(defmethod parse-body :application/transit+json
  [^Context ctx ^TypedData body]
  (let [reader (transit/reader (.getInputStream body) :json)]
    (transit/read reader)))

(defmethod parse-body :application/transit+msgpack
  [^Context ctx ^TypedData body]
  (let [reader (transit/reader (.getInputStream body) :msgpack)]
    (transit/read reader)))

(defmethod parse-body :application/edn
  [^Context ctx ^TypedData body]
  (edn/read {:eof nil :readers *data-readers*}
            (-> (.getInputStream body)
                (InputStreamReader. "UTF-8")
                (PushbackReader.))))

(defn body-params
  "A route chain that parses the body into
  a clojure friendly data structure.

  This function optionally accept a used defined method
  or multimethod where to delegate the body parsing."
  ([] (body-params nil))
  ([{:keys [parsefn attr] :or {parsefn parse-body attr :data}}]
   (fn [{:keys [method body] :as context}]
     (let [ctx (ctx/get-context* context)]
       (cond
         (= method :get)
         (ctx/delegate {attr nil})

         body
         (ctx/delegate {attr (parsefn ctx body)})

         (nil? body)
         (->> (ctx/get-body! ctx)
              (p/map (fn [^TypedData body]
                       (ctx/delegate {attr (parsefn ctx body) :body body})))))))))
