(ns dinsro.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [dinsro.components.config :as config]
   [dinsro.components.http]
   [dinsro.components.nrepl]
   [dinsro.components.secrets :as secrets]
   [dinsro.middleware.middleware :as middleware]
   [mount.core :as mount]
   [taoensso.timbre :as timbre])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (timbre/error {:what      :uncaught-exception
                    :exception ex
                    :where     (str "Uncaught exception on" (.getName thread))} ex))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (timbre/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (timbre/info "starting app")
  (let [options (parse-opts args cli-options)]
    (doseq [component (-> options mount/start-with-args :started)]
      (timbre/debug component "started")))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main
  [& args]
  (let [[config-file] args]
    (-> (mount/only #{#'config/config})
        (mount/with-args {:config config-file})
        mount/start))
  (mount/start #'secrets/secret)
  (mount/start #'middleware/token-backend)
  (start-app args))
