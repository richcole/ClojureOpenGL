(ns deforma.loadblender
  (:use deforma.mmap deforma.util)
  (:import [java.nio MappedByteBuffer ByteBuffer ByteOrder]
           [java.nio.channels FileChannel FileChannel$MapMode]
           [org.lwjgl BufferUtils]
           java.io.RandomAccessFile
           deforma.mmap.MmapFile)
  (:gen-class))

(defn read-hdr [^MmapFile m]
  (let [f (:buf m)]
	  (.rewind f)
	  (let [i (read-chars f 7)
	        p (char (read-u8 f))
	        e (char (read-u8 f))
	        v (read-chars f 3)
	        r {:id i :version v
	           :endian (if (= e \v) ByteOrder/LITTLE_ENDIAN ByteOrder/BIG_ENDIAN)
	           :ptr-size  (if (= p \_) 32 64)
	           :f         f
	           :read-ptr  (if (= p \_) read-u32 read-u64)}]
	    (.order f (:endian r))
	    r)))

(defn read-word [hdr]
  (read-u32 (:f hdr)))

(defn read-half-word [hdr]
  (read-u16 (:f hdr)))

(defn read-ptr [hdr]
  ((:read-ptr hdr) (:f hdr)))

(defn read-file-block [hdr]
  (when (< (.position (:f hdr)) (.limit (:f hdr)))
	  (let [code       (read-chars (:f hdr) 4)
	        size       (read-word hdr)
	        old-mem    (read-ptr  hdr)
	        sdna-index (read-word hdr)
	        count      (read-word hdr)
	        buf        (read-buf  (:f hdr) size)
	        ]
    {:code code :size size :ptr old-mem :sdna-index sdna-index :count count :buf buf}))
)

(defn read-field [buf]
  {:type-index (read-u16 buf) :name-index (read-u16 buf)})

(defn read-struct-type [buf]
  (let [type (read-u16 buf)
        num-fields (read-u16 buf)
        fields (read-times buf num-fields read-field)]
    {:types-index type :fields fields}))

(defn read-dna [b]
  (let [buf (:buf b)
        _ (.rewind buf)
        id (read-chars buf 4)
        name-id (read-chars buf 4)
        num-names (read-u32 buf)
        names (read-times buf num-names read-strz)
        _ (read-align buf 4)
        types-id (read-chars buf 4)
        num-types (read-u32 buf)
        types (read-times buf num-types read-strz)
        _ (read-align buf 4)
        types-len-id (read-chars buf 4)
        types-len (read-times buf num-types read-u16) 
        _ (read-align buf 4)
        struct-id (read-chars buf 4)
        num-structs (read-u32 buf)
        struct-types (read-times buf num-structs read-struct-type)
        ]
    (assert (= id "SDNA"))
    (assert (= name-id "NAME"))
    (assert (= types-id "TYPE"))
    (assert (= types-len-id "TLEN"))
    (assert (= struct-id "STRC"))
    (let [comp-types (doall (for [i (range (count types))] 
                              (let [name (nth types i)
                                    size (nth types-len i)]
                                {:name name :size size})))
          comp-struct-types (doall (for [i (range (count struct-types))]
                                     (let [struct (nth struct-types i)
                                           name   (nth types (:types-index struct))
                                           fields (map (fn [field] {:name (nth names (:name-index field))
                                                                    :type (nth comp-types (:type-index field))})
                                                    (:fields struct))]
                                       {:name name :fields fields})))
          ]
      {:types comp-types
       :structs comp-struct-types})))

(defn read-file-blocks [hdr]
  (doall (take-while not-nil? (repeatedly #(read-file-block hdr))))) 

(defn file-block-type [types file-block]
  (nth (:structs types) (:sdna-index file-block)))

(defn read-field-value [hdr buf field]
  (let [name (:name field)
        type (:type field)] 
    (if (.startsWith name "*")
      [(:name field) ((:read-ptr hdr) buf)]
      [(:name field) (read-buf buf (:size type))])))

(defn convert-file-block [hdr types file-block]
  (let [stype (file-block-type types file-block)
        buf (:buf file-block)]
    (.rewind buf)
    (into {} (map #(read-field-value hdr buf %) (:fields stype)))))

(comment
  (def model (mmap-file "c:\\/Users/Richard/Documents/models/cube.blend")) 
  (def hdr (read-hdr model))  
  (def file-blocks (read-file-blocks hdr))
  (def types (read-dna (first (filter #(= (:code %) "DNA1") file-blocks))))
  (file-block-type types (first file-blocks))
  (convert-file-block hdr types (first file-blocks))
  (count (filter #(= (:ptr %) 435643835219) file-blocks))
  (convert-file-block hdr types (first (filter 
                                         #(= (:name (file-block-type types %)) "Scene") 
                                         file-blocks)))
  (count file-blocks)
  (into #{} (map #(:name (file-block-type types %)) file-blocks))
  (second file-blocks)
  (format "%x" 435643835219)
)







