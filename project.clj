 (defproject video-rental "0.1.0-SNAPSHOT"
   :description "Demo video-rental application"
   :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                  [metosin/compojure-api "2.0.0-alpha13"]
                  [org.clojure/java.jdbc "0.7.3"]
                  [org.postgresql/postgresql "42.1.4"]
                  [metosin/spec-tools "0.5.1"]
                  [mount "0.1.11"]
                  [korma "0.4.0"]]
   :min-lein-version "2.0.0"
   :ring {:handler video-rental.handler/app
          :init mount.core/start
          :destroy mount.core/stop
          :nrepl {:start? true :port 55555}}
   :uberjar-name "server.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                   [cheshire "5.5.0"]
                                   [ring/ring-mock "0.3.2"]
                                   [clojure-csv/clojure-csv "2.0.1"]
                                   [com.h2database/h2 "1.4.196"]]
                    :plugins [[lein-ring "0.12.0"]]
                    :source-paths ["dev"]}})
