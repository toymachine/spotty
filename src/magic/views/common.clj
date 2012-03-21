(ns magic.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css html5]]
        [magic.login :as login]
        [magic.member :as member]
        [clojure.pprint :only [pprint]]))

(defpartial magic []
  [:span.m1 "S"]
  [:span.m2 "p"]
  [:span.m3 "o"]
  [:span.m4 "t"]
  [:span.m4 "t"]
  [:span.m5 "y"])

(defpartial layout [& content]
  (html5
   [:head
    [:title "Magic"]
    (include-css "/static/css/bootstrap.min.css")
    (include-css "/static/css/magic.css")]
   [:body
    ;;[:h1 (with-out-str (pprint (member/get-logged-in-member)))]
    [:div.navbar.navbar-fixed-top
     [:div.navbar-inner
      [:div.container
       [:a.brand {:href "/"} [:blockquote (magic) [:small "Turn on, tune in, drop out!"]]]
       [:ul.nav
        [:li.active
         [:a {:href "/channels"} "Channels"]]
        [:li
         [:a {:href "/members"} "Members"]]
        [:li
         (if-let [member (login/get-logged-in-member)]
           [:a {:href "/logout"} [:img {:src (member/get-avatar-url member)}]]
           [:a {:href "/login"} "Sign In"])]]]]]
    [:div.container
     content]]))
