(ns dinsro.model.rates
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db :as db]
            [dinsro.spec :as ds]
            [dinsro.spec.rates :as s.rates]
            [dinsro.streams :as streams]
            [manifold.stream :as ms]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

(defn prepare-record
  [params]
  (update params ::s.rates/rate double))

(s/fdef prepare-record
  :args (s/cat :params ::s.rates/params)
  :ret ::s.rates/params)

(defn create-record
  [params]
  (let [tempid (d/tempid "rate-id")
        prepared-params (-> (prepare-record params)
                            (assoc :db/id tempid)
                            (update ::s.rates/date tick/inst))
        response (d/transact db/*conn* {:tx-data [prepared-params]})
        id (get-in response [:tempids tempid])]
    (ms/put! streams/message-source [::create-record [:dinsro.events.rates/add-record id]])
    id))

(s/fdef create-record
  :args (s/cat :params ::s.rates/params)
  :ret ::ds/id)

(defn read-record
  [id]
  (let [record (d/pull @db/*conn* '[*] id)]
    (when (get record ::s.rates/rate)
      (update record ::s.rates/date tick/instant))))

(s/fdef read-record
  :args (s/cat :id ::ds/id)
  :ret  (s/nilable ::s.rates/item))

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.rates/rate _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (->> (index-ids)
       (d/pull-many @db/*conn* '[*])
       (sort-by ::s.rates/date)
       (reverse)
       (take 75)
       (map #(update % ::s.rates/date tick/instant))))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::s.rates/item))

(defn delete-record
  [id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(s/fdef delete-record
  :args (s/cat :id ::ds/id)
  :ret nil?)

(defn delete-all
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(s/fdef delete-all
  :args (s/cat)
  :ret nil?)