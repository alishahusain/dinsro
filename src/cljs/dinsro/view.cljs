(ns dinsro.view
  (:require [dinsro.components.login-page :as login]
            [dinsro.components.register :as register]
            [dinsro.components.user :as index-users]
            [dinsro.views.about :as about]
            [dinsro.views.home :as home]
            [kee-frame.core :as kf]
            [markdown.core :refer [md->html]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

;; Subscriptions

(rf/reg-sub
 :navbar-expanded
 (fn [db _] (get db :navbar-expanded)))

(rf/reg-sub
 :authenticated
 (fn [db _] (get db :authenticated)))

;; Events

(rf/reg-event-db
 :toggle-navbar
 (fn [db [_ _]]
   ;; TODO: I think this is a stream
   (assoc db :navbar-expanded (not (get db :navbar-expanded)))))

(rf/reg-event-db
 :toggle-auth
 (fn [db [_ _]]
   (timbre/warn "This shouldn't be available")
   (update-in db [:authenticated] not)))

;; Components

(defn nav-link [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn auth-toggle-button
  []
  (let [authenticated @(rf/subscribe [:authenticated])]
    [:a.navbar-item
     {:role :button
      :on-click #(rf/dispatch [:toggle-auth])}
     (if authenticated "in" "out")]))

(defn nav-burger
  []
  (let [expanded? @(rf/subscribe [:navbar-expanded])]
    [:div.navbar-burger.burger
     {:role :button
      :aria-label :menu
      :aria-expanded false
      :on-click #(rf/dispatch [:toggle-navbar])
      :class (when expanded? :is-active)}
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]
     [:span {:aria-hidden true}]]))

(defn navbar []
  (let [authenticated @(rf/subscribe [:authenticated])
        expanded? @(rf/subscribe [:navbar-expanded])]
    [:nav.navbar.is-info>div.container {:role "navigation" :aria-label "main navigation"}
     [:div.navbar-brand
      [:a.navbar-item
       {:href "/" :style {:font-weight :bold}}
       "Dinsro"]
      [auth-toggle-button]
      [nav-burger]]
     [:div.navbar-menu {:class (when expanded? :is-active)}
      [:div.navbar-start
       [nav-link "Users" :index-users-page]]
      [:div.navbar-end
       [nav-link "About" :about-page]
       (when (not authenticated)
         [:<>
          [nav-link "Login" :login]
          [nav-link "Register" :register-page]])]]]))

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :about-page       about/page
    :home-page        home/page
    :index-users-page index-users/page
    :login-page       login/page
    :register-page    register/page
    nil               [:div ""]]])
