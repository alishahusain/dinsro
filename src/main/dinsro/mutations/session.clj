(ns dinsro.mutations.session
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(>defn do-register
  [username email password name]
  [::m.users/username ::m.users/email ::m.users/password ::m.users/name => (s/keys)]
  (let [params #::m.users{:email    email
                          :password password
                          :name     name
                          :username username}]
    (try
      (a.authentication/register (timbre/spy :info params))
      (catch Exception ex
        ;; (timbre/error ex "error")
        {::error true
         :ex     (str ex)}))))

(defmutation register
  [_env {:user/keys [email name password username]}]
  {::pc/params #{:user/email :user/name :user/password :user/username}
   ::pc/output [:user/id :user/valid? :user/registered?]}
  (timbre/info "register")
  (do-register username email password name))

(defmutation login
  [{{:keys [session]} :request} {:user/keys [email password]}]
  {::pc/params #{:user/email :user/password}
   ::pc/output [:user/id :user/valid?]}
  (if-let [_user (q.users/find-by-email email)]
    (if (= password "hunter2")
      (augment-response
       {:user/id     email
        :user/valid? true}
       (fn [ring-response]
         (assoc ring-response :session (assoc session :identity email))))
      {:user/id     nil
       :user/valid? false})
    {:user/id     nil
     :user/valid? false}))

(defmutation logout
  [{{:keys [session]} :request} _]
  {::pc/params #{}
   ::pc/output [:user/id :user/valid?]}
  (augment-response
   {:user/id     nil
    :user/valid? false}
   (fn [ring-response]
     (assoc ring-response :session (assoc session :identity nil)))))

(def resolvers [login logout register])
