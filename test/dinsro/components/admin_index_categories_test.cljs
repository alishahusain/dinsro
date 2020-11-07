(ns dinsro.components.admin-index-categories-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.admin-index-categories :as c.admin-index-categories]
   [dinsro.components.boundary]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.users :as e.users]
   [dinsro.specs :as ds]
   [dinsro.specs.categories :as s.categories]
   [dinsro.specs.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def users (ds/gen-key (s/coll-of ::s.users/item :count 3)))

(def categories (map
                 (fn [category]
                   (let [user-ids (map :db/id users)
                         user-id (rand-nth user-ids)]
                     (assoc-in category [::s.categories/user :db/id] user-id)))
                 (ds/gen-key (s/coll-of ::s.categories/item :count 3))))
(def category (first categories))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.categories/init-handlers!
                e.users/init-handlers!
                e.f.create-category/init-handlers!)]
    store))

(let [store (test-store)]
  (st/dispatch store [::e.categories/do-fetch-index-success {:items categories}])
  (st/dispatch store [::e.users/do-fetch-index-success {:users users}])
  ;; (st/dispatch store [::e.f.create-categories/set-shown? true])

  (defcard-rg c.admin-index-categories/category-line
    [:table.table>tbody
     [c.admin-index-categories/category-line store category]])

  (deftest category-line-test
    (is (vector? (c.admin-index-categories/category-line store category))))

  (defcard-rg c.admin-index-categories/index-categories
    [c.admin-index-categories/index-categories store categories])

  (deftest index-categories-test
    (is (vector? (c.admin-index-categories/index-categories store categories))))

  (defcard-rg c.admin-index-categories/section
    [c.admin-index-categories/section store])

  (deftest section-test
    (is (vector? (c.admin-index-categories/section store)))))
