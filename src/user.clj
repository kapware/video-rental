(ns user
  (:require [clojure.test :refer [run-tests]]
            [video-rental.core-test]))


(defn run-all-tests []
  (run-tests 'video-rental.core-test))

