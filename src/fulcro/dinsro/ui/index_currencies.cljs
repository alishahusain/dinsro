(ns dinsro.ui.index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexCurrencyLine
  [_this {::m.currencies/keys [id name]}]
  {:query [::m.currencies/id ::m.currencies/name]
   :ident ::m.currencies/id}
  (dom/tr
   (dom/td name)
   (dom/td id)))

(def ui-index-currency-line (comp/factory IndexCurrencyLine {:keyfn ::m.currencies/id}))

(defsc IndexCurrencies
  [_this {:keys [currencies]}]
  {:query [:currencies
           #_{:currencies (comp/get-query IndexCurrencyLine)}]
   :initial-state (fn [_] {:currencies (vals sample/currency-map)})}
  (if (seq (timbre/spy :info currencies))
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:name-label]))
       (dom/th "Buttons")))

     (dom/tbody
      (map ui-index-currency-line currencies)))

    (dom/div (tr [:no-currencies]))))

(def ui-index-currencies (comp/factory IndexCurrencies))