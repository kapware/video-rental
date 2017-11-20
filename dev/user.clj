(ns user
  (:require [clojure.test :refer [run-tests]]
            [video-rental.core-test]
            [video-rental.inventory.film-test]
            [video-rental.inventory.film :as film]
            [video-rental.inventory.search-test]
            [clojure.spec.alpha :as spec]
            [clojure.java.jdbc :as j]
            [clojure-csv.core :as csv]))

(def db {:dbtype "postgresql"
         :dbname "video-rental"
         :host "0.0.0.0"
         :user "video-rental"
         :password "video-rental"})

(def mem-db "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

(defn ->map [rows]
  "Converts to map, assuming first row contains keywords"
  (let [header (->> (first rows) (map (comp keyword clojure.string/lower-case)))
        values (rest rows)]
    (map #(zipmap header %) values)))

#_(->> "imdb.csv"
     slurp
     csv/parse-csv
     ->map
     (map #(select-keys % [:tid :title :year]))
     (map #(update % :year read-string))
     (filter (fn [{:keys [title]}] (.contains title "Matrix")) )
     (take 30))

#_(->> "imdb.csv"
     slurp
     csv/parse-csv
     ->map
     (map #(select-keys % [:tid :title :year]))
     (map #(update % :year read-string))
     (map #(j/insert! db :film %)))

#_(j/query db
           ["SELECT * FROM film where title like ?" "%Matrix%"])

(defn run-all-tests []
  (run-tests 'video-rental.core-test
             'video-rental.inventory.film-test
             'video-rental.inventory.search-test))

