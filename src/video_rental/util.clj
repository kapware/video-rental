(ns video-rental.util
  (:import (java.time ZoneId ZonedDateTime)
           (java.time.temporal ChronoUnit)))

(defn now! []
  (ZonedDateTime/now (ZoneId/of "UTC")))

(defn year-of [date]
  (.getYear date))

(defn to-zoned-date [inst]
  (ZonedDateTime/ofInstant (.toInstant inst) (ZoneId/of "UTC")))

(defn days-between [date1 date2]
  (.between ChronoUnit/DAYS date1 date2))

(defn bigdec?
  "Return true if x is a BigDecimal"
  {:added "1.9"}
  [x] (instance? java.math.BigDecimal x))