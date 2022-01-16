(ns dinsro.joins.accounts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.components.database-queries :as queries])
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.accounts :as q.accounts])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(defattr transactions ::m.accounts/transactions :ref
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

(defattr index ::m.accounts/index :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/index [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{user-id ::m.users/id} _]
     (let [ids (if user-id #?(:clj (q.accounts/find-by-user user-id) :cljs []) [])]
       {::m.accounts/index (m.accounts/idents ids)}))})

(defattr my-accounts ::m.accounts/my-accounts :ref
  {ao/target    ::m.accounts/id
   ao/pc-output [{::m.accounts/my-accounts [::m.accounts/id]}]
   ao/pc-resolve
   (fn [{:keys      [query-params] :as env
         :ring/keys [request]} _]
     (let [[_ user-id] (get-in request [:session :identity])]
       (if user-id
         (do (comment env query-params)
             {::m.accounts/my-accounts
              #?(:clj  (queries/get-my-accounts env user-id query-params)
                 :cljs [])})
         {:errors "no user"})))})

(def attributes
  [transactions
   index
   my-accounts])