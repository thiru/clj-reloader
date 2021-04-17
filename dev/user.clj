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
            [clojure.tools.namespace.repl :as nsrepl]
            [nextjournal.beholder :as beholder]
            [repl-base.core :as repls]

            ;; Our Domain:
            [reloader.core :as reloader]
            [reloader.globals :as g]
            [reloader.tt :as tt]))

(defn reload-ns
  "This is a work-around with tools.namespace. If this namespace is active in
  the REPL and gets changed, the REPL will still be pointing to the old
  namespace."
  []
  (println "BEFORE `user` reload")
  (in-ns 'reloader.core)
  (in-ns 'user)
  (println "Reloaded `user` namespace"))

(defonce init
  (do
    (when (not (:app-initialised? @g/state))
      (swap! g/state assoc :app-initialised? true)
      (reloader/start ["src" "dev"] :on-change reload-ns)
      (repls/start))))

(def x 11)
