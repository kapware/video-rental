(ns video-rental.inventory.film-test
  (:require [clojure.test :refer :all]
            [video-rental.inventory.film :as sut]
            [clojure.spec.alpha :as spec]))

(deftest film-examples
  (testing "Should support valid films"
    (is (spec/valid? ::sut/film {:tid "tt0133093" :title "Matrix (1999)" :year 1999}))
    (is (spec/valid? ::sut/film {:tid "tt0145487" :title "Spider-Man (2002)" :year 2002}))
    (is (spec/valid? ::sut/film {:tid "tt0316654" :title "Spider-Man 2 (2004)" :year 2004}))
    (is (spec/valid? ::sut/film {:tid "tt0000248" :title "The Kiss in the Tunnel (1899)" :year 1899}))
    (is (spec/valid? ::sut/film {:tid "tt0974015" :title "Justice League (2017)" :year 2017}))))

(deftest film-types
  (testing "Should support regular (up to 5 years old), new-release (at most this and last year) and old types"
    (is (= :regular (sut/film-type {:year 2015} 2017)))
    (is (= :new-release (sut/film-type {:year 2017} 2017)))
    (is (= :new-release (sut/film-type {:year 2016} 2017)))
    (is (= :new-release (sut/film-type {:year 2018} 2017)))
    (is (= :old (sut/film-type {:year 2012} 2017)))
    (is (= :regular (sut/film-type {:year 2016} 2020)))))

(deftest rent-prices
  (testing "Should charge for rental based on types and days"
    (is (= 40M (sut/price 2017 {:year 2016} {:days  1})))
    (is (= 400M (sut/price 2017 {:year 2016} {:days 10})))
    (is (= 30M (sut/price 2017 {:year 2015} {:days 1})))
    (is (= 30M (sut/price 2017 {:year 2015} {:days 3})))
    (is (= 90M (sut/price 2017 {:year 2015} {:days 5})))
    (is (= 90M (sut/price 2017 {:year 1965} {:days 7})))))