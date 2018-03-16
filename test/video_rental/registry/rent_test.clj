(ns video-rental.registry.rent-test
  (:require [clojure.test :refer :all]
            [video-rental.registry.rent :as sut]
            [clojure.spec.alpha :as spec]))

#_(deftest rent-request-examples
  (testing "Should support valid rent requests"
    (is (spec/valid? ::sut/rent {:rent-id 1
                                 :rent-films [{:tid "tt0133093" :days 7}
                                              {:tid "tt0145487" :days 11}
                                              {:tid "tt0316654" :days 12}]}))))

#_(deftest rent-order-response-examples
  (testing "Should support valid order rent responses"
    (is (spec/valid? ::sut/rent {:rent-films [{:tid "tt0133093" :days 7 :charge 90M}
                                              {:tid "tt0145487" :days 5 :charge 60M}
                                              {:tid "tt0316654" :days 12 :charge 180M}]
                                 :created #inst "2017-11-23T20:34:39.957-00:00"}))))

