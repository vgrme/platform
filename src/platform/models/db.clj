(ns platform.models.db
  	(:refer-clojure :exclude [sort find])
	(:require 
	   	[monger.core :as mongo]
		[monger.collection :as coll]
		[monger.operators :refer :all]
  		[monger.query :refer :all]
		[taoensso.timbre :as timbre])
	(:import 
   		[org.bson.types ObjectId]))

;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost
(let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://127.0.0.1/geduca-platform")]
	(mongo/connect-via-uri! uri))		

 (defn now [] (new java.util.Date))

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


(defn add-image [user-id filename]
  	(coll/insert "images" {:userid user-id :name filename :created (now)}))


(defn remove-image [user-id filename]
  	(coll/remove "images" {:userid user-id :name filename}))


(defn images-by-user [user-id]
	(with-collection "images"
		(find {:userid user-id})
		(fields [:userid :name :created])
		(sort (array-map :created 1))
		(limit 100)))

