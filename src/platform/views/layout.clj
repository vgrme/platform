(ns platform.views.layout
	(:require 
		[selmer.parser :as parser]
		[clojure.string :as s]
		[ring.util.response :refer [content-type response]]
		[compojure.response :refer [Renderable]]
		[noir.session :as session]))

(def template-path "platform/views/templates/")

(deftype RenderableTemplate [template params]
	Renderable
		(render [this request]
			(content-type
				(->> (assoc params 
							(keyword (s/replace template #".html" "-selected")) 
       						"active"
							:context (:context request)
							:user (session/get :user-id))
					(parser/render-file (str template-path template))
				response)
			"text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

