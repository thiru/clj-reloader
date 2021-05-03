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
            [io.aviso.exception :as aviso-ex]
            [io.aviso.repl :as aviso-repl]
            [nextjournal.beholder :as beholder]
            [ns-tracker.core :as nst]
            [repl-base.core :as repls]

            ;; Our Domain:
            [reloader.core :as reloader]))

(defn setup-pretty-exceptions
  "Hook into exception printing and print with aviso library."
  []
  (alter-var-root #'aviso-ex/*app-frame-names*
                  (constantly [#"reloader.*" #"user"]))
  (aviso-repl/install-pretty-exceptions))

(defonce started? (atom false))

(when (not @started?)
  (reset! started? true)
  (setup-pretty-exceptions)
  (reloader/start ["src" "dev"])
  (repls/start))

