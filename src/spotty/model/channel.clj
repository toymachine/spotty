(ns spotty.model.channel
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [digest]))

(ds/defentity Channel [name])

(defn all []
  (ds/query :kind Channel))

(defn create [name]
  (Channel. name))

