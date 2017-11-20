(ns video-rental.db
  (:require [mount.core :refer [defstate]]))

(def config {:dbtype "postgresql"
             :dbname "video-rental"
             :host "0.0.0.0"
             :user "video-rental"
             :password "video-rental"})

(defstate db :start config)