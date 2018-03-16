(ns video-rental.registry.to-return
  (:require [clojure.java.jdbc :as j]
            [video-rental.ledger.charge :as charge]
            [video-rental.util :as util]
            [video-rental.inventory.search :as search]
            [video-rental.db :refer [db]]))

#_(defn calculate-surcharge [now {:keys [tid days created] :as rentfilm}]
  (let [days-since-created (util/days-between (util/to-zoned-date created) (util/now!))
        exceeded-days {:days (max 0 (- days-since-created days))}
        film (search/find-by-tid tid)
        current-year (util/year-of (util/now!))]
    (assoc rentfilm :surcharge (charge/surcharge current-year film exceeded-days))))

#_(defn calculate-bonus [now {:keys [tid days created] :as rentfilm}]
  (let [film (search/find-by-tid tid)
        current-year (util/year-of (util/now!))]
    (assoc rentfilm :bonus (charge/bonus current-year film))))

#_(defn to-return [now user-id {:keys [rentid return-films] :as a-return}]
  (let [tids (->> return-films (map :tid) set)
        calculate-surcharge-now (partial calculate-surcharge now)
        calculate-bonus-now (partial calculate-bonus now)
        normalize-return (fn [return] (dissoc return
                                              :created
                                              :charge
                                              :days))
        transform-return (comp normalize-return calculate-bonus-now calculate-surcharge-now)
        rentfilms (->> (j/query db ["SELECT * FROM rentfilm WHERE rentid = ?" rentid])
                       (filter (comp tids :tid))
                       (map transform-return))
        result (->> rentfilms
                    (map #(dissoc % :rentid))
                    (assoc a-return :return-films))]
    result))