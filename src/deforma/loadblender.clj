(ns deforma.loadblender
  (:import [java.nio MappedByteBuffer ByteBuffer ByteOrder]
           [java.nio.channels FileChannel FileChannel$MapMode]
           [org.lwjgl BufferUtils]
           [java.io RandomAccessFile])
  (:use deforma.mmap)
  (:gen-class))

(defn read-hdr [f]
  (let [i (read-chars f 7)
        p (read-u8 f)
        e (read-u8 f f)
        v (read-chars f 3)
        r {:id i :version v
           :endian (if (= e 'v') ByteOrder/LITTLE_ENDIAN ByteOrder/BIG_ENDIAN)
           :ptr-size  (if (= p '_') 32 64)
           :f         f
           :read-ptr  (if (= o '_') read-u32 read-u64)}]
    (.order (:endian r))
    r))

(defn read-word [hdr]
  (read-u32 (:f hdr)))

(def read-ptr [hdr]
  ((:read-ptr hdr) (:f hdr)))

(defn read-fileblock [hdr]
  (let [code       (read-chars (:f hdr) 4)
        size       (read-word  hdr)
        old_mem    (read-ptr hdr)
        sdna_index (read-word hdr)
        count      (read-word hdr)
        buf        (read-buf  size)
        ]
    {:code code :size size :old_mem old_mem :sdna_index index :count count}))
    
    


(comment





