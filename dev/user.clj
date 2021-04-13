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
            [repl-base.core :as repls]

            ;; Our Domain:
            [reloader.core :as reloader]))

(defonce init
  (do
    (reloader/start ["src" "dev"])
    (repls/start)))

