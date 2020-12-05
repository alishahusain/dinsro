(ns dinsro.ui.user-transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.user-transactions :as u.user-transactions]
   [taoensso.timbre :as timbre]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.currencies/init-handlers!
                e.accounts/init-handlers!
                e.transactions/init-handlers!
                e.f.create-transaction/init-handlers!
                e.f.add-user-transaction/init-handlers!)]
    store))

(let [user (ds/gen-key ::e.users/item)
      user-id (:db/id user)
      accounts (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      store (test-store)]
  (st/dispatch store [::e.f.add-user-transaction/set-shown? true])
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])

  (defcard-rg section
    [u.user-transactions/section store user-id accounts])

  (deftest section-test
    (is (vector? (u.user-transactions/section store user-id accounts)))))
