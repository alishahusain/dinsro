(ns dinsro.events.forms.add-user-account-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha]
   [dinsro.cards :refer-macros [assert-spec deftest]]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [taoensso.timbre :as timbre]))

(let [currency-id (ds/gen-key ::s.e.f.create-account/currency-id)
      initial-value (ds/gen-key ::s.e.f.create-account/initial-value)
      name (ds/gen-key ::s.e.f.create-account/name)
      user-id (ds/gen-key ::s.e.f.create-account/user-id)
      expected-result {:currency-id (int currency-id)
                       :initial-value (.parseFloat js/Number initial-value)
                       :name name
                       :user-id (int user-id)}
      db {::s.e.f.create-account/currency-id currency-id
          ::s.e.f.create-account/initial-value initial-value
          ::s.e.f.create-account/name name
          ::s.e.f.create-account/user-id user-id}
      event [::e.f.add-user-account/form-data]
      result (e.f.add-user-account/form-data-sub db event)]

  (assert-spec ::e.f.add-user-account/form-data-db db)
  (assert-spec ::e.f.add-user-account/form-data-event event)
  (assert-spec ::e.f.add-user-account/form-data expected-result)
  (assert-spec ::e.f.add-user-account/form-data result)

  (deftest form-data-sub-test
    (is (= expected-result result))))
