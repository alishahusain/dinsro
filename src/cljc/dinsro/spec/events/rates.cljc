(ns dinsro.spec.events.rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.rates :as s.rates]))

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::s.rates/item))

(s/def ::do-delete-record-success-response (s/keys))