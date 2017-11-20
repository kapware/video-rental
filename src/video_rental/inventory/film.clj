(ns video-rental.inventory.film
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::film
  (spec/keys :req-un [::tid ::title ::year]))

(spec/def ::tid string?)

(spec/def ::title string?)

(spec/def ::year (spec/and integer? pos?))

(defn film-type [{year :year} current-year]
  (let [last-year (dec current-year)
        old-year (- current-year 5)]
    (cond
      (>= year last-year) :new-release
      (< old-year year last-year) :regular
      :else :old)))

(def prices {:premium 40 :basic 30})

(defn- calculate-price-with-limit [days rate day-limit]
  (if (<= days day-limit)
         rate
         (* rate (- days (dec day-limit)))))

(defmulti price (fn [current-year film days] (film-type film current-year)))

(defmethod price :new-release [current-year film days]
  (* (:premium prices) days))

(defmethod price :regular [current-year film days]
  (calculate-price-with-limit days (:basic prices) 3))

(defmethod price :old [current-year film days]
  (calculate-price-with-limit days (:basic prices) 5))