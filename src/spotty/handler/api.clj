(ns spotty.handler.api
  (:require [appengine-magic.services.datastore :as ds]
            [noir.util.crypt :as crypt]
            [noir.response :as response]
            [noir.session :as session]
            [spotty.login :as login]
            [spotty.model.channel :as channel]
            [spotty.model.chat :as chat]
            [spotty.model.member :as member]
            [spotty.model.init :as init]
            [clojure.tools.logging :as log])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [clojure.pprint :only [pprint]]))

(defn channel-not-found []
  (response/status 404 "Channel not found"))

(defn ok []
  (response/status 200 ""))

(defpage "/api/channels" []
  ;;fetch all channels
  (response/json
   (for [c (channel/all)] {:id (ds/key-id c)
                           :name (:name c)
                           :description (:description c)
                           :imageurl (:imageurl c)})))

(defpage "/api/channel/:id" {:keys [id]}
  ;;fetch a single channel
  (if-let [c (channel/get-by-id (Integer/parseInt id))]
    (response/json {:name (:name c)
                    :description (:description c)
                    :imageurl (:imageurl c)})
    (channel-not-found)))

(defpage "/api/channel/:id/tracks" {:keys [id]}
  ;;fetch all the tracks of the given channel
  (if-let [channel (channel/get-by-id (Integer/parseInt id))]
    (response/json (channel/get-tracks channel))
    (channel-not-found)))

(defpage "/api/channel/:id/listeners" {:keys [id]}
  ;;fetch all the tracks of the given channel
  (if-let [channel (channel/get-by-id (Integer/parseInt id))]
    (response/json (channel/get-listeners channel))
    (channel-not-found)))

(defpage "/api/channel/:id/listen" {:keys [id]}
  ;;adds current member to listeners
  (if-let [channel (channel/get-by-id (Integer/parseInt id))]
    (do
      (channel/touch-listener! channel (login/get-logged-in-member))
      (ok))
    (channel-not-found)))

(defpage [:post "/api/channel/:channel-id/track"] {:keys [channel-id spotify-id duration-ms]}
  ;;add a track to a channel
  (if-let [channel (channel/get-by-id (Integer/parseInt channel-id))]
    (do
      (channel/add-track channel spotify-id (Integer/parseInt duration-ms))
      (ok))
    (channel-not-found)))

(defpage "/api/channel/:id/chat-messages" {:keys [id]}
  ;;fetch latest chat messages of the channel
  (if-let [channel (channel/get-by-id (Integer/parseInt id))]
    ;;TODO loop in a loop
    (response/json (for [cm (chat/get-latest channel)]
                     (let [mbr (member/get-by-id (:creator cm))]
                       {:msg (:msg cm)
                        :from (:spotify-id mbr)
                        :avator (member/get-avatar-url mbr)})))
    (channel-not-found)))

(defpage [:post "/api/channel/:channel-id/chat-message"] {:keys [channel-id message]}
  ;;post a chat message to a channel
  (if-let [channel (channel/get-by-id (Integer/parseInt channel-id))]
    (do
      (chat/send-message (login/get-logged-in-member) channel message)
      (ok))
    (channel-not-found)))

(defpage [:post "/api/channel"] {:keys [name description imageurl]}
  ;;create a new channel
  (ok))

(defpage [:put "/api/member/"] {:keys [id name email]}
  (member/create id name email))

(defpage [:get "/api/member/:id"] {:keys [id]}
  ;;gets member by id
  (if-let [member (member/get-by-id id)]
    (response/json member)
    (response/status 404 "Not found")))

(defpage "/api/init" []
  (init/init)
  (response/json {}))

