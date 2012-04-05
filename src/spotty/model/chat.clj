(ns spotty.model.chat
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [clj-json.core :as json]
            [digest]))

(ds/defentity ChatMessage [creator channel datetime msg])

(defn get-latest [channel]
  (ds/query :kind ChatMessage
            :filter (= :channel channel)))

(defn send-message [creator channel msg]
  (ds/save! (ChatMessage. creator channel (new java.util.Date) msg)))
