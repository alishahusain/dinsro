(ns dinsro.views.index-currencies
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-currency :as c.f.create-currency :refer [create-currency]]
            [dinsro.components.index-currencies :refer [index-currencies]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:dispatch [::e.currencies/do-fetch-index]
   :document/title "Index Currencies"})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-currencies-page)
  :start  [::init-page]})

(defn loading-buttons
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-currencies]]))

(defn page
  []
  (let [currencies @(rf/subscribe [::e.currencies/items])]
    [:section.section>div.container>div.content
     [loading-buttons]
     [:div.box
      [:h1
       (tr [:index-currencies "Index Currencies"])
       [c/show-form-button ::c.f.create-currency/shown? ::c.f.create-currency/set-shown?]]
      [c.f.create-currency/create-currency]
      [:hr]
      [index-currencies currencies]]]))
