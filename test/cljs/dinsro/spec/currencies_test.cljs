(ns dinsro.spec.currencies-test
  (:require [cljs.test :refer-macros [is]]
            [clojure.spec.alpha :as s]
            [devcards.core :as dc :refer-macros [defcard deftest]]
            [dinsro.spec :as ds]
            [dinsro.spec.currencies :as s.currencies]
            [expound.alpha :as expound]))

(defcard item
  (ds/gen-key ::s.currencies/item))

(let [item {::s.currencies/name "foo"}]
  (defcard item-test-validation
    (expound/expound-str ::s.currencies/item item))
  (deftest item-test
    (is (s/valid? ::s.currencies/item item))))
