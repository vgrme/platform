(ns platform.routes.app
  (:require
    [compojure.route  :as route]
    [compojure.core   :refer :all]
    [platform.views.layout :as layout]
    [platform.util :as util]))

(defn dashboard []
  (layout/render "dashboard.html" ))


(defn classes []
  (layout/render "class/classes.html"))

(defn new-class []
  (layout/render "class/new-class.html"))

(defn lessons []
  (layout/render "lesson/lessons.html"))

(defn new-lesson []
  (layout/render "lesson/new-lesson.html"))


(defroutes geduca-routes
  (GET "/" [] (dashboard))
  (GET "/lessons" [] (lessons))
  (GET "/lessons/new" [] (new-lesson))
  (GET "/classes" [] (classes))
  (GET "/classes/new" [] (new-class)))
