(ns video-rental.ledger.charge-test
  (:require [clojure.test :refer :all]
            [video-rental.ledger.charge :as sut]))

(deftest film-types
  (testing "Should support regular (up to 5 years old), new-release (at most this and last year) and old types"
    (is (= :regular (sut/film-type {:year 2015} 2017)))
    (is (= :new-release (sut/film-type {:year 2017} 2017)))
    (is (= :new-release (sut/film-type {:year 2016} 2017)))
    (is (= :new-release (sut/film-type {:year 2018} 2017)))
    (is (= :old (sut/film-type {:year 2012} 2017)))
    (is (= :regular (sut/film-type {:year 2016} 2020)))))

(deftest rent-charges
  (testing "Should charge for rental based on types and days"
    (is (= 40M  (sut/charge 2017 {:year 2016} {:days 1})))
    (is (= 400M (sut/charge 2017 {:year 2016} {:days 10})))
    (is (= 30M  (sut/charge 2017 {:year 2015} {:days 1})))
    (is (= 30M  (sut/charge 2017 {:year 2015} {:days 3})))
    (is (= 90M  (sut/charge 2017 {:year 2015} {:days 5})))
    (is (= 90M  (sut/charge 2017 {:year 1965} {:days 7})))))

(deftest rent-surcharges
  (testing "Should surcharge for rental based on types and days exceeded"
    (is (= 0M   (sut/surcharge 2017 {:year 2016} {:days 0})))
    (is (= 400M (sut/surcharge 2017 {:year 2016} {:days 10})))
    (is (= 30M  (sut/surcharge 2017 {:year 2015} {:days 1})))
    (is (= 90M  (sut/surcharge 2017 {:year 2015} {:days 3})))
    (is (= 150M (sut/surcharge 2017 {:year 2015} {:days 5})))
    (is (= 210M (sut/surcharge 2017 {:year 1965} {:days 7})))))