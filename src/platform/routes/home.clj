(ns platform.routes.home
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.util :as util]))


(defn home []
  (layout/render "home.html" ))

(defn classes []
  (layout/render "class/classes.html"))

(defn new-class []
  (layout/render "class/new-class.html"))

(defn lessons []
  (layout/render "lesson/lessons.html"))

(defn new-lesson []
  (layout/render "lesson/new-lesson.html"))


(defroutes home-routes
  (GET "/" [] (home))
  (GET "/lessons" [] (lessons))
  (GET "/lessons/new" [] (new-lesson))
  (GET "/classes" [] (classes))
  (GET "/classes/new" [] (new-class)))

