(ns reloader.core
  "Sole namespace containing all library-specific logic."
  (:require ;; Clojure Core:
            [clojure.string :as str]

            ;; Third-Party:
            [clojure.tools.namespace.repl :as nsrepl]
            [nextjournal.beholder :as beholder]))

;; ## Vars
;; ----------------------------------------------------------------------------

(declare -on-change)

(def empty-watch-state {:dirs ""
                        :instance nil})

(defonce
  ^{:doc "Contains an instance of the file-system watcher and the directories
         being watched. This is needed in order to stop a running watcher."}
  watch-state (atom empty-watch-state))

;; ## Primary API
;; ----------------------------------------------------------------------------

(defn start
  "Start watching the given directories for file-system change events in order
  to hot-reload code.

  * `dirs`
    * A list of directories (strings)

   Returns an instance of the watcher if successfull, otherwise `nil`."
  [dirs]
  (cond
    (empty? dirs)
    (println "Not watching any directories as none were provided")

    (not= empty-watch-state @watch-state)
    (println "Watcher already running. Please stop existing watcher first.")

    :else
    (let [watcher (apply beholder/watch -on-change dirs)]
      (if (nil? watcher)
        (println "Failed to start watcher on:" (str/join ", " dirs))
        (do
          (println "Started monitoring" (str/join ", " dirs)
                   "for hot code reloading")
          (reset! watch-state {:dirs dirs
                               :instance watcher})
          watcher)))))

(defn stop
  "Stop watching directories for changes.

  Returns `true` if watching stopped successfully, otherwise `false`."
  []
  (if (= empty-watch-state @watch-state)
    (do
      (println "No file-system watcher currently running.")
      false)
    (do
      (beholder/stop (:instance @watch-state))
      (println "Stopped monitoring" (str/join ", " (:dirs @watch-state))
               "for hot code reloading")
      (reset! watch-state empty-watch-state)
      true)))

(defn restart
  "Restart current watcher.

   Returns an instance of the watcher if successfull, otherwise `nil`."
  []
  (if (= empty-watch-state @watch-state)
    (println "Not able to restart watcher as it was never originally started")
    (let [curr-dirs (:dirs @watch-state)]
      (and (stop)
           (start curr-dirs)))))

;; ## Helpers
;; ----------------------------------------------------------------------------

(defn -on-change
  "TODO"
  [info]
  (println "INFO:" info))
