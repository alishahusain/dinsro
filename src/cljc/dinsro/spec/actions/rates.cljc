(ns dinsro.spec.actions.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec :as ds]
            [dinsro.spec.rates :as s.rates]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def ::currency-id ::ds/id)
(def currency-id ::currency-id)

;; Create

(s/def :create-rates-request-valid/params (s/keys :req-un [::s.rates/rate ::currency-id ::s.rates/date]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-rates-request-valid/params]))
(def create-handler-request-valid ::create-handler-request-valid)

(s/def :create-rates-request/params (s/keys :opt-un [::s.rates/rate ::currency-id]))
(s/def ::create-handler-request (s/keys :req-un [:create-rates-request/params]))
(def create-handler-request ::create-handler-request)

(comment
  (ds/gen-key create-handler-request-valid)
  (ds/gen-key create-handler-request)
  )

(s/def :create-rates-response-valid/body (s/keys :req-un [::s.rates/item]))
(s/def :create-rates-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-rates-response-valid/body
                                                        :create-rates-response-valid/status]))
(def create-handler-response-valid ::create-handler-response-valid)

(s/def ::create-handler-response (s/or :invalid ::ds/common-response-invalid
                                       :valid   ::create-handler-response-valid))
(def create-handler-response ::create-handler-response)

(comment
  (ds/gen-key create-handler-response)
  )

;; Read

(s/def :read-rates-request/path-params (s/keys :req-un []))
(s/def ::read-handler-request (s/keys :req-un [:read-rates-request/path-params]))
(def read-handler-request ::read-handler-request)

(s/def :read-rates-response/body (s/keys :req-un [::s.rates/item]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-rates-response/body]))
(def read-handler-response-valid ::read-handler-response-valid)

(s/def ::read-handler-response (s/or :not-found ::ds/common-response-not-found
                                     :valid     ::read-handler-response-valid))
(def read-handler-response ::read-handler-response)

;; Delete

(s/def :delete-rates-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-rates-request/path-params (s/keys :req-un [:delete-rates-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-rates-request/path-params]))
(def delete-handler-request ::delete-handler-request)

(s/def ::delete-handler-response (s/keys))
(def delete-handler-response ::delete-handler-response)

;; Index

(s/def ::index-handler-request (s/keys))
(def index-handler-request ::index-handler-request)

(s/def :index-rates-response/items (s/coll-of ::s.rates/item))
(s/def :index-rates-response/body (s/keys :req-un [:index-rates-response/items]))
(s/def ::index-handler-response (s/keys :req-un [:index-rates-response/body]))
(def index-handler-response ::index-handler-response)
