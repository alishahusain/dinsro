(ns dinsro.views.about-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.about :as v.about]
   [pjstadig.humane-test-output]))

(defn test-store
  []
  (let [store (mock-store)]
    store))

(let [store (test-store)
      match nil]

  (defcard-rg page-card
    [error-boundary
     [v.about/page store match]])

  (deftest page-test
    (is (vector? (v.about/page store match)))))
