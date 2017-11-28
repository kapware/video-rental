(ns video-rental.core-test
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer :all]
            [video-rental.handler :refer :all]
            [ring.mock.request :as mock]
            [mount.core :as mount]
            [clojure.java.jdbc :as j]
            [video-rental.test-schema :as test-schema]))

(def mem-db "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

(defn wrap-test [f]
  (test-schema/prepare mem-db)
  (j/insert-multi! mem-db :film
                   [{:tid "tt0027977" :title "Moderne Zeiten (1936)" :year 1936}
                    {:tid "tt0049406" :title "Killing (1956)" :year 1956}
                    {:tid "tt0266697" :title "Kill Bill: Vol. 1 (2003)" :year 2003}
                    {:tid "tt0361748" :title "Inglourious Basterds (2009)" :year 2009}])

  (mount/start-with {#'video-rental.db/db mem-db})
  (with-redefs-fn {#'video-rental.date-util/current-year! (fn [] 2003)}
    #(f))
  (mount/stop))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(use-fixtures :each wrap-test)

(deftest film-test
  (testing "Test GET request to /film?name={a-name} returns expected response"
    (let [response (app (-> (mock/request :get  "/api/film?title=Kill")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= [{:tid "tt0049406" :title "Killing (1956)", :year 1956}
              {:tid "tt0266697" :title "Kill Bill: Vol. 1 (2003)", :year 2003}]
             body)))))

(deftest rent-test
  (testing "Test POST request to /rent should rent film out "
    (let [post-response (app (-> (mock/request :post "/api/rent")
                            (mock/json-body {:rent-films [{:tid "tt0027977", :days 5}
                                                          {:tid "tt0266697", :days 2}
                                                          {:tid "tt0361748", :days 1}]})))
          post-body     (->> (parse-body (:body post-response)))
          post-headers  (:headers post-response)]
      (is (= (:status post-response) 201))
      (is (= {:rent-films [{:tid "tt0027977" :days 5 :charge 30}
                           {:tid "tt0266697" :days 2 :charge 80}
                           {:tid "tt0361748" :days 1 :charge 40}]}
             post-body)))))