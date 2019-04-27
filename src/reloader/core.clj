(ns reloader.core
  "Sole namespace containing all library-specific logic."
  (:require ;; Clojure Core:
            [clojure.data :as data]
            [clojure.pprint :as pp]
            [clojure.string :as str]

            ;; Third-Party:
            [hawk.core :as hawk]
            [ns-tracker.core :as nst]))

(declare
  reload
  start-watch
  stop-watch
  watcher-filter)

;;; Primary API ---------------------------------------------------------------

(defn start-watch
  "Start watching the specified directories for file-system change events.

   * `dirs`
     * A list of directory names

   Returns a map with these keys:
   * `:dirs`
     * The directories beings watched
   * `:ns-tracker-fn`
     * A function, when called, returns a set of namespaces that need to be
       reloaded
   * `:watcher`
     * The *hawk* file-system watcher object
     * We need this to be able to stop the file-system watching
       * e.g. via `stop-watch`"
  [dirs]
  (if (empty? dirs)
    (throw (ex-info "No watch directories provided" {})))
  (let [ns-tracker-fn (nst/ns-tracker dirs)
        cfg {:dirs dirs
             :ns-tracker-fn ns-tracker-fn
             :watcher (hawk/watch! [{:paths dirs
                                     :filter watcher-filter
                                     :handler (fn [ctx e]
                                                (reload e ns-tracker-fn)
                                                ctx)}])}]
    (println
      (pp/cl-format nil
                    "Hot code reloader started watching ~A folder~:P: ~A"
                    (count dirs)
                    (str/join ", " dirs)))
    cfg))

(defn stop-watch
  "Stop watching directories for changes.

   * `watch-config`
     * A map as specified as the return from `start-watch`"
  [watch-config]
  (when (and watch-config
             (:watcher watch-config))
    (hawk/stop! (:watcher watch-config))
    (println
      (pp/cl-format nil
                    "Hot code reloader stopped watching ~A folder~:P: ~A"
                    (count (:dirs watch-config))
                    (str/join ", " (:dirs watch-config))))))

;;; Primary API ===============================================================

(defn reload
  "Reload modified namespaces.

  * `fs-event`
    * A map describing the file-system event that occurred"
  [fs-event ns-tracker-fn]
  (let [namespaces (ns-tracker-fn)]
    (when (and (:file fs-event) (pos? (count namespaces)))
      (println
        (pp/cl-format nil
                      "HCR: (~A) ~A (~A namespace~:P reloaded)"
                      (str/upper-case (name (:kind fs-event)))
                      (.getPath (:file fs-event))
                      (count namespaces)))
      (doseq [ns-sym namespaces]
        ;(println (str "(require " ns-sym " :reload)")) ; DEBUG
        (require ns-sym :reload)))))

(defn watcher-filter
  "Filter the type of file-system events we're interested in."
  [_ctx {:keys [kind file]}]
  (and file
       ;; We're only interested in file modified events. I don't think we need
       ;; to care about file deleted events as we can't unload the code anyway
       ;; (as far as I know), and file created events don't seem significant.
       (= :modify kind)
       ;; Ignore directory events
       (.isFile file)
       (not (.isHidden file))
       (let [file-name (.getName file)]
         ;; Ignore hidden/temporary files
         (and (not= \. (first file-name))
              (not= \# (first file-name))
              (not= \~ (last file-name))
              ;; Only interested in Clojure file types
              (or (str/ends-with? file-name "clj")
                  (str/ends-with? file-name "cljc"))))))
