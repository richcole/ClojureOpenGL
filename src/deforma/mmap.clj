(ns deforma.mmap
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

(defn read-u8 [^ByteBuffer f]
  (bit-and (.getByte f) 0xff))

(defn read-u16 [^ByteBuffer f]
  (bit-and (.getShort f) 0xffff))

(defn read-u32 [^ByteBuffer f]
  (bit-and (long (.getInt f)) 0xffffffff))

(defn read-u64 [^ByteBuffer f]
  (long (.getLong f)) 0xffffffff)

(defn read-byte [^ByteBuffer f]
  (.get f))

(defn not-zero [x]
  (not (= x 0)))

(defn short-buf [buf]
  (let [nbuf (.duplicate buf)
        _    (.order nbuf ByteOrder/LITTLE_ENDIAN)
        rbuf (.asShortBuffer nbuf)]
    (.limit rbuf (/ (.limit buf) 2))
    rbuf))

(defn float-buf [buf]
  (let [nbuf (.duplicate buf)
        _    (.order nbuf ByteOrder/LITTLE_ENDIAN)
        rbuf (.asFloatBuffer nbuf)]
    (.limit rbuf (/ (.limit buf) 4))
    rbuf))

(defn read-buf [f buf-len]
  (println "buf-len" buf-len)
  (let [buf (.slice f)]
    (.limit buf buf-len)
    (.position f (+ (.position f) buf-len))
    buf))

(defn read-strz [^ByteBuffer f]
  (let [s (apply str (map char 
                          (take-while not-zero (repeatedly (fn [] (.get f))))))]
    s))

(defn read-chars [^ByteBuffer f len]
  (let [bytes (byte-array 7)]
    (.get f bytes)
    (String. bytes)))

(comment
 (def resource (mmap-resource "trees9.3ds"))

 resource
)
