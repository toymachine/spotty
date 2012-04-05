(ns spotty.model.init
  (:require
   [appengine-magic.services.datastore :as ds]
   [spotty.model.channel :as channel]))

(defn init []
  (ds/save! (channel/create "MetalliChannel"))
  (ds/save! (channel/create "Veronico"))
  (ds/save! (channel/create "Slayor!")))
