(ns spotty.login
  (:require [appengine-magic.services.datastore :as ds]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [spotty.model.member :as member]))

(def ^:dynamic *logged-in-member* nil)

(comment

(defn get-logged-in-member []
  *logged-in-member*)

(defn logged-in-member? []
  (boolean *logged-in-member*))

(defn find-member-by-email-and-password [email password]
  (if-let [result (seq (member/find-by-email email))]
    (let [member (first result)]
      (when (crypt/compare password (:password member))
        member))))

(defn get-member-from-session []
  (when-let [member-id (session/get :member-id)]
    (member/get-by-id member-id)))

(defn logged-in-member-middleware [handler]
  (fn [request]
    (if-let [member (get-member-from-session)]
      ;;execute request with logged in member
      (binding [*logged-in-member* member]
        (handler request))
      ;;no logged in member
      (handler request))))

(defn login [email password]
  (if-let [member (find-member-by-email-and-password email password)]
    ;;yeah valid user
    (do
      (session/put! :member-id (ds/key-id member))
      true)
    ;;else wrong password or member not found
    false))

(defn logout []
  (session/clear!))

(defn init-members []
  (let [henk (member/create "henkpunt@gmail.com" (crypt/encrypt "12345") "Henk Punt")
        boris (member/create "boris@hyves.nl" (crypt/encrypt "12345") "Boris Nieuwenhuis")]
    (ds/save! henk)
    (ds/save! boris)))

)