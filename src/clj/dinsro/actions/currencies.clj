(ns dinsro.actions.currencies
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [dinsro.model.currencies :as m.currencies]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn index-handler
  [request]
  (let [items (m.currencies/index)]
    (http/ok {:items items})))

(s/def :create-handler/params (s/keys :opt-un [::m.currencies/name]))
(s/def :create-handler-valid/params (s/keys :req-un [::m.currencies/name]))
(s/def :create-handler-valid/request (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(s/def ::create-handler-response (s/keys))

(def param-rename-map
  {:name ::m.currencies/name})

(defn-spec prepare-record ::m.currencies/params
  [params :create-handler/params]
  (-> params
      (set/rename-keys param-rename-map)
      (select-keys (vals param-rename-map))))

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (or (let [{:keys [params]} request
            params (prepare-record params)]
        (when (s/valid? ::m.currencies/params params)
          (let [item (m.currencies/create-record (timbre/spy :info params))]
            (http/ok {:item item}))))
      (http/bad-request {:status :invalid})))
