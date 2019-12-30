(ns dinsro.actions.transactions-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [datahike.api :as d]
            [datahike.config :refer [uri->config]]
            [dinsro.actions.transactions :as a.transactions]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.mocks :as mocks]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [mount.core :as mount]
            [ring.util.http-status :as status]
            [tick.alpha.api :as tick]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (d/transact db/*conn* s.currencies/schema)
      (d/transact db/*conn* s.transactions/schema)
      (f))))

(deftest prepare-record
  (let [currency-id 1
        account-id 1
        description "foo"
        value 1
        date (tick/instant)
        params {:currency-id currency-id
                :account-id account-id
                :description description
                :date (str date)
                :value value}
        response (a.transactions/prepare-record params)
        expected {::s.transactions/currency {:db/id currency-id}
                  ::s.transactions/date date
                  ::s.transactions/description description
                  ::s.transactions/value (double value)
                  ::s.transactions/account {:db/id account-id}}]
    (is (= expected response))))

(deftest create-record-response-test
  (let [request (ds/gen-key s.a.transactions/create-request-valid)
        response (a.transactions/create-handler request #_{:params params})]
    (is (= (:status response) status/ok))))

(deftest index-handler-empty
  (let [request {}
        response (a.transactions/index-handler request)]
    (is (= (:status response) status/ok))))

(deftest index-handler-with-records
  (mocks/mock-transaction)
  (let [request {}
        response (a.transactions/index-handler request)]
    (is (= (:status response) status/ok))
    (is (= 1 (count (:items (:body response)))))))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.transactions/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest delete-handler-success
  (let [item (mocks/mock-transaction)
        id (:db/id item)
        request {:path-params {:id (str id)}}]
    (is (not (nil? (m.transactions/read-record id))))
    (let [response (a.transactions/delete-handler request)]
      (is (= status/ok (:status response)))
      (is (nil? (m.transactions/read-record id))))))

(deftest read-handler-success
  (let [item (mocks/mock-transaction)
        id (str (:db/id item))
        request {:path-params {:id id}}
        response (a.transactions/read-handler request)]
    (is (= status/ok (:status response)))
    (is (= item (get-in response [:body :item])))))

(deftest read-handler-not-found
  (let [request (ds/gen-key ::ds/common-read-request)
        response (a.transactions/read-handler request)]
    (is (= status/not-found (:status response))
        "Returns a not-found status")

    (is (= :not-found (get-in response [:body :status]))
        "Has a not found status field")))
