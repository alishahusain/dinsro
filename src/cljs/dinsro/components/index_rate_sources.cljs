(ns dinsro.components.index-rate-sources
  (:require [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.translations :refer [tr]]))

(defn index-line
  [item]
  (let [name (::s.rate-sources/name item)
        url (::s.rate-sources/url item)
        currency-id (get-in item [::s.rate-sources/currency :db/id])]
    [:tr
     [:td name]
     [:td url]
     [:td [c.links/currency-link currency-id]]
     (c.debug/hide [:td [c.buttons/delete-rate-source item]])]))

(defn section
  [items]
  [:<>
   [c.debug/debug-box items]
   (if-not (seq items)
     [:p (tr [:no-rate-sources])]
     [:table.table
      [:thead>tr
       [:th (tr [:name])]
       [:th (tr [:url])]
       [:th (tr [:currency])]
       (c.debug/hide [:th (tr [:actions])])]
      (into
       [:tbody]
       (for [item items]
         ^{:key (:db/id item)} [index-line item]))])])