(ns spotty.model.channel
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.memcache :as memcache]
            [clj-json.core :as json]
            [digest]))

;tracks are stored as list under :tracks (with each track item as json string)
(ds/defentity Channel [creator name description imageurl])

(defn all []
  (ds/query :kind Channel))

(defn get-by-id [channel-id]
  (ds/retrieve Channel channel-id))

(defn create [creator name description imageurl]
  (ds/save! (Channel. creator name description imageurl)))

(defn add-track [channel spotify-id duration-ms]
  (let [tracks (get channel :tracks [])
        new-tracks (conj tracks (json/generate-string {:spotify-id spotify-id :duration-ms duration-ms}))]
    (ds/save! (assoc channel :tracks new-tracks))))

(defn get-tracks [channel]
  (for [track (:tracks channel)] (json/parse-string track)))

;;keep track of listeners in memcached
;;TODO time out
(defn listeners-key [channel]
  (str "listeners_" (ds/key-id channel)))

(defn now-ms []
  (.getTime (new java.util.Date)))

(def listener-timeout (* 60 1000))

;;keep track of listeners in memcached
;;listeners = map member-id->lasttouch
(defn get-listeners [channel]
  (let [listeners (if-let [str-listeners (memcache/get (listeners-key channel))]
                    (read-string str-listeners)
                    {})
        now-ms (now-ms)]
    ;;filter out non current listeners
    (into {} (for [[id last-touch] listeners
                   :when (< (- now-ms last-touch) listener-timeout)]
               [id last-touch]))))


(defn get-listener-ids [channel]
  (for [[id last-touch] (get-listeners channel)]
    id))

(defn touch-listener! [channel listener]
  (let [listeners (get-listeners channel)
        now-ms (now-ms)
        new-listeners (assoc listeners (:spotify-id listener) now-ms)]
    (memcache/put! (listeners-key channel) (prn-str new-listeners))))


