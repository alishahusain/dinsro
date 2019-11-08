(ns dinsro.actions.authentication
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [dinsro.model.user :as m.users]
            [dinsro.specs :as specs]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :register-handler/params ::m.users/registration-params)
(s/def ::register-request (s/keys :req-un [:register-handler/params]))
(s/def :register-handler/body any?)
(s/def :register-handler/request (s/keys :req-un [:register-handler/body]))
(s/def ::register-handler-response (s/keys :req-un [:register-handler/body]))

(defn-spec check-auth boolean?
  [email ::m.users/email password ::m.users/password]
  (if-let [user (m.users/This find-by-email email)]
    (let [{:keys [dinsro.model.user/password-hash]} user]
      (hashers/check password password-hash))))

(defn-spec authenticate-handler any?
  [request any?]
  (let [{{:keys [email password]} :params :keys [session]} request]
    (if (check-auth email password)
      (assoc (http/ok {:identity email})
             :session (assoc session :identity email))
      (http/unauthorized {:status :unathorized}))))

(defn-spec register-handler ::register-handler-response
  "Register a user"
  [request ::register-request]
  (let [{:keys [params]} request]
    (if (s/valid? ::m.users/registration-params params)
      (do
        (m.users/create-user! params)
        (http/ok))
      (http/bad-request))))

(defn logout-handler
  [request]
  (assoc-in (http/ok {:identity nil}) [:session :identity] nil))
