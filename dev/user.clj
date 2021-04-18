(ns user
  "Default initial namespace when starting a REPL."
  (:require ;; Clojure Core:
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.pprint :refer :all]
            [clojure.reflect :refer :all]
            [clojure.repl :refer :all]
            [clojure.string :as str]

            ;; Third-Party:
            [nextjournal.beholder :as beholder]
            [ns-tracker.core :as nst]
            [repl-base.core :as repls]

            ;; Our Domain:
            [reloader.core :as reloader]))

(defonce started? (atom false))

(when (not @started?)
  (reset! started? true)
  (reloader/start ["src" "dev"])
  (repls/start))

