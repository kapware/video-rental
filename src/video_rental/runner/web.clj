(ns video-rental.runner.web
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :refer [site]]
            [environ.core :refer [env]]
            [video-rental.handler :refer [app]]))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (mount.core/start)
    (jetty/run-jetty (site #'app)
                     {:port port :join? false})))