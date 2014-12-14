(ns deforma.load3ds
  (:import [java.nio MappedByteBuffer ByteBuffer ByteOrder]
           [java.nio.channels FileChannel FileChannel$MapMode]
           [org.lwjgl BufferUtils]
           [java.io RandomAccessFile])
  (:use deforma.mmap)
  (:gen-class))

(defn read-u8 [^ByteBuffer f]
  (bit-and (.getByte f) 0xff))

(defn read-u16 [^ByteBuffer f]
  (bit-and (.getShort f) 0xffff))

(defn read-u32 [^ByteBuffer f]
  (bit-and (long (.getInt f)) 0xffffffff))

(defn read-byte [^ByteBuffer f]
  (.get f))

(defn not-zero [x]
  (not (= x 0)))

(defn read-strz [^ByteBuffer f]
  (let [s (apply str (map char 
                          (take-while not-zero (repeatedly (fn [] (.get f))))))]
    s))

(declare read-nodes)
(declare read-node)

(defn not-nil? [x] (not (nil? x)))

(defn read-nodes [f end]
  (doall (take-while not-nil? (repeatedly (fn [] (read-node f end))))))

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

(defn read-elements [f]
  (let [num-elements (read-u16 f)]
    {:num-elements num-elements 
     :elements (short-buf (read-buf f (* num-elements 4 2)))}))

(defn read-face-materials [f]
  (let [name (read-strz f)
        num-elements (read-u16 f)]
    {:name name
     :num-elements num-elements 
     :elements (short-buf (read-buf f (* num-elements 1 2)))}))

(defn read-tex-coords [f]
  (let [num-vertices (read-u16 f)]
    (println "tx pos" (.position f))
    {:num-vertices num-vertices
     :tex-coords (float-buf (read-buf f (* num-vertices 2 4)))}))

(defn read-vertices [f]
  (let [num-vertices (read-u16 f)]
    (println "vs pos" (.position f))
    {:num-vertices num-vertices 
     :vertices (float-buf (read-buf f (* num-vertices 3 4)))}))

(defn node-vertex-list [vertex-node]
  (:vertices vertex-node))

(defn node-element-list [element-node]
  (let [elements (:elements element-node)
        num-elements (:num-elements element-node)
        _ (println "num-elements" num-elements)
        buf (BufferUtils/createShortBuffer (* 3 num-elements))]
    (.rewind elements)
    (doseq [i (range 0 (.limit elements))]
      (if (= (mod i 4) 3)
        (.get elements)
        (.put buf (.get elements))))
    (.flip buf)
    (.rewind elements)
    buf))

(defn node-tex-coords [tex-coords-nodes]
  (let [num-vertices (:num-vertices tex-coords-nodes)
        uvbuf (:tex-coords tex-coords-nodes)
        rbuf (BufferUtils/createFloatBuffer (* 2 num-vertices))]
    (.rewind uvbuf)
    (doseq [i (range 0 num-vertices)]
      (let [x (.get uvbuf)
            y (.get uvbuf)]
        (.put rbuf (float (+ x)))
        (.put rbuf (float (- y)))))
    (.flip rbuf)
    (.rewind uvbuf)
    rbuf))

(defn read-version [f] 
  {:version (read-u32 f)})

(defn read-name [f]
  {:name (read-strz f)})

(defn read-filename [f]
  {:filename (read-strz f)})

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
    (NodeType. 0x4d4d "Main"              read-nothing           true)
    (NodeType. 0x0002 "3DS-Version"       read-version           false)
    (NodeType. 0x3d3d "Editor"            read-nothing           true)
    (NodeType. 0x4000 "ObjectBlock"       read-name              true)
    (NodeType. 0x4100 "TriangularMesh"    read-nothing           true)
    (NodeType. 0x4110 "VertexList"        read-vertices          false)
    (NodeType. 0x4120 "Element"           read-elements          true)
    (NodeType. 0x4130 "FaceMaterials"     read-face-materials    true)
    (NodeType. 0x4140 "TexCoords"         read-tex-coords        true)
    (NodeType. 0xAFFF "MaterialBlock"     read-nothing           true)
    (NodeType. 0xA000 "MaterialName"      read-name              false)
    (NodeType. 0xA010 "AmbientColor"      read-nothing           true)
    (NodeType. 0xA020 "DiffuseColor"      read-nothing           true)
    (NodeType. 0xA030 "SpecularColor"     read-nothing           true)
    (NodeType. 0xA040 "ShininessPercent"  read-nothing           true)
    (NodeType. 0xA040 "ShininessStrength" read-nothing           true)
    (NodeType. 0xA200 "TextureMap1"       read-nothing           true)
    (NodeType. 0xA204 "SpecularMap"       read-nothing           true)
    (NodeType. 0xA33A "TextureMap2"       read-nothing           true)
    (NodeType. 0xA300 "MappingFilename"   read-filename          true)
    (NodeType. 0xB000 "KeyFramer"         read-nothing           true)
    (NodeType. 0xB008 "Frames"            read-frames            true)
    (NodeType. 0x0010 "Rgb (float)"       read-nothing           false)
    (NodeType. 0x0011 "Rgb (byte) "       read-nothing           false)
    (NodeType. 0x0012 "Rgb (byte) g"      read-nothing           false)
    (NodeType. 0x0013 "Rgb (float) g"     read-nothing           false)
    ]))

