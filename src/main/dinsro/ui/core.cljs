(ns dinsro.ui.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.ui.core.addresses :as u.c.addresses]
   [dinsro.ui.core.blocks :as u.c.blocks]
   [dinsro.ui.core.chains :as u.c.chains]
   [dinsro.ui.core.connections :as u.c.connections]
   [dinsro.ui.core.networks :as u.c.networks]
   [dinsro.ui.core.nodes :as u.c.nodes]
   [dinsro.ui.core.peers :as u.c.peers]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [dinsro.ui.core.wallet-addresses :as u.c.wallet-addresses]
   [dinsro.ui.core.words :as u.c.words]))

(defrouter CoreRouter
  [_this _props]
  {:router-targets [u.c.addresses/CoreAddressForm
                    u.c.addresses/CoreAddressReport
                    u.c.blocks/ShowBlock
                    u.c.blocks/CoreBlockForm
                    u.c.blocks/CoreBlockReport
                    u.c.chains/CoreChainForm
                    u.c.chains/CoreChainsReport
                    u.c.connections/CoreNodeConnectionsReport
                    u.c.connections/NewConnectionForm
                    u.c.networks/CoreNetworkForm
                    u.c.networks/CoreNetworksReport
                    u.c.nodes/NewCoreNodeForm
                    u.c.nodes/CoreNodeForm
                    u.c.nodes/CoreNodesReport
                    u.c.nodes/ShowNode
                    u.c.nodes/NodeContainer
                    u.c.peers/CorePeerForm
                    u.c.peers/CorePeersReport
                    u.c.peers/CorePeers2Report
                    u.c.peers/NewCorePeerForm
                    u.c.tx/CoreTxForm
                    u.c.tx/CoreTxReport
                    u.c.tx/ShowTransaction
                    u.c.wallets/NewWalletForm
                    u.c.wallets/ShowWallet
                    u.c.wallets/WalletForm
                    u.c.wallets/WalletsReport
                    u.c.wallet-addresses/NewWalletAddressForm
                    u.c.wallet-addresses/WalletAddressForm
                    u.c.wallet-addresses/WalletAddressesReport
                    u.c.words/WordsReport]}
  (dom/div {} "Core router"))

(def ui-core-router (comp/factory CoreRouter))

(defsc CorePage
  [_this {:keys [core-router]}]
  {:query         [{:core-router (comp/get-query CoreRouter)}]
   :initial-state {:core-router {}}
   :ident         (fn [] [:component/id ::CorePage])
   :route-segment ["core"]}
  (ui-core-router core-router))
