(ns dinsro.components.forms.create-rate
  (:require [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.create-rate :as e.f.create-rate]
            [dinsro.events.rates :as e.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.create-rate/init-form]})

(defn form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [:a.delete.is-pulled-right {:on-click #(rf/dispatch [::set-shown? false])}]
       [:div.field>div.control
        [c/number-input (tr [:rate]) ::rate ::set-rate]]
       [:div.field>div.control
        [:label.label (tr [:date])]
        [c.datepicker/datepicker {:on-select #(rf/dispatch [::set-date %])}]]
       [:div.field>div.control
        [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]]
       [:div.field>div.control
        [c.debug/debug-box form-data]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::e.rates/do-submit form-data]]]])))
