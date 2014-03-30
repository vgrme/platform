(ns platform.models.db
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              [taoensso.timbre :as timbre]))

;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost
(let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://127.0.0.1/geduca-platform")]
  (mg/connect-via-uri! uri))

(defn create-user [user]
  (timbre/info "creating new user: " (str user))
  (mc/insert "users" user))

(defn update-user [email fullname]
  (timbre/info "update user: " (str email))
  (mc/update "users" 
             {:email email}
             {$set 
              {:fullname fullname}}))

(defn get-user [email]
  (mc/find-one-as-map "users" {:email email}))

(defn get-user-by-id [id]
  (mc/find-one-as-map "users" {:id id}))

(defn has-user-with? [email]
  (nil? (mc/find-one-as-map "users" {:email email})))
