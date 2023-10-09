# Clojure Hot Code Reloader

A simple library for hot reloading your Clojure code.

Please note, **ClojureScript** is deliberately **unsupported**. [Figwheel](https://figwheel.org/) or [Shadow CLJS](http://shadow-cljs.org/) are probably your best options if you're working on a ClojureScript project. I personally only use this library for ClojureScript-free projects, and Figwheel-main otherwise.

Exceptions and throwables that occur while reloading code will be pretty-printed.

## Usage

Add this to your **deps.edn**:

```clojure
thiru/clj-reloader {:git/url "https://github.com/thiru/clj-reloader"
                    :sha "<COMMIT SHA>"}
```

Example of starting the hot code reloader to monitor the *src* and *dev*
directories:

```clojure
(require '[reloader.core :as reloader])

(reloader/start ["src" "dev"])
```

And if you need to stop the hot code reloader:

```clojure
(reloader/stop)
```

