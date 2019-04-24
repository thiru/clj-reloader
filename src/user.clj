(ns user
  "This namespace is loaded and made active when a REPL is started."
  (:require ;; Clojure Core:
            [clojure.java.io :as io]
            [clojure.pprint :refer :all]
            [clojure.reflect :refer :all]
            [clojure.repl :refer :all]
            [clojure.string :as str]

            ;; Third-Party:
            [cider.nrepl :as cider]
            [nrepl.server :as nrepl]
            [rebel-readline.main :as rebel]

            ;; App-Specific:
            [reloader.core :as reloader]))

;; Start hot code reloader
(defonce hcr (reloader/start-watch ["src"]))

(defn -main
  "Setup nREPL and start Rebel Readline."
  []
  (let [nrepl-port 7888
        nrepl-port-file ".nrepl-port"]
    (nrepl/start-server :port nrepl-port
                        :handler cider/cider-nrepl-handler)

    ;; Editors like vim-fireplace look at this file to automatically connect
    (spit nrepl-port-file nrepl-port)
    (println "nREPL server started on port" nrepl-port)

    ;; Start Rebel Readline (this is a blocking call)
    (rebel/-main)

    ;; Might as well delete the nrepl port file on exit
    (if (.exists (io/as-file nrepl-port-file))
      (io/delete-file nrepl-port-file true))

    (System/exit 0)))
