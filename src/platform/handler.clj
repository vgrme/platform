(ns platform.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [noir.util.middleware :refer [app-handler]]
            [noir.util.route :refer [restricted]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.cookies :as cookies]
            [platform.middleware :as middleware]
            [platform.routes.auth :refer [auth-routes]]
            [platform.routes.home :refer [home-routes]]
            [platform.routes.uploader :refer [upload-routes]]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn user-access [request]
  (session/get (cookies/get :user-id))
  :access-rules [{:redirect "/login"
                :rule user-access}])

(defn init
  "init will be called once whenvapp is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info,
     :enabled? true,
     :async? false,
     :max-message-per-msecs nil,
     :fn rotor/appender-fn})
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "geduca_platform.log", :max-size (* 512 1024), :backlog 10})
  (if (env :dev) (parser/cache-off!))
  (timbre/info "geduca-platform started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "geduca-platform is shutting down..."))

(def app
 (app-handler
   [auth-routes 
    home-routes 
    upload-routes 
    app-routes]
     :middleware
        [middleware/template-error-page 
         middleware/log-request]
     :access-rules 
        [user-access]
     :formats
        [:json-kw 
         :edn]))

