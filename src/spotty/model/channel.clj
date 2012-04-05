(ns spotty.model.channel
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [clj-json.core :as json]
            [digest]))

;tracks are stored as list under :tracks
(ds/defentity Channel [creator name description imageurl])

(defn all []
  (ds/query :kind Channel))

(defn get-by-id [channel-id]
  (ds/retrieve Channel channel-id))

(defn create [creator name description imageurl]
  (ds/save! (Channel. creator name description imageurl)))

(defn add-track [channel spottify-id duration-ms]
  (let [tracks (get channel :tracks [])
        new-tracks (conj tracks (json/generate-string {:spottify-id spottify-id :duration-ms duration-ms}))]
    (ds/save! (assoc channel :tracks new-tracks))))


