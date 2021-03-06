(ns platform.routes.uploader
	(:use 
		compojure.core)
	(:require
		[platform.views.layout :as layout]
		[hiccup.util :refer [url-encode]]
		[noir.io :refer [upload-file resource-path]]
		[noir.session :as session]
		[noir.response :as resp]
		[noir.util.route :refer [restricted]]
		[clojure.java.io :as io]
		[ring.util.response :refer [file-response]]
  		[platform.models.db :as db])
	(:import 
		[java.io File FileInputStream FileOutputStream]
		[java.awt.image AffineTransformOp BufferedImage]
		java.awt.RenderingHints
		java.awt.geom.AffineTransform
		javax.imageio.ImageIO))

(def thumb-size 150)
(def thumb-prefix "thumb_")
(def galleries "galleries")

(defn image-uri [userid file-name]
	(str "/img/" userid "/" (url-encode file-name)))

(defn thumb-uri [userid file-name]
	(image-uri userid (str thumb-prefix file-name)))

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

(defn remove-image [filename]
	(let [path (str (gallery-path) File/separator)]
		(ImageIO/write
			(scale-image (io/input-stream (str path filename)))
		"jpeg"
		(File. (str path thumb-prefix filename)))))


(defn upload-page [info]
	(layout/render "upload-image.html" info))

(defn handle-upload [file filename]
	(if (or (nil? file) (empty? filename))
		(resp/json {:msg "Selecione uma imagem para ser carregada." :code "NOK"})
		(try
			(noir.io/upload-file (gallery-path) file :create-path? true)
			(save-thumbnail filename)
			(db/add-image (session/get :user-id) filename)
			(resp/json {:msg "A imagem foi carregada com sucesso." :code "OK"})
		(catch Exception ex
			(resp/json {:msg (str "Não foi possivel carregar a imagem. Erro: " (.getMessage ex)) :code "NOK"})))))

(defn handle-remove [filename]
  (if (empty? filename)
		(resp/json {:msg "Selecione uma imagem para ser removida." :code "NOK"})
		(try
    		(io/delete-file (str (gallery-path) File/separator filename))
			(io/delete-file (str (gallery-path) File/separator thumb-prefix filename))
			(db/remove-image (session/get :user-id) filename)
			(resp/json {:msg "A imagem foi removida com sucesso." :code "OK"})
		(catch Exception ex
			(resp/json {:msg (str "Não foi possivel remover a imagem. Erro: " (.getMessage ex)) :code "NOK"})))))

(defroutes upload-routes
  	(POST "/upload/img" [file] 
        (handle-upload file (:filename file)))
   
   (POST "/remove/img/" [filename] 
        (handle-remove filename))
        
	(GET "/img/:file-name" [file-name] 
    	(serve-file file-name))
 
 	(GET "/img/:user-id/:file-name" [user-id file-name] 
    	(serve-file user-id file-name)))