(ns platform.routes.uploader
  (:use 
    compojure.core)
  (:require
      [platform.views.layout :as layout]
      [hiccup.form :refer :all]
      [hiccup.element :refer [image]]
      [hiccup.util :refer [url-encode]]
      [noir.io :refer [upload-file resource-path]]
      [noir.session :as session]
      [noir.response :as resp]
      [noir.util.route :refer [restricted]]
      [clojure.java.io :as io]
      [ring.util.response :refer [file-response]])
  (:import 
      [java.io File FileInputStream FileOutputStream]
      [java.awt.image AffineTransformOp BufferedImage]
      java.awt.RenderingHints
      java.awt.geom.AffineTransform
      javax.imageio.ImageIO))


(def gallery-path 
  "judge-img-galleries")

(defn serve-file [file-name]
  (file-response (str (gallery-path) File/separator file-name)))


(defn handle-upload [{:keys [filename] :as file}]
  (if (empty? filename)
    "Selecione uma imagem para carregar"
    (try
      (noir.io/upload-file (gallery-path) file :create-path? true)
      (image {:height "150px"}
             (str "/img/" (url-encode filename)))
      (catch Exception ex
        (str "error uploading file " (.getMessage ex))))))

(defroutes upload-routes
    (POST "/upload" [file] (handle-upload file))
    (GET "/img/:file-name" [file-name] (serve-file file-name)))