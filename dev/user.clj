(ns user
  "Default initial namespace when starting a REPL."
  (:require ;; Clojure Core:
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.pprint :as pp]
            [clojure.reflect :as reflect]
            [clojure.string :as str]

            ;; Third-Party:
            [nextjournal.beholder :as beholder]
            [ns-tracker.core :as nst]
            [rebel-readline.main :as rebel]

            ;; Our Domain:
            [reloader.core :as reloader]

            ;; Our Utils:
            [utils.nrepl :as nrepl]
            [utils.printing :refer [PP]]))

(defonce ^{:doc "Used to ensure we don't start more than once."}
  started? (atom false))

(defn start
  "Start & init dev environment (only once)."
  []
  (when (not @started?)
    (reset! started? true)
    (nrepl/start-server)
    (reloader/start ["src" "dev"])
    ;; Blocking call:
    (rebel/-main)
    (nrepl/stop-server)))

(start)
