(ns reloader.globals
  "Simply contains an atom to hold global state. This is needed as all other
  namespaces will be unloaded/reloaded by tools.namespace."
  (:require ;; Clojure Core:
            [clojure.string :as str]

            ;; Third-Party:
            [clojure.tools.namespace.repl :as nsrepl]))

(defonce init
  (nsrepl/disable-reload! *ns*))

(defonce
  ^{:doc "Global state atom for this library."}
  state (atom {}))


(defn change
  []
  (println "CHANGE IN GLOBALS"))
