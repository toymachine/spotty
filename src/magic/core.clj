(ns magic.core
  (:use [ring.middleware.session.cookie :only [cookie-store]])
  (:require [appengine-magic.core :as ae]
            [noir.util.gae :as noir-gae]
            [noir.statuses :as noir-statuses]
            [noir.session :as session]
            [noir.server :as server]
            [spotty.handler.api]
            [spotty.views.common :as common]
            [spotty.login :as login]
            [clojure.tools.logging :as log]))

(noir-statuses/set-page! 404
                         (common/layout
                          [:h1 "Page not found!"]))

;;(server/add-middleware login/logged-in-member-middleware)

(ae/def-appengine-app magic-app (noir-gae/gae-handler {:session-store (cookie-store {:key "rifkvkffdkorodkd"})}))

(defn -main []
  (log/info "starting spotty!")
  (ae/serve magic-app))

