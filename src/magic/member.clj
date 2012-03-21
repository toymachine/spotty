(ns magic.member
  (:use [clojure.string :only [lower-case trim]])
  (:require [appengine-magic.services.datastore :as ds]
            [digest]))

(ds/defentity Member [email, password, name])

(defn all []
  (ds/query :kind Member))

(defn create [email password name]
  (Member. email password name))

(defn find-by-email [email]
  (ds/query :kind Member
            :filter (= :email email)))

(defn get-by-id [member-id]
  (ds/retrieve Member member-id))

(defn get-avatar-url [member]
  (str "http://www.gravatar.com/avatar/" (digest/md5 (lower-case (trim (:email member)))) "?s=64"))
