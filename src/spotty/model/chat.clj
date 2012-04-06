(ns spotty.model.chat
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.channel :as chat-channel]
            [spotty.model.channel :as channel]
            [clj-json.core :as json]
            [digest]))

(ds/defentity ChatMessage [creator channel datetime msg])

(defn get-token-for-member [member]
  (chat-channel/create-channel (:spotify-id member)))

(defn get-latest [channel]
  (ds/query :kind ChatMessage
            :filter (= :channel channel)))

(defn send-message [sender channel msg]
  (ds/save! (ChatMessage. sender channel (new java.util.Date) msg))
  (let [sender-id (:spotify-id sender)
        json-msg (json/generate-string {:msg msg
                                        :channel-id (ds/key-id channel)})]
    (doseq [listener-id (channel/get-listeners)]
      (when-not (= sender-id listener-id)
        (chat-channel/send listener-id json-msg)))))
