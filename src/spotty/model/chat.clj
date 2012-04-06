(ns spotty.model.chat
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [appengine-magic.services.channel :as chat-channel]
            [spotty.model.channel :as channel]
            [cheshire.core :as json]
            [digest]
            [clojure.tools.logging :as log]))

(ds/defentity ChatMessage [creator channel datetime msg])

(defn get-token-for-member [member]
  (chat-channel/create-channel (:spotify-id member)))

(defn get-latest [channel]
  (ds/query :kind ChatMessage
            :filter (= :channel channel)))

(defn send-message [sender channel msg]
  (log/info "send-message" sender)
  (ds/save! (ChatMessage. sender channel (new java.util.Date) msg))
  (channel/touch-listener! channel sender)
  (let [sender-id (:spotify-id sender)
        json-msg (json/generate-string {:msg msg
                                        :channel-id (ds/key-id channel)})]
    (doseq [listener-id (channel/get-listener-ids channel)]
      ;;(when-not (= sender-id listener-id)
      (log/info "send to listener" listener-id json-msg)
      (chat-channel/send listener-id json-msg))))
