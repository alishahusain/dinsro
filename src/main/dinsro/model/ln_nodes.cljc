(ns dinsro.model.ln-nodes
  (:refer-clojure :exclude [name])
  (:require
   #?(:clj [clojure.java.io :as io])
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as log]))

(def cert-base "/mnt/certs/")

(>defn cert-path
  [id]
  [uuid? => string?]
  (str cert-base id "/tls.cert"))

(>defn macaroon-path
  [id]
  [uuid? => string?]
  (str cert-base id "/admin.macaroon"))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::core-node ::m.core-nodes/id)
(defattr core-node ::core-node :ref
  {ao/identities #{::id}
   ao/target     ::m.core-nodes/id
   ao/schema     :production
   ::report/column-EQL {::core-node [::m.core-nodes/id ::m.core-nodes/name]}})

(s/def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::port string?)
(defattr port ::port :string
  {ao/identities #{::id}
   ao/schema     :production})

(defattr mnemonic ::mnemonic :vector
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/identities #{::id}
   ao/target     ::m.users/id
   ao/schema     :production
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

#?(:clj
   (>defn has-cert?
     [id]
     [::id => boolean?]
     (log/info "has cert")
     (.exists (io/file (log/spy :info (cert-path id))))))

#?(:clj
   (>defn has-macaroon?
     [id]
     [::id => boolean?]
     (log/info "has cert")
     (.exists (io/file (log/spy :info (macaroon-path id))))))

(defattr hasCert? ::hasCert? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::hasCert?]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   {::hasCert?
                    #?(:clj (has-cert? id)
                       :cljs (do (comment id) false))})})

(defattr hasMacaroon? ::hasMacaroon? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::hasMacaroon?]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   {::hasMacaroon?
                    #?(:clj (has-macaroon? id)
                       :cljs (do (comment id) false))})})

(defattr unlocked? ::unlocked? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(defattr initialized? ::initialized? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::name ::host ::port ::core-node]))
(s/def ::params  (s/keys :req [::name ::host ::port ::user ::core-node]))
(s/def ::item (s/keys :req [::id ::name ::host ::port ::user ::core-node]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => any?]
  {::id id})

(>defn ident-item
  [{::keys [id]}]
  [::item => any?]
  (ident id))

(>defn idents
  [ids]
  [(s/coll-of ::id) => any?]
  (mapv ident ids))

#?(:clj
   (defn cert-file
     [id]
     (io/file (cert-path id))))

(def attributes
  [id name user host port mnemonic hasCert? hasMacaroon? unlocked? initialized? core-node])
