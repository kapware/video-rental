(ns video-rental.registry.return
  (:require [clojure.spec.alpha :as spec]
            [video-rental.inventory.film :as film]
            [clojure.spec.alpha :as s]
            [video-rental.util :as util]))

(spec/def ::return
  (spec/keys :req-un [::rentid ::return-films]
             :opt-un [::created]))

(spec/def ::rentid int?)
(spec/def ::return-films (s/coll-of ::return-film))

(spec/def ::return-film
  (spec/keys :req-un [::film/tid]
             :opt-un [::surcharge ::created ::bonus]))

(spec/def ::surcharge util/bigdec?)

(spec/def ::created inst?)

(spec/def ::bonus (spec/int-in 1 3))