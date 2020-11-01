(ns dinsro.components.show-currency-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard defcard-rg deftest]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-currency :as c.show-currency]
   [dinsro.events.debug :as e.debug]
   [dinsro.spec :as ds]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(let [item (ds/gen-key ::s.currencies/item)
      store (doto (mock-store)
              e.debug/init-handlers!)]
  (defcard item item)

  (defcard-rg show-currency
    (fn []
      (st/dispatch store [::e.debug/set-shown? true])
      [error-boundary
       [c.show-currency/show-currency store item]]))

  (deftest show-currency-test
    (is (vector? (c.show-currency/show-currency store item)))))
