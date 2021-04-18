(ns reloader.core
  "Sole namespace containing all functionality of this library."
  (:require ;; Clojure Core:
            [clojure.string :as str]

            ;; Third-Party:
            [ns-tracker.core :as nst]
            [nextjournal.beholder :as beholder]))

;; ## Vars
;; ----------------------------------------------------------------------------

(declare -on-change)

(defonce
  ^{:doc "Initially empty, but after `start` is called it contains a map with
         the following keys:
         * `:dirs`
           * A vector of directories being monitored
           * We store this primarily to be able to restart the watcher
         * `:modified-namespaces`
           * A function created by the `ns-tracker` library that returns the
             list of namespaces that need to be reloaded
           * See `nst/ns-tracker` for more info
         * `:watcher`
           * Contains an instance of the directory watcher
           * We store this in order to stop/restart the watcher
           * See `beholder/watch` for more info"}
  state (atom {}))

;; ## Primary API
;; ----------------------------------------------------------------------------

(defn start
  "Start watching the given directories for file-system change events in order
  to hot-reload code within the directories.

  * `dirs`
    * A list of directories (strings)

  NOTE: if watching was previously started a new watch won't start without
  first stopping the existing one (via `stop`).

  Side-effects:
  * Modifies `state`

  Returns an instance of the watcher if successfull, otherwise `nil`."
  [dirs]
  (cond
    (empty? dirs)
    (println "Not watching any directories as none were provided")

    (not (empty? @state))
    (println "Watcher already running. Please stop existing watcher first.")

    :else
    (let [modified-namespaces (nst/ns-tracker dirs)
          watcher (apply beholder/watch -on-change dirs)]
      (if (nil? watcher)
        (println "Failed to start watcher on:" (str/join ", " dirs))
        (do
          (println "Started monitoring" (str/join ", " dirs)
                   "for hot code reloading")
          (reset! state {:dirs dirs
                         :modified-namespaces modified-namespaces
                         :watcher watcher})
          watcher)))))

(defn stop
  "Stop watching directories for changes.

  Side-effects:
  * Modifies `state`

  Returns `true` if watching stopped successfully, otherwise `false`."
  []
  (if (empty? @state)
    (do
      (println "No file-system watcher currently running.")
      false)
    (do
      (beholder/stop (:watcher @state))
      (println "Stopped monitoring" (str/join ", " (:dirs @state))
               "for hot code reloading")
      (reset! state {})
      true)))

(defn restart
  "Restart current watcher.

  Side-effects:
  * Modifies `state`

  Returns an instance of the watcher if successful, otherwise `nil`."
  []
  (if (empty? (:watcher @state))
    (println "Not able to restart watcher as it was never originally started")
    (let [curr-dirs (:dirs @state)]
      (and (stop)
           (start curr-dirs)))))

;; ## Helpers
;; ----------------------------------------------------------------------------

(defn -on-change
  "Handle a file-system change event (e.g. file was modified)."
  [info]
  (let [file-name (-> info :path .getFileName str (or ""))]
    (when (and info
               ;; We're only interested in modified events. Create events are
               ;; superfluous as they're always immediately proceeded by a
               ;; modified event. There's nothing we can (currently) do with
               ;; delete events as unloading code isn't supported.
               (= :modify (:type info))
               (or (str/ends-with? file-name "clj")
                   (str/ends-with? file-name "cljc")))
      (let [ns-modified ((:modified-namespaces @state))
            num-ns-modified (count ns-modified)]
        (when (pos? num-ns-modified)
          (doseq [ns-sym ns-modified]
            (require ns-sym :reload))
          (println (format "File '%s' was modified (%d namespace%s reloaded)"
                           file-name
                           num-ns-modified
                           (if (= 1 num-ns-modified) "" "s"))))))))

