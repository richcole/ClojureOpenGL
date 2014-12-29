(ns deforma.util
  (:use clojure.java.io)
  (:gen-class))

(defn not-nil? [x] (not (nil? x)))

(defn write-to-file [path content]
  (with-open [w (output-stream path)]
    (.write w content)))
