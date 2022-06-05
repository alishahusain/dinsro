(ns dinsro.test-helpers
  #?(:clj
     (:require
      [dinsro.components.config :as config  :refer [secret]]
      [dinsro.components.xtdb :as c.xtdb]
      [mount.core :as mount]))
  #?(:cljs (:require-macros [dinsro.test-helpers])))

#?(:clj
   (defn start-db
     [f _schemata]
     (mount/stop #'c.xtdb/xtdb-nodes)
     (mount/start
      #'config/config
      #'secret
      #'c.xtdb/xtdb-nodes)
     (f)))
