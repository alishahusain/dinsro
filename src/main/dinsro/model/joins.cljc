(ns dinsro.model.joins
  (:require
   #?(:clj [clojure.spec.alpha :as s])
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   #?(:clj [dinsro.components.database-queries :as queries])
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.ln-nodes :as q.ln-nodes])
   #?(:clj [dinsro.queries.ln-peers :as q.ln-peers])
   #?(:clj [dinsro.queries.ln-transactions :as q.ln-tx])
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   #?(:clj [dinsro.queries.rates :as q.rates])
   [dinsro.specs]
   [taoensso.timbre :as log]))

#?(:clj
   (>defn find-by-currency
     [currency-id]
     [::m.currencies/id => (s/coll-of ::m.accounts/id)]
     (q.accounts/find-by-currency currency-id)))

(defattr account-transactions ::m.accounts/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.accounts/id}
   ao/pc-output   [{::m.accounts/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     #?(:clj  (if id
                (let [ids (q.transactions/find-by-account id)]
                  {::m.accounts/transactions (map (fn [id] {::m.transactions/id id}) ids)})
                {::m.accounts/transactions []})
        :cljs (comment id)))})

(defattr all-accounts ::m.accounts/all-accounts :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/all-accounts [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (comment env query-params)
     {::m.accounts/all-accounts
      #?(:clj  (queries/get-all-accounts env query-params)
         :cljs [])})})

(defattr all-currencies ::m.currencies/all-currencies :ref
  {ao/target    ::m.currencies/id
   ao/pc-output [{::m.currencies/all-currencies [::m.currencies/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj
        {::m.currencies/all-currencies (queries/get-all-currencies env query-params)}
        :cljs
        (comment env query-params)))})

(defattr all-users ::m.users/all-users :ref
  {ao/target    ::m.users/id
   ao/pc-output [{::m.users/all-users [::m.users/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     #?(:clj  {::m.users/all-users (queries/get-all-users env query-params)}
        :cljs (comment env query-params)))})

(defattr category-transactions ::m.categories/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.categories/id}
   ao/pc-output   [{::m.categories/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.categories/keys [id]}]
     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-category id)]
                  {::m.categories/transactions (map (fn [id] {::m.categories/id id}) transaction-ids)})
          :cljs {::m.categories/transactions []})
       {::m.categories/transactions []}))})

(defattr currency-accounts ::m.currencies/accounts :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/accounts [::m.accounts/id]}]
   ao/target      ::m.accounts/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     #?(:clj  (if id
                (let [account-ids (q.accounts/find-by-currency id)]
                  {::m.currencies/accounts (map (fn [id] {::m.accounts/id id}) account-ids)})
                {::m.currencies/accounts []})
        :cljs (comment id)))})

(defattr currency-transactions ::m.currencies/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-currency id)]
                  {::m.currencies/transactions (map (fn [id] {::m.transactions/id id}) transaction-ids)})
          :cljs {::m.currencies/transactions []})
       {::m.currencies/transactions []}))})

(defattr ln-node-peers ::m.ln-nodes/peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln-nodes/id}
   ao/pc-output   [{::m.ln-nodes/peers [::m.ln-peers/id]}]
   ao/target      ::m.ln-peers/id
   ao/pc-resolve
   (fn [_env {::m.ln-nodes/keys [id]}]
     {::m.ln-nodes/peers
      (let [ids #?(:clj (and id (q.ln-peers/find-ids-by-node id))
                   :cljs (do (comment id) []))]
        (map (fn [id] {::m.ln-peers/id id}) ids))})})

(defattr ln-node-transactions ::m.ln-nodes/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln-nodes/id}
   ao/pc-output   [{::m.ln-nodes/transactions [::m.ln-tx/id]}]
   ao/target      ::m.ln-tx/id
   ao/pc-resolve
   (fn [_env {::m.ln-nodes/keys [id]}]
     (let [transactions (let [ids #?(:clj (and id (q.ln-tx/find-ids-by-node id))
                                     :cljs (do (comment id) []))]
                          (mapv (fn [id] {::m.ln-tx/id id}) ids))]
       {::m.ln-nodes/transactions transactions}))})

(defattr rate-source-rates ::m.rate-sources/rates :ref
  {ao/cardinality :many
   ao/target      ::m.rates/id
   ao/pc-input    #{::m.rate-sources/id}
   ao/pc-output   [{::m.rate-sources/rates [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env {::m.rate-sources/keys [id]}]
     (if id
       #?(:clj (let [rate-ids (q.rates/find-ids-by-rate-source id)]
                 {::m.rate-sources/rates (map (fn [id] [::m.rates/id id]) rate-ids)})
          :cljs {::m.rate-sources/rates []})
       {:errors "no id"}))})

(defattr user-accounts ::m.users/accounts :ref
  {ao/cardinality :many
   ao/target      ::m.accounts/id
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/accounts [::m.accounts/id]}]
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     #?(:clj  (if id
                (let [account-ids (q.accounts/find-by-user id)]
                  {::m.users/accounts (map (fn [id] {::m.accounts/id id}) account-ids)})
                {::m.users/accounts []})
        :cljs (comment id)))})

(defattr user-categories ::m.users/categories :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/categories [::m.categories/id]}]
   ao/target      ::m.categories/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     #?(:clj  (if id
                (let [category-ids (q.categories/find-by-user id)]
                  {::m.users/categories (map (fn [id] {::m.categories/id id}) category-ids)})
                {::m.users/categories []})
        :cljs (comment id)))})

(defattr user-ln-nodes ::m.users/ln-nodes :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/ln-nodes [::m.ln-nodes/id]}]
   ao/target      ::m.ln-nodes/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     {::m.users/ln-nodes
      (let [ids #?(:clj (and id (q.ln-nodes/find-ids-by-user id))
                   :cljs (do (comment id) []))]
        (map (fn [id] {::m.ln-nodes/id id}) ids))})})

(defattr user-transactions ::m.users/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-user id)]
                  {::m.users/transactions (map (fn [id] {::m.transactions/id id}) transaction-ids)})
          :cljs {::m.users/transactions []})
       {::m.users/transactions []}))})

(def attributes
  [account-transactions
   all-accounts
   all-currencies
   all-users
   category-transactions
   currency-accounts
   currency-transactions
   ln-node-peers
   ln-node-transactions
   rate-source-rates
   user-accounts
   user-categories
   user-ln-nodes
   user-transactions])
