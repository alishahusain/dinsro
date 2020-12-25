(ns dinsro.ui.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def default-name "sally")
(def default-url "https://example.com/")

(defsc IndexRateSourceLine
  [_this {::m.rate-sources/keys [name url currency-id]}]
  {:query [::m.rate-sources/id
           ::m.rate-sources/currency-id
           ::m.rate-sources/name
           ::m.rate-sources/url]
   :ident ::m.rate-sources/id
   :initial-state {::m.rate-sources/id 0
                   ::m.rate-sources/currency-id 0
                   ::m.rate-sources/name ""
                   ::m.rate-sources/url ""}}
  (dom/tr
   (dom/td name)
   (dom/td url)
   (dom/td currency-id #_(c.links/currency-link currency-id))
   (dom/td
    (dom/button :.button.is-danger "Delete")
    #_(c.buttons/delete-rate-source item))))

(def ui-index-rate-source-line (comp/factory IndexRateSourceLine {:keyfn ::m.rate-sources/id}))

(defsc IndexRateSources
  [_this {::keys [items]}]
  {:query [{::items (comp/get-query IndexRateSourceLine)}]
   :initial-state {::items []}}
  (if (seq items)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:name]))
       (dom/th (tr [:url]))
       (dom/th (tr [:currency]))
       (dom/th (tr [:actions]))))
     (dom/tbody
      (map ui-index-rate-source-line items)))
    (dom/p "no items")))

(def ui-index-rate-sources (comp/factory IndexRateSources))
