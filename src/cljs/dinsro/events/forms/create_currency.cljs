(ns dinsro.events.forms.create-currency
  (:require [dinsro.spec.events.forms.create-currency :as s.e.f.create-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(def default-name "")

(rfu/reg-basic-sub ::s.e.f.create-currency/name)
(rfu/reg-set-event ::s.e.f.create-currency/name)

(rfu/reg-basic-sub ::s.e.f.create-currency/shown?)
(rfu/reg-set-event ::s.e.f.create-currency/shown?)

(defn form-data-sub
  [name]
  {:name name})

(rf/reg-sub
 ::s.e.f.create-currency/form-data
 :<- [::s.e.f.create-currency/name]
 form-data-sub)

(defn set-defaults
  [{:keys [db]} _]
  {:db (merge db {::s.e.f.create-currency/name s.e.f.create-currency/default-name})})

(kf/reg-event-fx ::set-defaults set-defaults)
