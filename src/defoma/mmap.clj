(ns defoma.mmap
  (:import [java.nio MappedByteBuffer ByteBuffer ByteOrder]
           [java.nio.channels FileChannel FileChannel$MapMode]
           java.io.RandomAccessFile
           java.io.File)
  (:require [clojure.java.io :as io])
  (:gen-class))

(defrecord MmapFile [file buf])

(def cache-dir (io/file "/tmp/resources"))

(defn mmap-resource [path]
  (let [cache-file-path (io/file cache-dir path)]
    (when (not (.exists cache-file-path))
      (.mkdirs cache-dir)
      (io/copy (io/input-stream (io/resource path)) cache-file-path))
    (let [file (RandomAccessFile. cache-file-path "r")
          buf  (-> file .getChannel (.map FileChannel$MapMode/READ_ONLY 0 (.length file)))]
      (MmapFile. file buf))))

(comment
 (def resource (mmap-resource "trees9.3ds"))

 resource
)
