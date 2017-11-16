 (defproject video-rental "0.1.0-SNAPSHOT"
   :description "Demo video-rental application"
   :dependencies [[org.clojure/clojure "1.8.0"]
                  [metosin/compojure-api "1.1.11"]]
   :ring {:handler video-rental.handler/app
          :nrepl {:start? true :port 55555}}
   :uberjar-name "server.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                  [cheshire "5.5.0"]
                                  [ring/ring-mock "0.3.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
