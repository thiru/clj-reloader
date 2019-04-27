(ns user
  "This namespace is loaded and made active when a REPL is started."
  (:require ;; Clojure Core:
            [clojure.java.io :as io]
            [clojure.pprint :refer :all]
            [clojure.reflect :refer :all]
            [clojure.repl :refer :all]
            [clojure.string :as str]

            ;; App-Specific:
            [reloader.core :as reloader]))

(defonce hcr (reloader/start-watch ["src"]))
