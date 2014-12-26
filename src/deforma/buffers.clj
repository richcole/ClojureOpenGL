(ns deforma.buffers
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL15 GL20 GL30 GL31]
           org.lwjgl.util.glu.GLU
           org.lwjgl.BufferUtils
           java.nio.FloatBuffer
           java.nio.IntBuffer
           java.nio.ShortBuffer
           com.jme3.math.Quaternion 
           com.jme3.math.Vector3f
           java.lang.System
           deforma.BufferGID)
  (:use deforma.util deforma.gid)
  (:gen-class))

(defrecord Buffer [^BufferGID id ^Integer size buf])

(defn to-list [buf]
  (.rewind buf)
  (doall (map (fn [x] (.get buf)) (range 0 (.limit buf)))))

(defn load-buffer [buf type]
  (let [id (BufferGID.)]
    (GL15/glBindBuffer type (.getGid id))
    (.rewind buf)
    (GL15/glBufferData type buf GL15/GL_STATIC_DRAW)
    (Buffer. id (.capacity buf) buf)))

(defn write-fbuf [^FloatBuffer buf vs]
  (doseq [v vs]
    (.put buf (float v)))
  (.flip buf)
  buf)
  
(defn write-sbuf [^ShortBuffer buf vs]
  (doseq [v vs]
    (.put buf (short v)))
  (.flip buf)
  buf)

(defn write-ibuf [^IntBuffer buf vs]
  (doseq [v vs]
    (.put buf (int v)))
  (.flip buf)
  buf)

(defn to-sbuf [xs]
  (let [buf (BufferUtils/createShortBuffer (count xs))]
    (write-sbuf buf xs)))

(defn to-ibuf [xs]
  (let [buf (BufferUtils/createIntBuffer (count xs))]
    (write-ibuf buf xs)))

(defn to-fbuf [xs]
  (let [buf (BufferUtils/createFloatBuffer (count xs))]
    (write-fbuf buf xs)))



