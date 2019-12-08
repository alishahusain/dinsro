(ns dinsro.components.forms.create-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-rate 1)

(s/def ::rate string?)
(rfu/reg-basic-sub ::rate)
(rfu/reg-set-event ::rate)

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::date string?)
(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(s/def ::time string?)
(rfu/reg-basic-sub ::time)
(rfu/reg-set-event ::time)

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn create-form-data
  [[currency-id rate date]]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 :<- [::date]
 create-form-data)

(defn submit-clicked
  [_ _]
  {:dispatch [::e.rates/do-submit @(rf/subscribe [::form-data])]})

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn toggle-form
  [db _]
  (update db ::shown? not))

(kf/reg-event-db ::toggle-form toggle-form)

(defn toggle-button
  []
  [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"])

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)]
    {:db (merge db {::rate (str default-rate)
                    ::currency-id ""
                    ::date (.toISOString default-date)})}))

(kf/reg-event-fx ::init-form init-form)

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::init-form]})

(defn create-rate-form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [:a.delete.is-pulled-right {:on-click #(rf/dispatch [::set-shown? false])}]
       [:div.field>div.control
        [c/number-input      "Rate"     ::rate        ::set-rate]]
       [:div.field>div.control
        [:label.label "Date"]
        [c.datepicker/datepicker {:on-select #(rf/dispatch [::set-date %])}]]
       [:div.field>div.control
        [c/currency-selector "Currency" ::currency-id ::set-currency-id]]
       [:div.field>div.control
        [c.debug/debug-box form-data]]
       [:div.field>div.control
        [c/primary-button    "Submit"   [::submit-clicked]]]])))
