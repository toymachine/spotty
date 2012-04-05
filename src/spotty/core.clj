(ns spotty.core
  (:use [ring.middleware.session.cookie :only [cookie-store]])
  (:require [appengine-magic.core :as ae]
            [noir.util.gae :as noir-gae]
            [noir.statuses :as noir-statuses]
            [noir.session :as session]
            [noir.server :as server]
            [spotty.handler.api]
            [spotty.views.common :as common]
            [spotty.login :as login]
            [clojure.tools.logging :as log]
            [clj-json.core :as json]
            [clojure.tools.logging :as log]))

(noir-statuses/set-page! 404
                         (common/layout
                          [:h1 "Page not found!"]))

(defn backbone [handler]
  (fn [req]
    (let [neue (if (= "application/json" (get-in req [:headers "content-type"]))
                 (let [json-params (json/parse-string (slurp (:body req)))
                       keyword-params (into {} (for [[k v] json-params] [(keyword k) v]))
                       merged-params (merge {} (:params req) keyword-params)
                       new-req (assoc-in req [:params] merged-params)]
                   new-req)
                 req)]
      (handler neue))))

(server/add-middleware backbone)

(ae/def-appengine-app spotty-app (noir-gae/gae-handler {:session-store (cookie-store {:key "rifkvkffdkorodkd"})}))

(defn -main []
  (log/info "starting spotty!")
  (ae/serve spotty-app))
