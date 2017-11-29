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