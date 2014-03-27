(ns platform.routes.auth
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [noir.session :as session]
            [noir.cookies :as cookies]
            [taoensso.timbre :as timbre]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [platform.models.db :as db]))

(defn valid? [email password passwordc]
  
  (vali/rule (vali/has-value? email)
             [:email "O campo e-mail é obrigatório"])
  (vali/rule (db/has-user-with? email)
             [:email "Este e-mail já está sendo utilizado"])
  (vali/rule (vali/has-value? password)
             [:email "O campo senha é obrigatório"])
  (vali/rule (vali/has-value? passwordc)
             [:email "O campo confirmar senha é obrigatório"])
  (vali/rule (vali/min-length? password 5)
             [:password "A senha deve conter pelo menos 5 caracteres"])
  (vali/rule (= password passwordc)
             [:passwordc "As senhas digitas estão diferentes"])
  
  (not (vali/errors? :email :password :passwordc)))


(defn register [& [email]]
  (layout/render "registration.html"
    {:email-error (vali/on-error :email first)
     :password-error (vali/on-error :password first)
     :passwordc-error (vali/on-error :passwordc first)}))

(defn login []
  (layout/render "login.html"))

(defn handle-registration [email password passwordc]
  (if (valid? email password passwordc)
    (try
      (do
        (db/create-user {:email email :pass (crypt/encrypt password)})
        (session/put! :user-id email)
        (cookies/put! :user-id email)
        (resp/redirect "/"))
      (catch Exception ex
        (vali/rule false [:email (.getMessage ex)])
        (register)))
    (register email)))

(defn profile []
  (layout/render
    "profile.html"
    {:user (db/get-user (session/get :user-id))}))

(defn update-profile [{:keys [first-name last-name email]}]
  (db/update-user (session/get :user-id) first-name last-name email)
  (profile))

(defn handle-login [email password]
  (let [user (db/get-user email)]
    (if (and user (crypt/compare password (:pass user)))
      (session/put! :user-id email))
    (resp/redirect "/")))

(defn logout []
  (session/clear!)
  (resp/redirect "/login"))

(defn is-logged [] 
  (if (empty? (str 
                (session/get 
                  (cookies/get :user-id)))) false true))

(defroutes auth-routes
  (GET "/" [] (resp/redirect 
                (if (is-logged) "/home" "/login")))
   
  (GET "/register" [] (register))

  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))

  (GET "/profile" [] (profile))
  
  (POST "/update-profile" {params :params} (update-profile params))
  
  (GET "/login" [] (login))
  
  (POST "/login" [id pass] (handle-login id pass))

  (GET "/logout" [] (logout)))
