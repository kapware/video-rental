(ns video-rental.registry.rent
  (:require [clojure.spec.alpha :as spec]
            [video-rental.inventory.film :as film]
            [clojure.spec.alpha :as s]
            [video-rental.util :as util]))

(spec/def ::rent
  (spec/keys :req-un [::rent-films]
             :opt-un [::created]))

(spec/def ::rent-films (s/coll-of ::rent-film))

(spec/def ::rent-film
  (spec/keys :req-un [::film/tid ::days]
             :opt-un [::charge ::created]))

(spec/def ::days (spec/and integer? pos?))

(spec/def ::charge util/bigdec?)

(spec/def ::created inst?)