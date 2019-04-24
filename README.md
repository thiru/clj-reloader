# Clojure Hot Code Reloader

A simply library for hot reloading your Clojure code.

Please note, **ClojureScript** is deliberately **unsupported**. [Figwheel](https://figwheel.org/) or [Shadow CLJS](http://shadow-cljs.org/) are probably your best options if you're working on a ClojurScript project. I personally only use this library for *ClojurScript-free* projects, and Figwheel-main otherwise.

## Usage

Add this to your *deps.edn*:

```clojure
github-thiru/clj-reloader {:git/url "https://github.com/thiru/clj-reloader" :sha "INSERT SHA HERE"}
```

Let's say you want to start the hot code reloader in your *user.clj* file:

```clojure
(ns user
  (:require [reloader.core :as reloader]))

(defonce hcr (reloader/start-watch ["src"]))
```

If you need to stop the reloader you can:

```clojure
(reloader/stop-watch hcr)
```

## Development

While working on this project I like to launch a REPL with the following script, as it sets up a development-friendly environment the way I like. You might like it to:

```shell
$ bin/repl
```
