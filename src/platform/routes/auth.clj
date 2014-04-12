(ns platform.routes.auth
	(:use 
   		compojure.core)
	(:require 
		[platform.views.layout :as layout]
  		[platform.routes.uploader :refer [gallery-path]]
		[noir.session :as session]
		[noir.cookies :as cookies]
		[taoensso.timbre :as timbre]
		[noir.response :as resp]
		[noir.validation :as vali]
		[noir.util.crypt :as crypt]
		[noir.util.route :as route]
		[platform.models.db :as db])
 	(:import 
		[java.io File FileInputStream FileOutputStream]))


(defn valid? [email password passwordc]
	(vali/rule (vali/has-value? email)
            [:email "O campo e-mail é obrigatório"])
	;(vali/rule (not (= true (vali/is-email? email)))
	;          [:email "E-mail inválido"])
	(vali/rule (db/has-user-with? email)
    		[:email "Este e-mail já está sendo utilizado"])
	(vali/rule (vali/has-value? password)
    		[:password "O campo senha é obrigatório"])
	(vali/rule (vali/has-value? passwordc)
        	[:passwordc "O campo confirmar senha é obrigatório"])
	(vali/rule (vali/min-length? password 5)
	    	[:password "A senha deve conter pelo menos 5 caracteres"])
	(vali/rule (= password passwordc)
        	[:passwordc "As senhas digitas estão diferentes"])
	(not (vali/errors? :email :password :passwordc)))


(defn valid-login? [email password]
	(vali/rule (vali/has-value? email)
	         [:email "O campo e-mail é obrigatório"])
	(vali/rule (vali/has-value? password)
	         [:password "O campo senha é obrigatório"])
	(vali/rule (not (db/has-user-with? email))
	         [:email "Email não cadastrado"])
	(not (vali/errors? :email :password)))

(defn valid-email? [email]
	(vali/rule (vali/has-value? email)
	         [:email "O campo e-mail é obrigatório"])
	(vali/rule (db/has-user-with? email)
	         [:email "Este e-mail já está sendo utilizado"])
	(not (vali/errors? :email)))

(defn valid-pwd? [password encripted-pass]
	(vali/rule (crypt/compare password encripted-pass)
	         [:password "Senha inválida"])
	(not (vali/errors? :password)))

(defn login []
	(layout/render 
   		"login.html"
     	{:email-error (vali/on-error :email first)
		 :password-error (vali/on-error :password first)}))


(defn register [& [email]]
	(layout/render 
   		"registration.html"
		{:email-error (vali/on-error :email first)
		 :password-error (vali/on-error :password first)
		 :passwordc-error (vali/on-error :passwordc first)}))


(defn create-gallery-path []
	(let [user-path (File. (gallery-path))]
		(if-not (.exists user-path) (.mkdirs user-path))
		(str (.getAbsolutePath user-path) File/separator)))

(defn coming-soon [& [success]]
	(layout/render 
	   	"coming-soon.html"
		{:message "Estamos trabalhando para lançar a nossa primeira versão.
		           Deixe seu e-mail que entraremos em contato para compartilhar as novidades."
		 :email-error (vali/on-error :email first)
		 :success success}))


(defn handle-coming-soon [email]
	(if (valid-email? email)
		(try
    		(do
				(db/create-user email (crypt/encrypt "gpass"))
			    (coming-soon "Muito Obrigado!!! Em breve você saberá nossas novidades."))
		  (catch Exception ex
			    (vali/rule false [:email (.getMessage ex)])
			    (coming-soon nil)))
		(coming-soon nil)))


(defn handle-registration [email password passwordc]
	(if (valid? email password passwordc)
		(try
			  (do
					(let [user-id (db/create-user email (crypt/encrypt password))]
				    	(session/put! :user email)
 						(session/put! :user-id user-id)
     					(cookies/put! :user-id user-id))
        			(create-gallery-path)
				    (resp/redirect "/login"))
			  (catch Exception ex
				    (vali/rule false [:email (.getMessage ex)])
				    (register)))
		(register email)))


(defn handle-login [email password]
  ;ta limpando a sessao qndo faz redirect para o /
	(if (valid-login? email password)
		(try
    		(do
				(let [user (db/get-user email)]
					  (if (and user (valid-pwd? password (:password user)))
					        ((session/put! :user email)
	             			 (session/put! :user-id (.toString (:id user)))
					         (resp/redirect "/"))
				        	(login))))
			(catch Exception ex
				  (vali/rule false [:email (.getMessage ex)]) ;colocar um box pra msg
				  (login)))
		(login)))


(defn logout []
	(session/clear!)
	(session/remove! :user)
	(resp/redirect "/login"))


(defn profile []
	(layout/render 
	   	"profile.html"
		{:user (db/get-user (session/get :user))}))


(defn update-profile [{:keys [fullname]}]
	(db/update-user (session/get :user) fullname)
	(profile))

(defn handle-root-request []
	(if (empty? (session/get :user-id))
		(resp/redirect "/login")
		(resp/redirect "/home")))
  
(defroutes auth-routes
	(GET "/" []
		(handle-root-request))
   
	(POST "/register" 
		[email password passwordc]
		(handle-registration email password passwordc))
    
	(POST "/coming-soon" 
		[email]
		(handle-coming-soon email))
  
	(POST "/login" 
		[email password] 
		(handle-login email password))

	(GET "/login" [] 
		(login))

	(GET "/register" [] 
		(register))

	;(GET "/register" [] 
	;	(coming-soon))

	(GET "/profile" [] 
		(profile))
	(GET "/logout" [] 
		(logout))
	(POST "/update-profile" {params :params} 
		(update-profile params)))
