(ns dinsro.components.navbar
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.users :as e.users]
            [dinsro.spec.events.forms.settings :as s.e.f.settings]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

;; Subscriptions

(s/def ::expanded? boolean?)
(rfu/reg-basic-sub ::expanded?)
(rfu/reg-set-event ::expanded?)

;; Events

(defn toggle-navbar
  [db _]
  (update db ::expanded? not))

(defn nav-link-activated
  [_ _]
  {:dispatch [::toggle-navbar]})

(kf/reg-event-db ::toggle-navbar toggle-navbar)
(kf/reg-event-fx ::nav-link-activated nav-link-activated)

;; Components

(defn nav-link
  [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :on-click #(rf/dispatch [::nav-link-activated])
    :class (when (= page @(rf/subscribe [:nav/page])) :is-active)}
   title])

(defn nav-burger
  []
  (let [expanded? @(rf/subscribe [::expanded?])]
    [:div.navbar-burger.burger
     {:role :button
      :aria-label :menu
      :aria-expanded false
      :on-click #(rf/dispatch [::toggle-navbar])
      :class (when expanded? :is-active)}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]))

(defn debug-button
  []
  [:a.navbar-item
   {:on-click #(rf/dispatch [::e.debug/toggle-shown?])}
   ""])

(defn navbar []
  (let [auth-id @(rf/subscribe [::e.authentication/auth-id])
        expanded? @(rf/subscribe [::expanded?])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [nav-burger]]
     [:div.navbar-menu {:class (when expanded? :is-active)}
      [:div.navbar-start
       (when auth-id
         [:<>
          (nav-link (tr [:accounts]) :index-accounts-page)
          (nav-link (tr [:transactions]) :index-transactions-page)])]
      [:div.navbar-end
       (when @(rf/subscribe [::e.debug/enabled?]) [debug-button])
       (if auth-id
         [:div.navbar-item.has-dropdown.is-hoverable
          [:a.navbar-link (::s.users/name @(rf/subscribe [::e.users/item auth-id]))]
          [:div.navbar-dropdown
           (nav-link (tr [:settings]) :settings-page)
           (c.debug/hide (nav-link (tr [:currencies]) :index-currencies-page))
           (nav-link (tr [:admin]) :admin-page)
           (c.debug/hide (nav-link (tr [:rate-sources]) :index-rate-sources-page))
           (c.debug/hide (nav-link (tr [:rates]) :index-rates-page))
           (c.debug/hide (nav-link (tr [:categories]) :index-categories-page))
           (c.debug/hide (nav-link (tr [:users]) :index-users-page))
           [:a.navbar-item {:on-click #(rf/dispatch [::e.authentication/do-logout])} (tr [:logout])]]]
         [:<>
          ;; FIXME: Do not show this section when settings are not loaded
          (nav-link (tr [:login]) :login-page)
          (when @(rf/subscribe [::s.e.f.settings/allow-registration])
              (nav-link (tr [:register]) :register-page))])]]]))