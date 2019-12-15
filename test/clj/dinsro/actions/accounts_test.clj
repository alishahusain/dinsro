(ns dinsro.actions.accounts-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [datahike.api :as d]
            [datahike.core :as dc]
            [dinsro.actions.accounts :as a.accounts]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.model.account :as m.accounts]
            [dinsro.model.user :as m.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [mount.core :as mount]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (datahike.config/uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (d/transact db/*conn* s.accounts/schema)
      (f))))

(deftest index-handler-test
  (testing "success"
    (let [request {:params {}}]
      (is [] (a.accounts/index-handler request))))
  (testing "with-records"
    (let [user (m.accounts/mock-record)
          request {}
          response (a.accounts/index-handler request)
          {{:keys [items]} :body} response]
      (is (= [user] items)))))

(deftest create-handler-valid
  (testing "success"
    (let [request (gen/generate (s/gen ::a.accounts/create-handler-request-valid))
          response (a.accounts/create-handler request)]
      (is (= status/ok (:status response)))
      #_(is (= nil response)))))

(deftest create-handler-invalid
  (let [request {:params {}}
        response (a.accounts/create-handler request)]
    (is (= status/bad-request (:status response)))
    #_(is (= nil response))))

(deftest delete-handler
  (testing "success"
    (let [account (m.accounts/mock-record)
          id (:db/id account)
          request {:path-params {:id (str id)}}
          response (a.accounts/delete-handler request)]
      (is (= status/ok (:status response)) "successful status")
      (is (nil? (m.accounts/read-record id)) "account is deleted"))))

(comment
  (gen/generate (s/gen ::a.accounts/create-handler-request-valid))

  (expound.alpha/expound
   ::a.accounts/delete-handler-request
   {:path-params {:accountId "7"}})


  )
