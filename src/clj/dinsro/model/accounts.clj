(ns dinsro.model.accounts
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec prepare-account ::s.accounts/params
  [params ::s.accounts/params]
  params)

(defn-spec create-record ::ds/id
  [params ::s.accounts/params]
  (let [response (d/transact db/*conn* {:tx-data [(assoc params :db/id "account-id")]})]
    (get-in response [:tempids "account-id"])))

(defn-spec read-record (s/nilable ::s.accounts/item)
  [id ::ds/id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.accounts/name)
      record)))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.accounts/name _]] @db/*conn*)))

(defn-spec index-records (s/* ::s.accounts/item)
  []
  (d/pull-many @db/*conn* '[*] (index-ids)))

(defn-spec delete-record any?
  [id ::ds/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))
