(ns video-rental.core-test
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer :all]
            [video-rental.handler :refer :all]
            [ring.mock.request :as mock]
            [mount.core :as mount]
            [clojure.java.jdbc :as j]
            [video-rental.test-schema :as test-schema]
            [video-rental.util :as util]))

(def mem-db "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

(defn wrap-test [f]
  (test-schema/prepare mem-db)
  (j/insert-multi! mem-db :film
                   [{:tid "tt0027977" :title "Moderne Zeiten (1936)" :year 1936}
                    {:tid "tt0049406" :title "Killing (1956)" :year 1956}
                    {:tid "tt0266697" :title "Kill Bill: Vol. 1 (2003)" :year 2003}
                    {:tid "tt0361748" :title "Inglourious Basterds (2009)" :year 2009}])

  (j/insert! mem-db :user {:id 1 :bonus 0})
  (j/insert! mem-db :rent {:id 1 :created #inst"2003-11-05T20:34:39.957-00:00" :userid 1})
  (j/insert! mem-db :rentfilm {:rentid 1 :tid "tt0027977" :days 5 :charge 30 :created #inst"2003-11-05T20:34:39.957-00:00"})
  (j/insert! mem-db :rentfilm {:rentid 1 :tid "tt0266697" :days 5 :charge 200 :created #inst"2003-11-05T20:34:39.957-00:00"})

  (mount/start-with {#'video-rental.db/db mem-db})
  (with-redefs-fn {#'video-rental.util/now! (fn [] (util/to-zoned-date #inst"2003-11-20T20:34:39.957-00:00"))}
    #(f))
  (mount/stop))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(use-fixtures :each wrap-test)

#_(deftest film-test
  (testing "Test GET request to /film?name={a-name} returns expected response"
    (let [response (app (-> (mock/request :get  "/api/film?title=Kill")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= [{:tid "tt0049406" :title "Killing (1956)", :year 1956}
              {:tid "tt0266697" :title "Kill Bill: Vol. 1 (2003)", :year 2003}]
             body)))))

#_(deftest rent-test
  (testing "Test POST request to /rent should rent film out "
    (let [post-response (app (-> (mock/request :post "/api/rent")
                                 (mock/json-body {:rent-films [{:tid "tt0027977", :days 5}
                                                               {:tid "tt0266697", :days 2}
                                                               {:tid "tt0361748", :days 1}]})))
          post-body     (->> (parse-body (:body post-response)))]
      (is (= (:status post-response) 201))
      (is (= {:rent-films [{:tid "tt0027977" :days 5 :charge 30}
                           {:tid "tt0266697" :days 2 :charge 80}
                           {:tid "tt0361748" :days 1 :charge 40}]}
             (dissoc post-body :rentid)))
      (is (some? (:rentid post-body))))))

#_(deftest return-test
  (testing "Test POST request to /return should return film(s) and calculate surcharges "
    (let [post-response (app (-> (mock/request :post "/api/return")
                                 (mock/json-body {:rentid 1
                                                  :return-films [{:tid "tt0027977"}
                                                                 {:tid "tt0266697"}
                                                                 {:tid "tt0361748"}]})))
          post-body     (->> (parse-body (:body post-response)))]
      (is (= (:status post-response) 201))
      (is (= {:rentid 1
              :return-films [{:tid "tt0027977" :surcharge 300 :bonus 1}
                             {:tid "tt0266697" :surcharge 400 :bonus 2}]}
             post-body)))))