(ns platform.controllers.class
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [platform.util :as util]
            [noir.response :as resp]))


(defn classes []
  (layout/render "class/classes.html"))

(defn new-class []
  (layout/render "class/new-class.html"))