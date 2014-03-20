(ns platform.controllers.lesson
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.util :as util]
            [noir.response :as resp]))


(defn lessons []
  (layout/render "lesson/lessons.html"))

(defn new-lesson []
  (layout/render "lesson/new-lesson.html"))