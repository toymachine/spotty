(ns spotty.handler.api
  (:require [appengine-magic.services.datastore :as ds]
            [noir.util.crypt :as crypt]
            [noir.response :as response]
            [noir.session :as session]
            [spotty.model.channel :as channel]
            [spotty.model.init :as init]
            [clojure.tools.logging :as log])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [clojure.pprint :only [pprint]]))

(defpage "/api/channels" []
  (response/json (for [c (channel/all)] [(ds/key-id c) (:name c)])))

(defpage "/api/channel/:id" {:keys [id]}
  (if-let [channel (channel/get-by-id (Integer/parseInt id))]
    (response/json {:name (:name channel)})
    (response/status 404 "Not found")))

(defpage [:post "/api/channel"] {:keys [name description imageurl]}
  (response/json {:success true}))

(defpage "/api/init" []
  (init/init)
  (response/json {}))

