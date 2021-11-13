(ns dinsro.queries.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(def attributes-list
  '[:xt/id
    ::m.categories/id
    ::m.categories/name])
(def record-limit 1000)

(def find-eid-by-id-query
  '{:find  [?eid]
    :in    [?id]
    :where [[?eid ::m.categories/id ?id]]})

(def find-id-by-eid-query
  '{:find  [?id]
    :in    [?eid]
    :where [[?eid ::m.categories/id ?id]]})

(>defn find-eid-by-id
  [id]
  [::m.categories/id => (? :xt/id)]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-eid-by-id-query id))))

(>defn find-id-by-eid
  [eid]
  [:xt/id => (? ::m.categories/id)]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-id-by-eid-query eid))))

(>defn find-by-name-and-user
  [_name _user-id]
  [::m.categories/name ::m.categories/id => (? (s/keys))]
  nil)

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.categories/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?category-eid]
                :in    [?user-id]
                :where [[?category-eid ::m.categories/user ?user-id]]}]
    (map first (xt/q db query user-id))))

(>defn create-record
  [params]
  [::m.categories/params => :xt/id]
  (let [node   (c.xtdb/main-node)
        id     (new-uuid)
        params (assoc params ::m.categories/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.categories/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.categories/name)
      (let [user-eid (get-in record [::m.categories/user :xt/id])
            user-id  (q.users/find-id-by-eid user-eid)]
        (-> record
            (dissoc :xt/id)
            (assoc ::m.categories/user {::m.users/id user-id}))))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (let [db      (c.xtdb/main-db)
        query   '{:find  [?e]
                  :where [[?e ::m.categories/name _]]}
        results (xt/q db query)]
    (map first results)))

(>defn index-records
  []
  [=> (s/coll-of ::m.categories/item)]
  (map read-record (index-ids)))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
