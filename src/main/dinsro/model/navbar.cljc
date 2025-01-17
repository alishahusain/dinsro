(ns dinsro.model.navbar
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.navlink :as m.navlink]))

(def auth-link-names [:accounts])
(def dropdown-link-names
  [:accounts
   :transactions
   :currencies
   :categories
   :rates
   :rate-sources
   :admin])
(def menu-link-names [:accounts :transactions :core-menu :ln-menu])
(def unauth-link-names [:login :registration])

(defattr id ::id :symbol
  {ao/identity?  true
   ao/pc-resolve (fn [_env _props] {::id :main})})

(defattr auth-links ::auth-links :ref
  {ao/target     ::m.navlink/id
   ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::auth-links [::m.navlink/id]}]
   ao/pc-resolve (fn [_env _props] {::auth-links (m.navlink/idents auth-link-names)})})

(defattr dropdown-links ::dropdown-links :ref
  {ao/target     ::m.navlink/id
   ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::dropdown-links [::m.navlink/id]}]
   ao/pc-resolve (fn [_env _props] {::dropdown-links (m.navlink/idents dropdown-link-names)})})

(defattr menu-links ::menu-links :ref
  {ao/target     ::m.navlink/id
   ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::menu-links [::m.navlink/id]}]
   ao/pc-resolve (fn [_env _props] {::menu-links (m.navlink/idents menu-link-names)})})

(defattr unauth-links ::unauth-links :ref
  {ao/target     ::m.navlink/id
   ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [{::unauth-links [::m.navlink/id]}]
   ao/pc-resolve (fn [_env _] {::unauth-links (m.navlink/idents unauth-link-names)})})

(defattr current-navbar :root/navbar :ref
  {ao/target     ::id
   ao/pc-output  [{:root/navbar [::id]}]
   ao/pc-resolve (fn [_ _] {:root/navbar {::id :main}})})

(def attributes
  [id
   dropdown-links
   menu-links
   unauth-links
   current-navbar])
