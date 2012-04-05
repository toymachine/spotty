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
  (response/json
   (for [c (channel/all)] {:id (ds/key-id c)
                           :name (:name c)
                           :description (:description c)
                           :imageurl (:imageurl c)})))

(defpage "/api/channel/:id" {:keys [id]}
  (if-let [c (channel/get-by-id (Integer/parseInt id))]
    (response/json {:name (:name c)
                    :description (:description c)
                    :imageurl (:imageurl c)})
    (response/status 404 "Channel not found")))

(defpage "/api/channel/:id/tracks" {:keys [id]}
  (if-let [channel (channel/get-by-id (Integer/parseInt id))]
    (response/json (channel/get-tracks channel))
    (response/status 404 "Channel not found")))

(defpage [:any "/api/channel/:channel-id/track"] {:keys [channel-id spotify-id duration-ms]}
  (if-let [channel (channel/get-by-id (Integer/parseInt channel-id))]
    (do
      (channel/add-track channel spotify-id (Integer/parseInt duration-ms))
      (response/status 200 ""))
    (response/status 404 "Channel not found")))

(defpage [:post "/api/channel"] {:keys [name description imageurl]}
  (response/status 200 ""))

(defpage "/api/init" []
  (init/init)
  (response/json {}))

