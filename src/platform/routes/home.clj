(ns platform.routes.home
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.util :as util]
            [noir.response :as resp]))


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

(defn view-activity []
  (layout/render "activity/right-wrong-quiz.html"))

(defn new-activity [] 
  (resp/redirect "/lessons/new"))


(defroutes home-routes
  (GET "/home" [] (home))
  
  (GET "/lessons" [] (lessons))
  (GET "/lessons/new" [] (new-lesson))
  
  (GET "/classes" [] (classes))
  (GET "/classes/new" [] (new-class))
  
  (GET "/activity/new" [] (view-activity))
  (POST "/activity/new" [] (new-activity)))
