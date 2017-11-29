(ns video-rental.ledger.charge)

(defn film-type [{year :year} current-year]
  (let [last-year (dec current-year)
        old-year (- current-year 5)]
    (cond
      (>= year last-year) :new-release
      (< old-year year last-year) :regular
      :else :old)))

(def prices {:premium 40M :basic 30M})

(def film-types {:new-release {:price :premium :day-limit 0 :bonus 2}
                 :regular     {:price :basic   :day-limit 3 :bonus 1}
                 :old         {:price :basic   :day-limit 5 :bonus 1}})

(defn- calculate-price-with-limit [days rate day-limit]
  (cond
    (zero? days) 0M
    (<= days day-limit) rate
    :else (*' 1M rate (- days (dec day-limit)))))

(defmulti charge (fn [current-year film rent] (film-type film current-year)))

(defmethod charge :new-release [current-year film {days :days}]
  (*' 1M (:premium prices) days))

(defmethod charge :regular [current-year film {days :days}]
  (calculate-price-with-limit days (:basic prices) 3))

(defmethod charge :old [current-year film {days :days}]
  (calculate-price-with-limit days (:basic prices) 5))

(defn surcharge [current-year film {days :days}]
  (let [price (->> (film-type film current-year)
                   (get film-types)
                   :price
                   (get prices))]
    (*' 1M price days)))

(defn bonus [current-year film]
  (->> (film-type film current-year)
       (get film-types)
       :bonus))