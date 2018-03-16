(ns video-rental.registry.rent-out
  (:require [video-rental.db :refer [db]]
            [clojure.java.jdbc :as j]
            [video-rental.inventory.film :as film]
            [video-rental.ledger.charge :as charge]
            [video-rental.inventory.search :as search]))

#_(defn calculate-charge [current-year {:keys [tid] :as rent}]
  (assoc rent
    :charge (charge/charge current-year (search/find-by-tid tid) rent)))

#_(defn rent-out [current-year user-id rent]
  (j/with-db-transaction [tconn db]
    (let [insert-rent (j/insert! tconn :rent {:userid 1})
          id-with-h2-fallback #(:id % ((keyword "scope_identity()") %))
          rent-id (-> insert-rent first id-with-h2-fallback)
          calculate-charge-current-year (partial calculate-charge current-year)
          add-rent-id #(assoc % :rentid rent-id)
          prepare-rentfilms (comp calculate-charge-current-year add-rent-id)
          rentfilms (->> rent
                         :rent-films
                         (map prepare-rentfilms))
          inserted-rents (j/insert-multi! tconn :rentfilm rentfilms)
          rent-with-id (assoc rent :rentid rent-id)
          result (->> rentfilms
                      (map #(dissoc % :rentid))
                      (assoc rent-with-id :rent-films))]
      result)))