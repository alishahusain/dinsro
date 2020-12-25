(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.router :as router]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.navbar :as u.navbar]
   [taoensso.timbre :as timbre]))

(defsc DebugLinkButton
  [this {:debug-menu/keys [path label]}]
  {:query [:debug-menu/id
           :debug-menu/path
           :debug-menu/label]
   :ident :debug-menu/id
   :initial-state {}}
  (dom/button {:onClick #(dr/change-route this path)} label))

(def ui-debug-link-button (comp/factory DebugLinkButton {:keyfn :debug-menu/id}))

(defsc DebugLinkBar
  [_this {:keys [all-debug-menus]}]
  {:query [{:all-debug-menus (comp/get-query DebugLinkButton)}]
   :initial-state {:all-debug-menus []}}
  (bulma/container
   (bulma/box
    (map ui-debug-link-button all-debug-menus))))

(def ui-debug-link-bar (comp/factory DebugLinkBar))

(defsc Root [this {:root/keys [debug-link-bar navbar router]}]
  {:query [{:root/debug-link-bar (comp/get-query DebugLinkBar)}
           {:root/navbar (comp/get-query u.navbar/Navbar)}
           {:root/router (comp/get-query router/RootRouter)}]
   :initial-state {:root/debug-link-bar {}
                   :root/navbar {}
                   :root/router {}}}
  (let [top-router-state (or (uism/get-active-state this ::router/RootRouter) :initial)]
    (dom/div
     (u.navbar/ui-navbar navbar)
     (ui-debug-link-bar debug-link-bar)
     (bulma/container
      (if (= :initial top-router-state)
        (dom/div :.loading "Loading...")
        (router/ui-root-router router))))))
