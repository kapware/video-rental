(ns video-rental.registry.rent
  (:require [clojure.spec.alpha :as spec]
            [video-rental.inventory.film :as film]
            [clojure.spec.alpha :as s]))

(spec/def ::rent
  (spec/keys :req-un [::rent-films]
             :opt-un [::created]))

(spec/def ::rent-films (s/coll-of ::rent-film))

(spec/def ::rent-film
  (spec/keys :req-un [::film/tid ::days]
             :opt-un [::charge ::created]))

(spec/def ::days (spec/and integer? pos?))

(defn bigdec?
  "Return true if x is a BigDecimal"
  {:added "1.9"}
  [x] (instance? java.math.BigDecimal x))

(spec/def ::charge bigdec?)

(spec/def ::created inst?)