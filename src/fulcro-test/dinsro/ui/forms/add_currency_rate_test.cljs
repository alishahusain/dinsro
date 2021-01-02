(ns dinsro.ui.forms.add-currency-rate-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.add-currency-rate :as u.f.add-currency-rate]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AddCurrencyRate
  {::wsm/card-height 5
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.add-currency-rate/AddCurrencyRate
    ::ct.fulcro3/initial-state
    (fn []
      {:form/id ::u.f.add-currency-rate/form})}))
