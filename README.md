# Clojure Hot Code Reloader

TODO

A simple library for hot reloading your Clojure code.

Please note, **ClojureScript** is deliberately **unsupported**. [Figwheel](https://figwheel.org/) or [Shadow CLJS](http://shadow-cljs.org/) are probably your best options if you're working on a ClojurScript project. I personally only use this library for *ClojurScript-free* projects, and Figwheel-main otherwise.

## Usage

Add this to your **deps.edn**:

```clojure
github-thiru/clj-reloader {:git/url "https://github.com/thiru/clj-reloader" :sha "INSERT SHA HERE"}
```
Example of starting the hot code reloader in **user.clj**:

```clojure
(ns user
  (:require [reloader.core :as reloader]))

(defonce hcr (reloader/start-watch ["src"]))
```
And if you need to stop the hot code reloader:

```clojure
(reloader/stop-watch hcr)
```
## Development

While working on this project I like to launch a REPL with the following script, as it sets up a development-friendly environment the way I like (nREPL, Rebel-Readline). You might like it to:

```shell
$ bin/dev
```
