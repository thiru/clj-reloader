(ns reloader.core
  "Sole namespace containing all library-specific logic."
  (:require ;; Clojure Core:
            [clojure.string :as str]

            ;; Third-Party:
            [clojure.tools.namespace.repl :as nsrepl]
            [nextjournal.beholder :as beholder]

            ;; Our Domain:
            [reloader.globals :as g]))

;; ## Vars
;; ----------------------------------------------------------------------------

(declare -on-change)

(defonce init
  (nsrepl/disable-reload! *ns*))

;; ## Primary API
;; ----------------------------------------------------------------------------

(defn start
  "Start watching the given directories for file-system change events in order
  to hot-reload code.

  * `dirs`
    * A list of directories (strings)

  * `on-change`
    * TODO

  Returns an instance of the watcher if successfull, otherwise `nil`."
  [dirs & {:keys [on-change]}]
  (nsrepl/refresh)
  (cond
    (empty? dirs)
    (println "Not watching any directories as none were provided")

    (not (empty? (:fs-watcher @g/state)))
    (println "Watcher already running. Please stop existing watcher first.")

    :else
    (let [watcher (apply beholder/watch
                         #(-on-change % on-change)
                         dirs)]
      (if (nil? watcher)
        (println "Failed to start watcher on:" (str/join ", " dirs))
        (do
          (println "Started monitoring" (str/join ", " dirs)
                   "for hot code reloading")
          (swap! g/state assoc :fs-watcher {:dirs dirs
                                            :instance watcher})
          watcher)))))

(defn stop
  "Stop watching directories for changes.

  Returns `true` if watching stopped successfully, otherwise `false`."
  []
  (if (empty? (:fs-watcher @g/state))
    (do
      (println "No file-system watcher currently running.")
      false)
    (do
      (beholder/stop (-> @g/state :fs-watcher :instance))
      (println "Stopped monitoring" (str/join ", "
                                              (-> @g/state :fs-watcher :dirs))
               "for hot code reloading")
      (swap! g/state assoc :fs-watcher {})
      true)))

(defn restart
  "Restart current watcher.

   Returns an instance of the watcher if successfull, otherwise `nil`."
  []
  (if (empty? (:fs-watcher @g/state))
    (println "Not able to restart watcher as it was never originally started")
    (let [curr-dirs (-> @g/state :fs-watcher :dirs)]
      (and (stop)
           (start curr-dirs)))))

;; ## Helpers
;; ----------------------------------------------------------------------------

(defn do-on-change
  []
  (println "do on change worked"))

(defn -on-change
  "Handle a file-system change event."
  [info on-change]
  (let [file-name (-> info :path .getFileName str (or ""))]
    (when (and info
               ;; We're only interested in modified and deleted events. Create
               ;; events are superfluous as they're always immediately
               ;; proceeded by a modified event.
               (or (= :modify (:type info))
                   (= :delete (:type info)))
               (or (str/ends-with? file-name "clj")
                   (str/ends-with? file-name "cljc")))
      (println (format "File '%s' was %s - "
                       file-name
                       (condp = (:type info)
                         :modify "modified"
                         :delete "deleted"
                         (name (:type info)))))
      (println "before refresh")
      (nsrepl/refresh :after 'reloader.core/do-on-change))))
      ;(nsrepl/refresh :after 'user/reload-ns))))
      ;(println "after refresh")
      ;(on-change))))
