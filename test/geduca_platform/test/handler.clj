(ns geduca-platform.test.handler
  (:use clojure.test
        ring.mock.request
        geduca-platform.handler))

(deftest test-app
  (testing "hangle login"
    (let [response (app (request :post {:email "felipe.forbeck@gmail.com" :password "12345"} "/login"))]
      (is (= (:status response) 200)))))
