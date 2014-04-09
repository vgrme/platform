(ns platform.routes.auth
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [noir.session :as session]
            [noir.cookies :as cookies]
            [taoensso.timbre :as timbre]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [noir.util.route :as route]
            [platform.models.db :as db]))


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
  (layout/render "login.html"
                 {:email-error (vali/on-error :email first)
                  :password-error (vali/on-error :password first)}))


(defn register [& [email]]
  (layout/render "registration.html"
    {:email-error (vali/on-error :email first)
     :password-error (vali/on-error :password first)
     :passwordc-error (vali/on-error :passwordc first)}))

(defn coming-soon [& [success]]
  (layout/render "coming-soon.html"
    {:message "Estamos trabalhando para lançar a nossa primeira versão.
               Deixe seu e-mail que entraremos em contato para compartilhar as novidades."
     :no-spam "Sem spam e sem compartilhamento de e-mails :) "
     :email-error (vali/on-error :email first)
     :success success}))


(defn handle-coming-soon [email]
  (if (valid-email? email)
    (try
      (do
        (db/create-user {:email email :password (crypt/encrypt "gpass")})
        (coming-soon "Muito Obrigado!!! Em breve você saberá nossas novidades."))
      (catch Exception ex
        (vali/rule false [:email (.getMessage ex)])
        (coming-soon nil)))
    (coming-soon nil)))


(defn handle-registration [email password passwordc]
  (if (valid? email password passwordc)
    (try
      (do
        (db/create-user {:email email :password (crypt/encrypt password)})
        (session/put! :user email)
        (resp/redirect "/login"))
      (catch Exception ex
        (vali/rule false [:email (.getMessage ex)])
        (register)))
    (register email)))


(defn handle-login [email password]
  (if (valid-login? email password)
      (try
        (let [user (db/get-user email)]
          (if (and user (valid-pwd? password (:password user)))
                ((session/put! :user email)
                 (cookies/put! :user email)
                  (resp/redirect "/"))
                (login)))
        (catch Exception ex
          (vali/rule false [:email (.getMessage ex)]) ;colocar um box pra msg
          (login)))
      (login)))


(defn logout []
  (session/clear!)
  (session/remove! :user)
  (resp/redirect "/login"))


(defn profile []
  (layout/render "profile.html"
    {:user (db/get-user (session/get :user))}))


(defn update-profile [{:keys [fullname]}]
  (db/update-user (session/get :user) fullname)
  (profile))

(defn handle-root-request []
  (if (nil? (session/get :user))
    (resp/redirect "/login")
    (resp/redirect "/home")))

  
(defroutes auth-routes
  (GET "/" [] 
       (handle-root-request))
   
  ;(POST "/register" 
   ;     [email password passwordc]
    ;    (handle-registration email password passwordc))
    
  (POST "/coming-soon" 
        [email]
        (handle-coming-soon email))
  
  (POST "/login" 
        [email password] 
        (handle-login email password))
  
  (GET "/login" [] 
       (login))
  
  ;(GET "/register" [] 
       ;(register))
  
  (GET "/register" [] 
       (coming-soon))
  
  (GET "/profile" [] 
       (profile))
  (GET "/logout" [] 
       (logout))
  (POST "/update-profile" {params :params} 
        (update-profile params)))
