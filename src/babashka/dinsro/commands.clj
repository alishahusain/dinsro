(ns dinsro.commands
  (:require [babashka.tasks :refer [clojure shell]]
            [clojure.string :as str]))

(defn cljfmt
  [paths]
  (clojure
   (format "-M:cljfmt check %s --indents indentation.edn"
           (str/join " " paths))))

(defn earthly
  ([cmd]
   (earthly cmd {}))
  ([cmd opts]
   (println opts)
   (let [flags (->> [(when (:interactive opts) "-i")
                     (when (:privileged opts) "-P")]
                    (filter identity)
                    (str/join " "))]
     (println flags)
     (shell
      (format "earthly %s +%s" flags (name cmd))))))

(defn eight
  []
  (shell
   (format
    "echo \"%s\""
    (str/join
     "\n"
     ;; From: https://codepen.io/andrewarchi/pen/eJZjej
     ["    _-=-_    "
      "   ´´´-```   "
      "  (  (O)  )  "
      "  []=__○_[]  "
      " ´ |    °  `  "
      "´-´ ° _---_ ` "
      "|    ´ □=□ `|"
      "|  ° |  -  || "
      "`    | □=□ ´´"
      " `--_____--´  "]))))

(defn get-docker-status
  [container-name]
  (:out (shell
    {:out :string}
    (format "docker inspect -f {{.State.Health.Status}} %s" container-name))))

(defn chown
  [user group path]
  (shell (format "chown -R %s:%s %s" user group path)))

(defn sudo
  ([cmd]
   (sudo "root" cmd))
  ([user cmd]
   (shell (format "sudo -u %s %s" user cmd))))

(defn display-env
  []
  (shell "sh -c \"env | sort\""))

(defn migrate-env
  [names]
  (->> names
       (map (fn [name] (when-let [v (System/getenv name)] (str name "=" v))))
       (filter identity)))