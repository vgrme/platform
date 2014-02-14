(ns platform.routes.home
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.util :as util]))

(defn dashboard-page []
  (layout/render "dashboard.html" ))

(defn lessons-page []
  (layout/render "lessons.html"))

(defn classes-page []
  (layout/render "classes.html"))

(defn new-lesson-page []
  (layout/render "new-lesson.html"))

(defn new-class-page []
  (layout/render "new-class.html"))

(defroutes home-routes
  (GET "/" [] (dashboard-page))
  (GET "/lessons" [] (lessons-page))
  (GET "/classes" [] (classes-page))
  (GET "/new-lesson" [] (new-lesson-page))
  (GET "/new-class" [] (new-class-page)))
