(ns deforma.buffers
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL15 GL20 GL30 GL31]
           org.lwjgl.util.glu.GLU
           org.lwjgl.BufferUtils
           java.nio.FloatBuffer
           java.nio.IntBuffer
           java.nio.ShortBuffer
           java.lang.System
           deforma.BufferGID)
  (:use deforma.util deforma.gid)
  (:gen-class))

(deftype Buffer [^BufferGID id ^Integer size buf]
  Gidable (gid [self] (.getGid (.id self))))

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
  (if (instance? ShortBuffer xs) xs
	  (let [buf (BufferUtils/createShortBuffer (count xs))]
	    (write-sbuf buf xs))))

(defn to-ibuf [xs]
  (if (instance? IntBuffer xs) xs
	  (let [buf (BufferUtils/createIntBuffer (count xs))]
	    (write-ibuf buf xs))))

(defn to-fbuf [xs]
  (if (instance? FloatBuffer xs) xs
	  (let [buf (BufferUtils/createFloatBuffer (count xs))]
	    (write-fbuf buf xs))))



