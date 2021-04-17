(ns dinsro.queries.users-test
  (:require
   [clojure.test :refer [use-fixtures]]
   [dinsro.mocks :as mocks]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions behavior specification]]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.users/schema])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(specification "create-record"
  (behavior "when the params are valid"
    (let [params                   (ds/gen-key ::m.users/params)
          {::m.users/keys [email]} params
          id                       (q.users/create-record params)
          user                     (q.users/read-record id)]
      (assertions
       (::m.users/email user) => email)))
  (behavior "when the params are invalid"
    (let [params (ds/gen-key ::m.users/params)
          _id    (q.users/create-record params)]
      (assertions
       (q.users/create-record params) =throws=> RuntimeException))))

(specification "read-record"
  (behavior "when the record exists"
    (let [params                   (ds/gen-key ::m.users/params)
          {::m.users/keys [email]} params
          id                       (q.users/create-record params)
          response                 (q.users/read-record id)]
      (assertions
       (::m.users/email response) => email)))
  (behavior "missing"
    (let [id (ds/gen-key :db/id)]
      (assertions
       (q.users/read-record id) => nil))))

(specification "read-records"
  (behavior "empty"
    (assertions
     (q.users/read-records []) => []))
  (behavior "existing"
    (let [n 2]
      (dotimes [_ n] (mocks/mock-user))
      (let [records (q.users/index-records)
            ids     (map :db/id records)]
        (assertions
         (q.users/read-records ids) => records)))))
