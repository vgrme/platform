(ns platform.routes.auth
  (:use compojure.core)
  (:require [platform.views.layout :as layout]
            [noir.session :as session]
            [noir.cookies :as cookies]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [platform.models.db :as db]))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user ID is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass1 "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn register [& [id]]
  (layout/render "registration.html"
    {:id id
     :id-error (vali/on-error :id first)
     :pass-error (vali/on-error :pass first)
     :pass1-error (vali/on-error :pass1 first)}))

(defn login []
  (layout/render "login.html"))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (do
        (db/create-user {:id id :pass (crypt/encrypt pass)})
        (session/put! :user-id id)
        (cookies/put! :geduca-user-id (str id))
        (resp/redirect "/"))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))
    (register id)))

(defn profile []
  (layout/render
    "profile.html"
    {:user (db/get-user (session/get :user-id))}))

(defn update-profile [{:keys [first-name last-name email]}]
  (db/update-user (session/get :user-id) first-name last-name email)
  (profile))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare pass (:pass user)))
      (session/put! :user-id id))
    (resp/redirect "/")))

(defn logout []
  (session/clear!)
  (resp/redirect "/login"))

(defn is-logged [] 
  (nil? (session/get (cookies/get :geduca-user-id))))

(defroutes auth-routes
  (GET "/" [] (resp/redirect (if (is-logged) "/home" "/login")))
   
  (GET "/register" [] (register))

  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))

  (GET "/profile" [] (profile))
  
  (POST "/update-profile" {params :params} (update-profile params))
  
  (GET "/login" [] (login))
  
  (POST "/login" [id pass] (handle-login id pass))

  (GET "/logout" [] (logout)))
