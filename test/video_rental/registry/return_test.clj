(ns video-rental.registry.return-test
  (:require [clojure.test :refer :all]
            [video-rental.registry.return :as sut]
            [clojure.spec.alpha :as spec]))

#_(deftest return-request-examples
  (testing "Should support valid return requests"
    (is (spec/valid? ::sut/return {:rentid 1
                                   :return-films [{:tid "tt0133093"}
                                                  {:tid "tt0145487"}
                                                  {:tid "tt0316654"}]}))))

#_(deftest return-response-examples
  (testing "Should support valid return responses"
    (is (spec/valid? ::sut/return {:rentid 3
                                   :return-films [{:tid "tt0133093" :surcharge 0M   :bonus 2}
                                                  {:tid "tt0145487" :surcharge 60M  :bonus 1}
                                                  {:tid "tt0316654" :surcharge 180M :bonus 1}]
                                   :created #inst "2017-11-23T20:34:39.957-00:00"}))))