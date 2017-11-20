(ns video-rental.inventory.search-test
  (:require [clojure.test :refer :all]
            [video-rental.inventory.search :as sut]
            [mount.core :as mount]
            [clojure.java.jdbc :as j]))

(def mem-db "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

(defn setup-db [f]
  (j/execute! mem-db (str "CREATE TABLE IF NOT EXISTS film ("
                          "tid        varchar(50) CONSTRAINT firstkey PRIMARY KEY,"
                          "title      varchar(200) NOT NULL,"
                          "year       integer NOT NULL)"))
  (j/execute! mem-db "TRUNCATE TABLE film")
  (j/insert-multi! mem-db :film
             [{:tid "tt0133093", :title "Matrix (1999)", :year 1999}
              {:tid "tt0025316", :title "Es geschah in einer Nacht (1934)", :year 1934}
              {:tid "tt0031381", :title "Vom Winde verweht (1939)", :year 1939}
              {:tid "tt0234215", :title "Matrix Reloaded (2003)", :year 2003}
              {:tid "tt0027977", :title "Moderne Zeiten (1936)", :year 1936}
              {:tid "tt0242653", :title "Matrix Revolutions (2003)", :year 2003}])
  (f))

(defn inject-mem-db [f]
  (mount/start-with {#'video-rental.db/db mem-db})
  (f)
  (mount/stop))

(use-fixtures :once setup-db)
(use-fixtures :each inject-mem-db)

(deftest return-inserted-films
  (testing "should find films by title"
    (is (= (sut/find-by-title "Matrix")
           [{:tid "tt0133093", :title "Matrix (1999)", :year 1999}
            {:tid "tt0234215", :title "Matrix Reloaded (2003)", :year 2003}
            {:tid "tt0242653", :title "Matrix Revolutions (2003)", :year 2003}]))))