(defn read-fields [f type]
  (if type 
    ((:reader type) f)
    {}))

(defn read-node [f end]
  (let [pos     (.position f)]
    (println "pos" pos "end" end)
    (when (< pos end)
      (let [id       (read-u16 f)
            offset   (read-u32 f)
            type     (get node-types id)
            fields   (read-fields f type)]
        (println (format "%04x" id) pos offset (+ pos offset))
        (let [children (if (:has-children type)
                         (read-nodes f (+ pos offset))
                         [])]
          (.position f (+ pos offset))
          (assoc fields :id id :type type :pos pos :offset offset :children children))))))
        
(defn read-3ds [mmap-file]
  (let [f (:buf mmap-file)
        file (:file mmap-file)]
    (.rewind f)
    (.order f ByteOrder/LITTLE_ENDIAN)
    (read-node f (.length file))))

(declare print-3ds)

(defn print-3ds [indent node]
  (println (format (str indent "%04x") (:id node)))
  (when (:name node) 
    (println (str indent " name") (:name node)))
  (when (:filename node) 
    (println (str indent " filename") (:filename node)))
  (when (:num-vertices node) 
    (println (str indent " num-vertices") (:num-vertices node)))
  (when (:num-elements node) 
    (println (str indent " num-elements") (:num-elements node)))
  (when (:vertices node) 
    (println (str indent " vertices") (.limit (:vertices node))))
  (when (:elements node) 
    (println (str indent " elements") (.limit (:elements node))))
  (doseq [child (:children node)]
    (print-3ds (str "  " indent) child))
  nil)

(defn first-node [match node]
  (if (match node)
    node
    (first (filter not-nil? (map #(first-node match %) (:children node))))))

(defn each-node [match node]
  (if (match node)
    [node]
    (apply concat (map #(each-node match %) (:children node)))))

(defn first-path [match path]
  (if (sequential? path)
    (if (match (last path))
      path
      (first (filter not-nil? (map #(first-path match (conj path %)) 
                                   (:children (last path))))))
    (first-path match [path])))

(defn each-path [match path]
  (if (sequential? path)
    (if (match (last path))
      [path]
      (apply concat (map #(each-path match (conj path %)) 
                                   (:children (last path)))))
    (each-path match [path])))

(defn match-name-fn [name]
  #(= name (:name %)))

(defn nth-last [i s]
  (first (take-last i s)))

(defn match-field-fn [ & rest ]
  (fn [record] 
    (reduce (fn [result [field value]] 
              (and result (= value (get record field)))) true (partition 2 rest))))

(defn match-id-fn [name]
  #(= name (:id %)))

(defn node-texture-filename [ root-node material-name ]
  (let [material-node    (nth-last 2 (first-path (match-field-fn :id 0xA000 :name material-name) root-node))
        texture-node     (first-node (match-field-fn :id 0xA200) material-node)
        texture-filename (:filename (first-node (match-field-fn :id 0xA300) texture-node))]
    (println "material-name" material-name)
    (println "material-node" material-node)
    (println "texture-node" texture-node)
    texture-filename))

(defn node-mesh [root-node mesh-name]
  (let [mesh-node (first-node (match-field-fn :id 0x4000 :name mesh-name) root-node)
        vertex-node (first-node (match-id-fn 0x4110) mesh-node)
        element-node (first-node (match-id-fn 0x4120) mesh-node)
        tex-coords-node (first-node (match-id-fn 0x4140) mesh-node)
        face-material-node (first-node (match-id-fn 0x4130) mesh-node)
        material-name (:name face-material-node)
        texture-filename (node-texture-filename root-node material-name)]
    (println "face-material-node" face-material-node)
    (println "texture-filename" texture-filename)
    {:vertices (node-vertex-list vertex-node)
     :elements (node-element-list element-node)
     :tex-coords (node-tex-coords tex-coords-node)
     :texture-filename texture-filename}))


(defn check-node-mesh [mesh-node]
  (let [e (:elements mesh-node)
        v (:vertices mesh-node)
        t (:tex-coords mesh-node)
        l (.limit v)]
    (.rewind e)
    (.rewind v)
    (.rewind t)
    (println (.limit v) (.limit t))
    (assert (= (* 2 (.limit v)) (* 3 (.limit t))))
    (doseq [i (range 0 (.limit e))]
      (let [index (.get e)]
      (assert (< index l))))))


(comment

  
)




