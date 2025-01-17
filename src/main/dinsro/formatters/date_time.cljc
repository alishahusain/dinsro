(ns dinsro.formatters.date-time
  (:refer-clojure :exclude [name])
  (:import
   #?(:clj (java.time LocalDateTime ZoneId Instant))
   #?(:clj (java.time.format DateTimeFormatter))
   #?(:clj (java.util Date))
   #?(:cljs goog.i18n.DateTimeFormat)))

#?(:clj (def ^:private ^:static ^ZoneId UTC (ZoneId/of "UTC")))

(defn date-year
  [date]
  #?(:cljs (assert (instance? js/Date date) (str "`date` " date ": " (type date) " isn't js/Date")))
  #?(:clj  (.getYear ^LocalDateTime date)
     :cljs (.getFullYear date)))

(defn current-year []
  (date-year
   #?(:clj  (LocalDateTime/now UTC)
      :cljs (js/Date.))))

(def fmt #?(:clj (-> (DateTimeFormatter/ofPattern "MMM d")
                     (.withZone UTC))
            :cljs (DateTimeFormat. "MMM d")))

#?(:clj (defn ->local-date ^LocalDateTime [^Date date]
          (condp instance? date
            Date          (->local-date (.toInstant date))
            Instant       (LocalDateTime/ofInstant date UTC)
            LocalDateTime date)))

#?(:clj (defn ->instant ^Instant [date]
          (condp instance? date
            Date    (.toInstant date)
            Instant date)))

(defn format-date [^Instant instant]
  (if instant
    (let [date #?(:clj (->local-date instant)
                  :cljs instant)
          year (date-year date)]
      (cond-> (.format fmt date)
        (not= year (current-year)) (str ", " year)))
    "N/A"))

(defn format-period [from to]
  (str (format-date from) " - " (format-date to)))

(defn date-formatter
  "RAD report formatter that can be set on a RAD attribute (`::report/field-formatter`)"
  [_report-instance value]
  (format-date value))

(comment
  (format-date #inst "2019-08-31T22:00:00.000-00:00") ; => "Aug 31, 2019"
  nil)
