(ns platform.models.db
	(:require 
	   	[monger.core :as mongo]
		[monger.collection :as coll]
		[monger.operators :refer :all]
		[taoensso.timbre :as timbre])
	(:import 
   		[org.bson.types ObjectId]))

;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost
(let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://127.0.0.1/geduca-platform")]
	(mongo/connect-via-uri! uri))		


(defn create-user [email enc-pwd]
	(let [oid (ObjectId.)]
		(coll/insert "users" { :_id oid :email email :password enc-pwd })
		(.toString oid)))


(defn update-user [email fullname]
	(coll/update "users" 
				{:email email}
				{$set 
					{:fullname fullname}}))


(defn get-user [email]
	(coll/find-one-as-map "users" {:email email}))


(defn get-user-by-id [id]
	(coll/find-one-as-map "users" {:_id (ObjectId. id)}))


(defn has-user-with? [email]
	(nil? (coll/find-one-as-map "users" {:email email})))


(defn add-image [userid filename]
  	(coll/insert "images" {:userid userid :name filename}))


(defn images-by-user [userid]
  	(coll/find-maps "images" {:userid userid}))