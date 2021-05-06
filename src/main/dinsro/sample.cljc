(ns dinsro.sample
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]))

(defn account-line
  [id initial-value name currency-id username]
  {::m.accounts/id            id
   ::m.accounts/initial-value initial-value
   ::m.accounts/name          name
   ::m.accounts/currency      {::m.currencies/id currency-id}
   ::m.accounts/user          {::m.users/username username}})

(defn category-line
  [id name username]
  {::m.categories/id   id
   ::m.categories/name name
   ::m.categories/user {::m.users/username username}})

(defn currency-line
  [id name]
  {::m.currencies/id   id
   ::m.currencies/name name})

(defn navlink-line
  [id name href path]
  {:navlink/id   id
   :navlink/name name
   :navlink/href href
   :navlink/path path})

(defn rate-line
  [id currency-id rate date]
  {::m.rates/id       id
   ::m.rates/currency {::m.currencies/id currency-id}
   ::m.rates/rate     rate
   ::m.rates/date     date})

(defn rate-source-line
  [id name currency-id url]
  {::m.rate-sources/id       id
   ::m.rate-sources/name     name
   ::m.rate-sources/currency {::m.currencies/id currency-id}
   ::m.rate-sources/url      url})

(defn transaction-line
  [id description date account-id]
  {::m.transactions/id          id
   ::m.transactions/description description
   ::m.transactions/date        date
   ::m.transactions/account     {::m.accounts/id account-id}})

(defn user-line
  [username]
  {::m.users/username username})

(def account-map
  {1 (account-line 1 1  "Savings Account" 2 1)
   2 (account-line 2 20 "Fun Money"       1 2)})

(def category-map
  {1 (category-line 1 "Savings"  1)
   2 (category-line 2 "Spending" 2)})

(def currency-map
  {"usd"  (currency-line "usd" "Dollars")
   "eur"  (currency-line "eur" "Euros")
   "sats" (currency-line "sats" "Sats")
   "yen"  (currency-line "yen" "Yen")})

(def navlink-map
  {:accounts     (navlink-line :accounts     "Accounts"     "/accounts"     ["accounts"])
   :admin        (navlink-line :admin        "Admin"        "/admin"        ["admin"])
   :bar          (navlink-line :bar          "bar"          "/bar"          ["bar"])
   :baz          (navlink-line :baz          "baz"          "/baz"          ["baz"])
   :categories   (navlink-line :categories   "Categories"   "/categories"   ["categories"])
   :currencies   (navlink-line :currencies   "Currencies"   "/currencies"   ["currencies"])
   :foo          (navlink-line :foo          "foo"          "/foo"          ["foo"])
   :home         (navlink-line :home         "Home"         "/"             [""])
   :login        (navlink-line :login        "Login"        "/login"        ["login"])
   :rates        (navlink-line :rates        "Rates"        "/rates"        ["rates"])
   :rate-sources (navlink-line :rate-sources "Rate Sources" "/rate-sources" ["rate-sources"])
   :registration (navlink-line :registration "Registration" "/register"     ["register"])
   :settings     (navlink-line :settings     "Settings"     "/settings"     ["settings"])
   :transactions (navlink-line :transactions "Transactions" "/transactions" ["transactions"])
   :users        (navlink-line :users        "User"         "/users"        ["users"])})

(def rate-map
  {1 (rate-line 1 1 1.01 "2020-12-05 22:11:04")})

(def rate-source-map
  {1 (rate-source-line 1 "CoinLott0"   1 "https://www.coinlott0.localhost/api/v1/quotes/BTC-USD")
   2 (rate-source-line 2 "BitPonzi"    1 "https://www.bitponzi.biz.localhost/cgi?id=3496709")
   3 (rate-source-line 3 "DuckBitcoin" 1 "https://www.duckbitcoin.localhost/api/current-rates")
   4 (rate-source-line 4 "Leviathan"   1 "https://www.leviathan.localhost/prices")})

(def transaction-map
  {1 (transaction-line 1 "bought a thing"   "2020-11-28 00:00:00" 1)
   2 (transaction-line 2 "sold some things" "2020-11-28 01:00:00" 1)})

(def user-map
  {1 (user-line "foo")
   2 (user-line "bar")})
