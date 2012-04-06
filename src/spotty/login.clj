(ns spotty.login
  (:require [appengine-magic.services.datastore :as ds]
            [noir.util.crypt :as crypt]
            [spotty.model.member :as member]
            [clojure.tools.logging :as log]))

(def ^:dynamic *logged-in-member* nil)

  ;;0dd8a1809dc2cc5ca4a48496b4ee34108c393b73

(defn get-logged-in-member []
  *logged-in-member*)

(defn logged-in-member? []
  (boolean *logged-in-member*))

(defn logged-in-member-middleware [handler]
  (fn [request]
    (let [member-id (get-in request [:headers "x-spotty-member-id"] "0")]
      (if-let [member (member/get-by-id member-id)]
        ;;execute request with logged in member
        (binding [*logged-in-member* member]
          (log/info "run with logged in member:" member-id)
          (handler request))
        ;;no logged in member
        (do
          (log/info "no logged in member!")
          (handler request))))))

