(ns video-rental.core-test
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer :all]
            [video-rental.handler :refer :all]
            [ring.mock.request :as mock]
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
                   [{:tid "tt0027977", :title "Moderne Zeiten (1936)", :year 1936}
                    {:tid "tt0049406", :title "Killing (1956)", :year 1956}
                    {:tid "tt0266697", :title "Kill Bill: Vol. 1 (2003)", :year 2003}
                    {:tid "tt0361748", :title "Inglourious Basterds (2009)", :year 2009}])

  (f))

(defn inject-mem-db [f]
  (mount/start-with {#'video-rental.db/db mem-db})
  (f)
  (mount/stop))


(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(use-fixtures :once setup-db)
(use-fixtures :each inject-mem-db)

(deftest film-test
  (testing "Test GET request to /film?name={a-name} returns expected response"
    (let [response (app (-> (mock/request :get  "/api/film?title=Kill")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= body [{:tid "tt0049406", :title "Killing (1956)", :year 1956}
                   {:tid "tt0266697", :title "Kill Bill: Vol. 1 (2003)", :year 2003}]))
      (mount/stop))))
