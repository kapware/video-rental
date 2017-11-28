(ns video-rental.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [video-rental.inventory.film :as film]
            [video-rental.inventory.search :as search]
            [video-rental.registry.rent :as rent]
            [video-rental.registry.rent-out :as rent-out]
            [video-rental.date-util :as date-util]))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Video-rental"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    (context "/api" []
      :tags ["api"]
      :coercion :spec

      (GET "/film" []
        :return (s/coll-of ::film/film)
        :query-params [title :- ::film/title]
        :summary "returns films"
        (ok (search/find-by-title title)))

      (POST "/rent" []
        :return ::rent/rent
        :body [rent ::rent/rent]
        :summary "rent out films"
        (created nil (rent-out/rent-out (date-util/current-year!) 1 rent)))
      )))