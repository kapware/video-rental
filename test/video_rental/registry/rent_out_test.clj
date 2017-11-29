(ns video-rental.registry.rent-out-test
  (:require [clojure.test :refer :all]
            [video-rental.registry.rent-out :as sut]
            [mount.core :as mount]
            [clojure.java.jdbc :as j]
            [video-rental.test-schema :as test-schema]))

(def mem-db "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

(defn wrap-test [f]
  (test-schema/prepare mem-db)

  (j/insert-multi! mem-db :film
                   [{:tid "tt0133093", :title "Matrix (1999)", :year 1999}
                    {:tid "tt0025316", :title "Es geschah in einer Nacht (1934)", :year 1934}
                    {:tid "tt0031381", :title "Vom Winde verweht (1939)", :year 1939}
                    {:tid "tt0234215", :title "Matrix Reloaded (2003)", :year 2003}
                    {:tid "tt0027977", :title "Moderne Zeiten (1936)", :year 1936}
                    {:tid "tt0242653", :title "Matrix Revolutions (2003)", :year 2003}])
  (j/insert! mem-db :user {:id 1 :bonus 0})
  (j/insert! mem-db :rent {:id 1 :created #inst"2017-11-23T20:34:39.957-00:00" :userid 1})
  (j/insert! mem-db :rentfilm {:rentid 1 :tid "tt0027977" :days 13 :charge 240 :created #inst"2017-11-23T20:34:39.957-00:00"})

  (mount/start-with {#'video-rental.db/db mem-db})
  (f)
  (mount/stop))

(use-fixtures :each wrap-test)

(deftest calculate-charge-upon-rent-out
  (testing "should allow renting out films"
    (let [;; given:
          current-year 2017
          user-id 1
          rent {:rent-films [{:tid "tt0133093" :days 5}
                             {:tid "tt0025316" :days 2}
                             {:tid "tt0031381" :days 1}]}
          ;; when:
          result (sut/rent-out current-year user-id rent)]
      ;; then:
      (is (= {:rent-films [{:tid "tt0133093" :days 5 :charge 30M}
                           {:tid "tt0025316" :days 2 :charge 30M}
                           {:tid "tt0031381" :days 1 :charge 30M}]}
             (dissoc result :rentid)))
      (is (some? (:rentid result))))))