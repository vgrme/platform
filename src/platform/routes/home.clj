(ns platform.routes.home
	(:use 
   		compojure.core)
	(:require 
	   	[platform.views.layout :as layout]
	    [platform.controllers.activity :as activity-controller]
	    [platform.controllers.lesson :as lesson-controller]
	    [platform.controllers.class :as class-controller]
	    [platform.util :as util]
	    [noir.response :as resp]
	    [noir.session :as session]
	    [noir.cookies :as cookies]
	    [noir.util.route :as route]
	    [noir.util.route :refer [def-restricted-routes]]))


(defn dashboard []
	(layout/render "dashboard.html"))


(def-restricted-routes private-routes
	(GET "/home" [] 
		(dashboard))
	(GET "/aulas" [] 
		(lesson-controller/lessons))
	(GET "/nova/aula" [] 
		(lesson-controller/new-lesson))
	(GET "/turmas" [] 
		(class-controller/classes))
	(GET "/nova/turma" [] 
		(class-controller/new-class))
	(GET "/nova/atividade/right-wrong" [] 
		(activity-controller/right-wrong-quiz))
	(GET "/nova/atividade/judge-image" [] 
		(activity-controller/judge-image-quiz (session/get :user-id)))
	(POST "/nova-atividade" []
		(activity-controller/new-activity)))
