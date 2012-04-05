(ns magic.views.welcome
  (:require [magic.views.common :as common]
            [appengine-magic.services.datastore :as ds]
            [noir.util.crypt :as crypt]
            [noir.response :as response]
            [noir.session :as session]
            [magic.login :as login]
            [magic.member :as member])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [clojure.pprint :only [pprint]]))

(defpage "/test" []
  (common/layout
   "blaat"))

(defpage "/init" []
  (login/init-members)
  (common/layout
   [:p "done, data inited"]))

(defpage "/members" []
  (let [members (member/all)]
    (common/layout
     [:p "list" (with-out-str (pprint members))])))

(defpage "/channels" []
  (common/layout
   [:p "channels"]))


(defpage [:get "/login"] []
  (common/layout
   [:h2 "Sign In"]
   [:form.form-vertical {:method "post" :action "/login"}
    [:label {:for "email"} "Email"]
    [:input {:name "email" :type "text" :size "40"}]
    [:label {:for "password"} "Password"]
    [:input {:name "password" :type "password" :size "40"}]
    [:div.form-actions
     [:input.btn {:type "submit" :value "Sign In"}]]]))

;;TODO error response to user
(defpage [:post "/login"] {:keys [email password]}
  (if (login/login email password)
    (response/redirect "/?loggedin")
    (response/redirect "/login")))

(defpage "/logout" []
  (login/logout)
  (response/redirect "/"))

(defpage "/" []
  (common/layout
   [:h1 "Welcome to " (common/magic)
    (when-let [member (login/get-logged-in-member)]
      (str " " (:name member)))]))

