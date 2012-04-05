(ns spotty.model.chat
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.channel :as chat-channel]
            [spotty.model.channel :as channel]
            [clj-json.core :as json]
            [digest]))

(ds/defentity ChatMessage [creator channel datetime msg])

(defn get-latest [channel]
  (ds/query :kind ChatMessage
            :filter (= :channel channel)))

(defn send-message [sender channel msg]
  (ds/save! (ChatMessage. sender channel (new java.util.Date) msg))
  (let [sender-id (:spotify-id sender)]
    (doseq [listener-id (channel/get-listeners)]
      (when-not (= sender-id listener-id)
        (chat-channel/send listener-id msg)))))
