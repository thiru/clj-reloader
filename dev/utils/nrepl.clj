(ns utils.nrepl

  "nREPL startup & shutdown."

  (:require
            [clojure.java.io :as io]
            [nrepl.server :as nrepl]))

(defonce nrepl-server (atom nil))

(defn start-server []
  (reset! nrepl-server (nrepl/start-server :port 0))
  (println "nREPL server started on port" (:port @nrepl-server))
  (spit ".nrepl-port" (:port @nrepl-server)))

(defn stop-server []
  (when @nrepl-server
    (io/delete-file ".nrepl-port" :silently)))
