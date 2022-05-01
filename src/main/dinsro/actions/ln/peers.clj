(ns dinsro.actions.ln.peers
  (:refer-clojure :exclude [next])
  (:require
   [clojure.core.async :as async]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])

  (:import
   java.net.URI
   org.lightningj.lnd.wrapper.message.ConnectPeerRequest
   org.bitcoins.lnd.rpc.config.LndInstanceRemote))

(>defn fetch-peers
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (let [ch (async/chan)]
    (with-open [client (a.ln.nodes/get-client node)]
      (.listPeers client true (c.lnd/ch-observer ch)))
    ch))

(defn create-peer-record!
  [data]
  (log/info :create-peer-record!/starting {:data data})
  (q.ln.peers/create-record data))

(>defn update-peer!
  [node data]
  [::m.ln.nodes/item ::m.ln.peers/params => any?]
  (let [{::m.ln.nodes/keys [id]}     node
        {::m.ln.peers/keys [pubkey]} data
        params (assoc data ::m.ln.peers/node id)]
    (if-let [peer-id (q.ln.peers/find-peer id pubkey)]
      (if-let [peer (q.ln.peers/read-record peer-id)]
        (do
          (log/info :update-peer!/starting {:peer peer :data data})
          (let [params (merge peer params)]
            (q.ln.peers/update! params))
          nil)
        (throw (RuntimeException. "Can't find peer")))
      (do
        (log/error :update-peer!/no-peer {})
        (create-peer-record! params)))))

(defn handle-fetched-peer
  [node peer]
  (try
    (let [params (set/rename-keys peer m.ln.peers/rename-map)]
      (update-peer! node params))
    (catch Exception ex
      (log/error "Failed to update" ex))))

(>defn fetch-peers!
  [id]
  [::m.ln.nodes/id => (? any?)]
  (log/info :fetch-peers!/starting {:id id})
  (if-let [node (q.ln.nodes/read-record id)]
    (if-let [ch (fetch-peers node)]
      (let [data            (async/<!! ch)
            {:keys [peers]} data]
        (doseq [peer peers]
          (handle-fetched-peer node peer))
        ch)
      (do
        (log/error :fetch-peers!/no-peer {})
        nil))
    (do
      (log/error :fetch-peers!/no-node {})
      nil)))

(>defn ->connect-peer-request
  [host pubkey]
  [string? string? => (partial instance? ConnectPeerRequest)]
  (let [address (c.lnd/->lightning-address host pubkey)]
    (c.lnd/->connect-peer-request address)))

(>defn create-peer!
  [node host pubkey]
  [::m.ln.nodes/item string? string? => any?]
  (log/info :create-peer!/starting {:pubkey pubkey :host host})
  (with-open [client (a.ln.nodes/get-client node)]
    (c.lnd/connect-peer client host pubkey)))

(defn create!
  "Handler for new peer submit button"
  [{::m.ln.peers/keys [address]
    node-id           ::m.ln.peers/node
    :as               props}]
  (log/info :create!/starting {:props props})
  (let [node   (q.ln.nodes/read-record node-id)
        host   address
        pubkey nil]
    (create-peer!
     node host pubkey)))

(defn delete!
  "Handler for delete peer mutation"
  [props]
  (log/info :delete!/starting {:props props}))

(comment
  (q.ln.peers/index-ids)
  (q.ln.peers/index-records)

  (map ::m.ln.info/identity-pubkey (q.ln.nodes/index-records))

  (def peer (first (q.ln.peers/index-records)))
  (def node-id (::m.ln.peers/node peer))
  node-id

  (def node (q.ln.nodes/read-record node-id))
  node
  (def pubkey (::m.ln.info/identity-pubkey node))
  pubkey

  (first (q.ln.nodes/index-records))

  (let [url (URI. "http://lnd.bob/")
        macaroon ""
        cert-file (scala.Option/empty)
        cert-opt (scala.Option/empty)]
    (LndInstanceRemote. url macaroon cert-file cert-opt))

  (q.ln.peers/find-peer node-id pubkey)

  (map q.ln.peers/delete (q.ln.peers/index-ids))
  (first (q.ln.peers/index-records))

  nil)
