(ns platform.routes.home
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.controllers.activity :as activity-controller]
            [platform.controllers.lesson :as lesson-controller]
            [platform.controllers.class :as class-controller]
            [platform.util :as util]
            [noir.response :as resp]
            [noir.session :as session]
            [noir.cookies :as cookies]
            [noir.util.route :as route]
            [noir.util.route :refer [restricted]]
            [platform.models.db :as db]))


(defn dashboard []
  (layout/render "dashboard.html"))


(defroutes home-routes
  (GET "/home" [] 
       (dashboard))
  
  (GET "/lessons" [] 
       (lesson-controller/lessons))
  (GET "/lessons/new" [] 
       (lesson-controller/new-lesson))
  
  (GET "/classes" [] 
       (class-controller/classes))
  (GET "/classes/new" [] 
       (class-controller/new-class))
  
  (GET "/activity/new/right-wrong" [] 
       (activity-controller/right-wrong-quiz))
  (GET "/activity/new/judge-image" [] 
       (activity-controller/judge-image-quiz))
  
  (POST "/activity/new" []
       (activity-controller/new-activity)))
