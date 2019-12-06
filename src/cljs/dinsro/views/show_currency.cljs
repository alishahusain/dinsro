(ns dinsro.views.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.show-currency :refer [show-currency]]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.tempura :as tempura :refer [tr]]
            [taoensso.timbre :as timbre]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn-spec init-page ::init-page-response
  [cofx ::init-page-cofx
   event ::init-page-event]
  (timbre/spy :info cofx)
  (timbre/spy :info event)
  (let [[{:keys [id]}] event]
    {:dispatch [::e.currencies/do-fetch-record id]}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-currency-page)
  :start  [::init-page]})

;; Fixme: string
(s/def :show-currency-view/id          string?)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))

(def l
  {:en
   {:missing "Missing"
    :load-currency "Load Currency"
    :load-rates "Load Rates: %1"
    :not-loaded "Currency not loaded"}})

(defn-spec page vector?
  [{{:keys [id]} :path-params} ::view-map]
  (let [currency-id (int id)
        currency @(rf/subscribe [::e.currencies/item currency-id])
        rates @(rf/subscribe [::e.rates/items-by-currency currency])]
    [:section.section>div.container>div.content
     (let [state @(rf/subscribe [::e.rates/do-fetch-index-state])]
       [:button.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index id])}
        (tr [:load-rates] [(str state)])])
     (let [state @(rf/subscribe [::e.currencies/do-fetch-record-state])]
       [:<>
        [:button.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-record id])}
         (tr [:load-currency] [(str state)])]
        (condp = state
          :loaded [show-currency currency rates]
          :loading [:p "Loading"]
          :failed [:p "Failed"]
          [:p "Unknown State"])])]))
