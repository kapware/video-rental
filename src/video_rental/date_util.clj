(ns video-rental.date-util
  (:import (java.time ZoneId ZonedDateTime)))

(defn current-year! []
  (.getYear (ZonedDateTime/now (ZoneId/of "UTC"))))