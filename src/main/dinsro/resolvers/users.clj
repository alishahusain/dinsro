(ns dinsro.resolvers.users
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation delete!
  [_request {::m.users/keys [id]}]
  {::pc/params #{::m.users/id}
   ::pc/output [:status]}
  (q.users/delete-record id)
  {:status :success})

(defresolver user-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [::m.users/id]}
  (timbre/infof "resolving user: %s" id)
  (q.users/read-record id))

(defresolver user-link-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.users/link [::m.users/id]}]}
  {::m.users/link [[::m.users/id id]]})

(defresolver users-resolver
  [_env _props]
  {::pc/output [{:all-users [::m.users/id]}]}
  {:all-users
   (map (fn [{::m.users/keys [id]}]
          [::m.users/id id])
        (q.users/index-records))})

(def resolvers
  [user-resolver
   user-link-resolver
   users-resolver])

(comment
  (q.users/index-ids)
  (q.users/read-record 21))
