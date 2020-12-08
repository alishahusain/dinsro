(ns dinsro.views.admin-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.forms.create-currency :as e.f.create-currency]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.users :as e.users]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.admin :as v.admin]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.admin-accounts/init-handlers!
                e.categories/init-handlers!
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.f.create-account/init-handlers!
                e.f.create-category/init-handlers!
                e.f.create-currency/init-handlers!
                e.rate-sources/init-handlers!
                e.transactions/init-handlers!
                e.users/init-handlers!)]
    store))

(let [match nil]

  (let [store (test-store)]
    (defcard-rg load-buttons
      [v.admin/load-buttons store])
    (deftest load-buttons-test
      (is (vector? (v.admin/load-buttons store)))))

  (let [store (test-store)]
    (defcard-rg page-card
      [v.admin/page store match])
    (deftest page-test
      (is (vector? (v.admin/page store match))))))