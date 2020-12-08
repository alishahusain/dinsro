(ns dinsro.ui.forms.admin-create-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defsc AdminCreateCurrencyForm
  [_this _props]
  {:query []}
  (dom/div
   (u.buttons/ui-close-button #_close-button)

   "Admin Create Currency"
   (u.inputs/ui-text-input {:label (tr [:name])})))

(def ui-admin-create-currency-form (comp/factory AdminCreateCurrencyForm))