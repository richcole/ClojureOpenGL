(ns defoma.core
  (:import [java.nio MappedByteBuffer ByteBuffer ByteOrder]
           [java.nio.channels FileChannel FileChannel$MapMode]
           [java.io RandomAccessFile])
  (:use  defoma.gl-thread)
  (:gen-class))

(def file (RandomAccessFile. "/home/richcole/models/trees9/trees9.3ds" "r"))
(def f (-> file .getChannel (.map FileChannel$MapMode/READ_ONLY 0 (.length file))))

(defn read-u8 [^ByteBuffer f]
  (bit-and (.getByte f) 0xff))

(defn read-u16 [^ByteBuffer f]
  (bit-and (.getShort f) 0xffff))

(defn read-u32 [^ByteBuffer f]
  (bit-and (long (.getInt f)) 0xffffffff))

(defn read-float [^ByteBuffer f]
  (.getFloat f))

(defn read-byte [^ByteBuffer f]
  (.get f))

(defn not-zero [x]
  (not (= x 0)))

(defn read-strz [^ByteBuffer f]
  (let [s (apply str (map char 
                          (take-while not-zero (repeatedly (fn [] (.get f))))))]
    (println "s" s)
    s))

(declare read-nodes)
(declare read-node)

(defn not-nil? [x] (not (nil? x)))

(defn read-nodes [f end]
  (doall (take-while not-nil? (repeatedly (fn [] (read-node f end))))))


(range 0 10)

(defn read-buf [f buf-len]
  (let [buf (.slice f)]
    (.limit buf buf-len)
    (.position f (+ (.position f) buf-len))
    buf))

(defn read-faces-description [f]
  (let [num-faces (read-u16 f)]
    (println "num-faces" num-faces)
    {:num-faces num-faces :indexes (read-buf f (* num-faces 4 2)) }
    ))

(defn read-triangular-mesh [f]
  (let [num-vertices (read-u16 f)]
    (println "num-vertices" num-vertices)
    {:num-vertices num-vertices :verticies (read-buf f (* num-vertices 3 4))}))

(defn read-version [f] 
  {:version (read-u32 f)})

(defn read-name [f]
  {:name (read-strz f)})

(defn read-frames [f]
  { :start (read-u32 f) :end (read-u32 f)})

(defn read-nothing [f]
  {})

(defn to-node-map [node-types]
  (into {} (map (fn [x] [(:id x) x]) node-types)))

(defrecord NodeType [id name reader has-children])

(def node-types
  (to-node-map 
   [
    (NodeType. 0x4d4d "Main"            read-nothing           true)
    (NodeType. 0x0002 "3DS-Version"     read-version           false)
    (NodeType. 0x3d3d "Editor"          read-nothing           true)
    (NodeType. 0x4000 "ObjectBlock"     read-name              true)
    (NodeType. 0x4100 "TriangularMesh"  read-nothing           true)
    (NodeType. 0x4110 "VertexList"      read-triangular-mesh   true)
    (NodeType. 0x4120 "FaceDescription" read-faces-description true)
    (NodeType. 0xAFFF "Material Block"  read-nothing           true)
    (NodeType. 0xB000 "KeyFramer"       read-nothing           true)
    (NodeType. 0xB008 "Frames"          read-frames            true)
    (NodeType. 0x0010 "Rgb (float)"     read-nothing           false)
    (NodeType. 0x0011 "Rgb (byte) "     read-nothing           false)
    (NodeType. 0x0012 "Rgb (byte) g"    read-nothing           false)
    (NodeType. 0x0013 "Rgb (float) g"   read-nothing           false)
    ]))

(defn read-fields [f type]
  (if type 
    ((:reader type) f)
    {}))

(defn read-node [f end]
  (let [pos     (.position f)]
    (when (< pos end)
      (let [id       (read-u16 f)
            offset   (read-u32 f)
            type     (get node-types id)
            fields   (read-fields f type)]
        (println "pos" pos "id" (format "%04x" id) "offset" offset fields)
        (let [children (if (:has-children type)
                         (read-nodes f (+ pos offset))
                         [])]
          (.position f (+ pos offset))
          (assoc fields :id id :pos pos :offset offset :children children))))))
        
(defn read-3ds [file f]
  (.rewind f)
  (.order f ByteOrder/LITTLE_ENDIAN)
  (read-node f (.length file)))

(declare print-3ds)

(defn print-3ds [indent node]
  (println (format (str indent "%04x") (:id node)) (or (:name node) ""))
  (doseq [child (:children node)]
    (print-3ds (str "  " indent) child))
  nil)

(print-3ds "" (read-3ds file f))


