(ns video-rental.inventory.film
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::film
  (spec/keys :req-un [::tid ::title ::year]))

(spec/def ::tid string?)

(spec/def ::title string?)

(spec/def ::year (spec/and integer? pos?))