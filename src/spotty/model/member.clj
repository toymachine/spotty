(ns spotty.model.member
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [digest]))

(ds/defentity Member [name email])

(defn all []
  (ds/query :kind Member))

(defn create [name email]
  (ds/save! (Member. name email)))

(defn find-by-email [email]
  (ds/query :kind Member
            :filter (= :email email)))

(defn get-by-id [member-id]
  (ds/retrieve Member member-id))

(defn get-avatar-url [member]
  (str "http://www.gravatar.com/avatar/" (digest/md5 (lower-case (trim (:email member)))) "?s=64"))
