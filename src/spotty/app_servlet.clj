(ns spotty.app_servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use spotty.core)
  (:use [appengine-magic.servlet :only [make-servlet-service-method]]))


(defn -service [this request response]
  ((make-servlet-service-method spotty-app) this request response))
