(ns dinsro.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators]
            [clojure.spec.gen.alpha :as gen]
            [tick.alpha.api :as tick]
            [time-specs.core :as ts]))

(defn valid-jwt? [jwt]
  (re-matches #"^[a-zA-Z0-9\-_]+?\.[a-zA-Z0-9\-_]+?\.([a-zA-Z0-9\-_]+)?$" jwt))

(defn uuid-str-gen []
  (gen/fmap str (s/gen uuid?)))

(defn gen-key
  [key]
  (gen/generate (s/gen key)))

#_(defn valid-uuid-str?
  "Ensures a match of the original uuid str with the result of coercing that str to
  and from a uuid"
  [^String uuid-str]
  (let [as-uuid (java.util.UUID/fromString uuid-str)]
    (= uuid-str (str as-uuid))))

(def non-empty-string-alphanumeric
  "Generator for non-empty alphanumeric strings"
  (gen/such-that #(not= "" %) (gen/string-alphanumeric)))

(def email-gen
  "Generator for email addresses"
  (gen/fmap
   (fn [[name host tld]]
     (str name "@" host "." tld))
   (gen/tuple
    non-empty-string-alphanumeric
    non-empty-string-alphanumeric
    non-empty-string-alphanumeric)))

(s/def ::id pos-int? #_(s/with-gen valid-uuid-str? uuid-str-gen))
(s/def :db/id ::id)
(def id ::id)

(s/def ::valid-double (s/and double? #(== % %) #(not (#{##Inf ##-Inf} %))))
(def valid-double ::valid-double)

(s/def ::date-string (s/with-gen string? #(s/gen #{(str (tick/instant))})))
(def date-string ::date-string)

(s/def ::email (s/with-gen #(re-matches #".+@.+\..+" %) (fn [] email-gen)))
(def email-gen ::email)

(s/def ::date (s/with-gen ts/instant? #(gen/fmap tick/instant (s/gen ::date-string))))
(def date ::date)

(s/def ::id-string (s/with-gen (s/and string? #(re-matches #"\d+" %))
                     #(gen/fmap str (s/gen pos-int?))))
(def id-string ::id-string)

(s/def ::not-found-status #{:not-found})
(def not-found-status ::not-found-status)

(comment
  (gen-key ::date)
  )
