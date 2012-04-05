(ns spotty.handler.api
  (:require [appengine-magic.services.datastore :as ds]
            [noir.util.crypt :as crypt]
            [noir.response :as response]
            [noir.session :as session]
            [spotty.model.channel :as channel]
            [spotty.model.init :as init])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [clojure.pprint :only [pprint]]))

(defpage "/api/channels" []
  (response/json (for [c (channel/all)] (:name c))))


(defpage "/api/init" []
  (init/init)
  (response/json {}))