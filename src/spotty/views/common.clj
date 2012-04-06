(ns spotty.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5]]
        [clojure.pprint :only [pprint]]))

(defpartial spotty []
  [:span.m1 "S"]
  [:span.m2 "p"]
  [:span.m3 "o"]
  [:span.m4 "t"]
  [:span.m4 "t"]
  [:span.m5 "y"])

(defpartial layout [& content]
  (html5
   [:head
    [:title "Spotty"]
    (include-css "/static/css/bootstrap.min.css")
    (include-css "/static/css/magic.css")]
   [:body
    ;;[:h1 (with-out-str (pprint (member/get-logged-in-member)))]
    [:div.navbar.navbar-fixed-top
     [:div.navbar-inner
      [:div.container
       [:a.brand {:href "/"} [:blockquote (spotty) [:small "Turn on, tune in, drop out!"]]]
       [:ul.nav
        [:li.active
         [:a {:href "/channels"} "Channels"]]
        [:li
         [:a {:href "/members"} "Members"]]]]]]
    [:div.container
     content]]))
