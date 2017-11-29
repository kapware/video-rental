(ns video-rental.registry.to-return-test
  (:require [clojure.test :refer :all]
            [video-rental.registry.to-return :as sut]
            [mount.core :as mount]
            [clojure.java.jdbc :as j]
            [video-rental.test-schema :as test-schema]
            [video-rental.util :as util]))

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
  (j/insert! mem-db :rentfilm {:rentid 1 :tid "tt0027977" :days 5 :charge 30 :created #inst"2003-11-23T20:34:39.957-00:00"})
  (j/insert! mem-db :rentfilm {:rentid 1 :tid "tt0031381" :days 5 :charge 30 :created #inst"2003-11-23T20:34:39.957-00:00"})
  (j/insert! mem-db :rentfilm {:rentid 1 :tid "tt0242653" :days 5 :charge 200 :created #inst"2003-11-23T20:34:39.957-00:00"})

  (mount/start-with {#'video-rental.db/db mem-db})
  (with-redefs-fn {#'video-rental.util/now! (fn [] (util/to-zoned-date #inst"2003-11-30T20:34:39.957-00:00"))}
    #(f))
  (mount/stop))

(use-fixtures :each wrap-test)

(deftest calculate-surcharge-upon-return
  (testing "should allow to return films"
    (let [;; given:
          now (util/to-zoned-date #inst"2003-11-30T20:34:39.957-00:00")
          user-id 1
          a-return {:rentid 1
                    :return-films [{:tid "tt0027977"}
                                   {:tid "tt0031381"}
                                   {:tid "tt0242653"}]}
          ;; when:
          result (sut/to-return now user-id a-return)]
      ;; then:
      (is (= {:rentid 1
              :return-films [{:tid "tt0027977" :surcharge 60M :bonus 1}
                             {:tid "tt0031381" :surcharge 60M :bonus 1}
                             {:tid "tt0242653" :surcharge 80M :bonus 2}]}
             result)))))