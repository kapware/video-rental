(ns video-rental.inventory.search
  (:require [video-rental.db :refer [db]]
            [clojure.java.jdbc :as j]))

(defn find-by-title [title]
  (j/query db ["SELECT * FROM film WHERE title LIKE ?"
               (str "%" title "%")]))