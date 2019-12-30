(ns dinsro.spec.actions.users
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec :as ds]
            [dinsro.spec.users :as s.users]
            [taoensso.timbre :as timbre]))

;; Create

(s/def :create-user-request/params (s/keys :opt [::s.users/name]))
(s/def ::create-request (s/keys :req-un [:create-request/params]))
(s/def :create-user-request/status (constantly 200))

(s/def ::create-response (s/keys :req-un [:create-handler/status]))

;; Read

(s/def ::read-handler-request ::ds/common-read-request)

(s/def :read-user-response/body (s/keys :req-un [::s.users/item]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-user-response/body]))
(s/def ::read-handler-response (s/or :not-found ::ds/common-response-not-found
                                     :valid     ::read-handler-response-valid))
