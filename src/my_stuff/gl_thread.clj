(ns my-stuff.gl-thread)

(defonce queue (new java.util.concurrent.SynchronousQueue))

(defn exec [item]
  (try (item)
    (catch Throwable e 
      (println "Caught exception:" (.getMessage e)))))

(defn process-queue [queue]
  (exec (.take queue)))

(defonce gl-queue-future (future (while true (process-queue queue))))

(defmacro gl-do [ & f ]
  `(.put queue (fn [] (do ~@f))))

(defmacro catch-and-print-ex [ & f ]
  `(exec (fn [] (do ~@f))))


