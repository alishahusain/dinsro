(ns dinsro.queries.core.addresses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn index-ids
  []
  [=> (s/coll-of ::m.c.addresses/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.c.addresses/id _]]}]
    (map first (xt/q db query))))

(>defn read-record
  [id]
  [::m.c.addresses/id => (? ::m.c.addresses/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.addresses/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.addresses/params => ::m.c.addresses/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.addresses/id id)
                            (assoc :xt/id id))
        resp            (xt/submit-tx node [[::xt/put prepared-params]])]
    (xt/await-tx node resp)
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.addresses/item)]
  (map read-record (index-ids)))

(comment
  2
  :the
  (first (index-records))

  nil)
