(ns utils.printing

  "Various print debugging utilities."

  (:require
    [puget.printer :as puget]))

(defmacro PP
  "Convenience macro to pretty-print last evaluated result at the REPL."
  []
  `(puget/cprint *1))
