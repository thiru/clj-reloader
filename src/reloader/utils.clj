(ns reloader.utils
  "General, non-domain-specific utilities."
  (:require ;; Clojure Core:
            [clojure.string :as str]
            [clojure.pprint :as pprint]))

(defn try-resolving
  "Attempts to resolve the given symbol ignoring any exceptions.

  Returns the resolved symbol if successful, otherwise `nil`."
  [sym]
  (try
    (requiring-resolve sym)
    (catch Throwable _)))

(defmacro spy
  "A simpler version of Timbre's spy, printing the original expression and the
  evaluated result.

  Returns the eval'd expression."
  [expr]
  `(let [evaled# ~expr]
     (print (str '~expr " => "))
     (pprint/pprint evaled#)
     evaled#))

