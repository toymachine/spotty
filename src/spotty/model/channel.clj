(ns spotty.model.channel
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [digest]))

(ds/defentity Channel [creator name description imageurl])

(defn all []
  (ds/query :kind Channel))

(defn create [creator name description imageurl]
  (ds/save! (Channel. creator name description imageurl)))

