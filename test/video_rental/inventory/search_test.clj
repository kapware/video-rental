(ns video-rental.inventory.search-test
  (:require [clojure.test :refer :all]
            [video-rental.inventory.search :as sut]
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

  (mount/start-with {#'video-rental.db/db mem-db})
  (f)
  (mount/stop))

(use-fixtures :each wrap-test)

(deftest find-by-title-test
  (testing "should find films by title"
    (is (= (sut/find-by-title "Matrix")
           [{:tid "tt0133093", :title "Matrix (1999)", :year 1999}
            {:tid "tt0234215", :title "Matrix Reloaded (2003)", :year 2003}
            {:tid "tt0242653", :title "Matrix Revolutions (2003)", :year 2003}]))))

(deftest find-by-tid-test
  (testing "should find films by tid"
    (is (= (sut/find-by-tid "tt0027977")
           {:tid "tt0027977", :title "Moderne Zeiten (1936)", :year 1936}))))

