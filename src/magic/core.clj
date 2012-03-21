(ns magic.core
  (:use [ring.middleware.session.cookie :only [cookie-store]])
  (:require [appengine-magic.core :as ae]
            [appengine-magic.services.datastore :as ds]
            [noir.util.gae :as noir-gae]
            [noir.statuses :as noir-statuses]
            [noir.session :as session]
            [noir.server :as server]
            [magic.views.welcome]
            [magic.views.common :as common]
            [magic.login :as login]))

(noir-statuses/set-page! 404
                         (common/layout
                          [:h1 "Page not found!"]))

(server/add-middleware login/logged-in-member-middleware)

(ae/def-appengine-app magic-app (noir-gae/gae-handler {:session-store (cookie-store {:key "rifkvkffdkorodkd"})}))

(comment
  (ae/serve magic-app)
  (ae/stop)
  )