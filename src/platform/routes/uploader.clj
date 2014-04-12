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

(def thumb-size 150)
(def thumb-prefix "thumb_")
(def galleries "galleries")

(defn gallery-path []
	(str galleries File/separator (session/get :user-id)))

(defn serve-file [user-id file-name]
	(file-response (str galleries File/separator user-id File/separator file-name)))

(defn scale [img ratio width height]
  	"Scale an image using the respective params"
	(let [scale (AffineTransform/getScaleInstance (double ratio) (double ratio))
   		transform-op (AffineTransformOp. scale AffineTransformOp/TYPE_BILINEAR)]
   	(.filter transform-op img (BufferedImage. width height (.getType img)))))


(defn scale-image [file]
	(let [img 			(ImageIO/read file)
		  img-width 	(.getWidth img)
		  img-height 	(.getHeight img)
		  ratio 		(/ thumb-size img-height)]
	(scale img ratio (int (* img-width ratio)) thumb-size)))

(defn save-thumbnail [filename]
	(let [path (str (gallery-path) File/separator)]
		(ImageIO/write
			(scale-image (io/input-stream (str path filename)))
		"jpeg"
		(File. (str path thumb-prefix filename)))))

(defn upload-page [info]
	(layout/render "upload-image.html" {:p info}))

(defn handle-upload [file]
		(upload-page
			(if (or (nil? file) (empty? (:filename file)))
				"Selecione um arquivo para ser carregado."
				(try
					(noir.io/upload-file (gallery-path) file :create-path? true)
     				(save-thumbnail (:filename file))
					(image {:height "150px"}
					(str "/img/" thumb-prefix (url-encode (:filename file))))
				(catch Exception ex
					(str 
       					"Não foi possivel carregar o arquivo " 
            			(:filename file) 
           				", Erro: " 
           				(.getMessage ex)))))))

(defroutes upload-routes
	(GET "/upload" [info] (upload-page info))
  	(POST "/upload" [file] (handle-upload file))
	(GET "/img/:file-name" [file-name] (serve-file file-name))
 	(GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name)))