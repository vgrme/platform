(defproject
  geduca-platform
  "0.1.0-SNAPSHOT"
  :repl-options
  {:init-ns platform.repl}
  :dependencies
  [[ring-server "0.3.1"]
   [com.novemberain/monger "1.7.0"]
   [com.taoensso/timbre "3.0.0"]
   [environ "0.4.0"]
   [markdown-clj "0.9.41"]
   [selmer "0.6.1"]
   [com.taoensso/tower "2.0.1"]
   [org.clojure/clojure "1.5.1"]
   ;[ring-anti-forgery "0.2.1"] 
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [lib-noir "0.8.1"]
   [compojure "1.1.6"]]
  :ring
  {:handler platform.handler/app,
   :init platform.handler/init,
   :destroy platform.handler/destroy}
  :cucumber-feature-paths
  ["test/features/"]
  :profiles
  {:uberjar {:aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies
    [[org.clojure/core.cache "0.6.3"]
     [ring/ring-devel "1.2.1"]
     [clj-webdriver/clj-webdriver "0.6.1"]
     [ring-mock "0.1.5"]],
    :env {:dev true}}}
  :url
  "http://geduca.org/app"
  :plugins
  [[lein-ring "0.8.10"] [lein-environ "0.4.0"] [lein-cucumber "1.0.2"]]
  :description
  "Geduca.org: A Collaborative Learning Platform for Elementary School"
  :min-lein-version "2.0.0")