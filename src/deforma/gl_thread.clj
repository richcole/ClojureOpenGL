(ns deforma.gl_thread
  (:use clojure.stacktrace)
  (:gen-class))

(defonce queue (new java.util.concurrent.SynchronousQueue))

(defn exec [item]
  (try (item)
    (catch Throwable e 
      (println "Exception throw: " (.getMessage e))
      (print-stack-trace e)
      (println ""))))

(defn process-queue [queue]
  (exec (.take queue)))

(defonce gl-queue-future (future (while true (process-queue queue))))

(defmacro gl-do [ & f ]
  `(.put queue (fn [] (do ~@f))))

(defmacro catch-and-print-ex [ & f ]
  `(exec (fn [] (do ~@f))))


