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

(defn not-found []
  (response/status 404 "Not found"))

(defn channel-not-found []
  (response/status 404 "Channel not found"))

(defn ok []
  (response/status 200 ""))

(defn channel-from-id [id]
  (channel/get-by-id (Integer/parseInt id)))

(defpage "/api/channels" []
  ;;fetch all channels
  (response/json
   (for [c (channel/all)] {:id (ds/key-id c)
                           :name (:name c)
                           :description (:description c)
                           :imageurl (:imageurl c)})))

(defpage "/api/channel/:id" {:keys [id]}
  ;;fetch a single channel
  (if-let [c (channel-from-id id)]
    (response/json {:name (:name c)
                    :description (:description c)
                    :imageurl (:imageurl c)})
    (channel-not-found)))

(defpage "/api/channel/:id/tracks" {:keys [id]}
  ;;fetch all the tracks of the given channel
  (if-let [channel (channel-from-id id)]
    (response/json (channel/get-tracks channel))
    (channel-not-found)))

(defpage "/api/channel/:id/listeners" {:keys [id]}
  ;;fetch all the tracks of the given channel
  (if-let [channel (channel-from-id id)]
    (response/json (channel/get-listeners channel))
    (channel-not-found)))

(defn find-current [tracks offset-in-tracks-ms]
  ;;loops trough tracks to find the track at given offset in the playlist
  (loop [track-index 0
         track-start-ms 0]
    (let [track (nth tracks track-index)
          track-duration-ms (get track "duration-ms")
          track-end-ms (+ track-start-ms track-duration-ms)]
      (if (and (>= offset-in-tracks-ms track-start-ms)
               (< offset-in-tracks-ms track-end-ms))
        {:current-index track-index
         :current-offset-ms (- offset-in-tracks-ms track-start-ms)
         :current-id (get track "spotify-id")}
        (recur (inc track-index) track-end-ms)))))

(defpage "/api/channel/:id/listen" {:keys [id]}
  ;;returns tracks
  ;;returns current song/offset
  ;;adds/updates current member in listeners
  (if-let [channel (channel-from-id id)]
    (let [tracks (channel/get-tracks channel)
          total-duration-ms (reduce + (for [t tracks] (get t "duration-ms")))
          current-time-ms (.getTime (new java.util.Date))
          offset-in-tracks-ms (mod current-time-ms total-duration-ms)
          ]
      (channel/touch-listener! channel (login/get-logged-in-member))
      (response/json {:tracks tracks
                      :total-duration-ms total-duration-ms
                      :current (find-current tracks offset-in-tracks-ms)}))
    (channel-not-found)))

(defpage [:post "/api/channel/:channel-id/track"] {:keys [channel-id spotify-id duration-ms]}
  ;;add a track to a channel
  (if-let [channel (channel-from-id channel-id)]
    (do
      (channel/add-track channel spotify-id (Integer/parseInt duration-ms))
      (ok))
    (channel-not-found)))

(defpage "/api/channel/:id/chat-messages" {:keys [id]}
  ;;fetch latest chat messages of the channel
  (if-let [channel (channel-from-id id)]
    ;;TODO loop in a loop
    (response/json (for [cm (chat/get-latest channel)]
                     (let [mbr (member/get-by-id (:creator cm))]
                       {:msg (:msg cm)
                        :from (:spotify-id mbr)
                        :avatar (member/get-avatar-url mbr)
                        })))
    (channel-not-found)))

(defpage [:post "/api/channel/:id/chat-message"] {:keys [id message]}
  ;;post a chat message to a channel
  (if-let [channel (channel-from-id id)]
    (do
      (chat/send-message (login/get-logged-in-member) channel message)
      (ok))
    (channel-not-found)))

(defpage "/api/chat/token" []
  (response/json {:token (chat/get-token-for-member (login/get-logged-in-member))}))

(defpage [:post "/api/channel"] {:keys [name description imageurl]}
  ;;create a new channel
  (response/json (channel/create (login/get-logged-in-member) name description imageurl)))

(defpage [:put "/api/member/"] {:keys [id name email]}
  (member/create id name email)
  (ok))

(defpage [:get "/api/member/:id"] {:keys [id]}
  ;;gets member by id
  (if-let [member (member/get-by-id id)]
    (response/json member)
    (not-found)))

(defpage "/api/init" []
  (init/init)
  (ok))

