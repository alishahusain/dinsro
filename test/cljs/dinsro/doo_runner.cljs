(ns dinsro.doo-runner
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            dinsro.core-test))

(doo-all-tests #"dinsro\..*(?:-test)$")
