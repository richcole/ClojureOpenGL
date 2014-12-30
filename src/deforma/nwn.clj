(ns deforma.nwn
  (:use 
    clj-audio.core 
    clojure.java.io 
    deforma.vector
    deforma.mesh
    deforma.gl_thread
    deforma.buffers)
  (:import   
    game.Context 
    game.nwn.readers.ResourceType
    game.nwn.NwnMesh
    java.io.ByteArrayInputStream
    ; javax.sound.sampled.AudioSystem
    ; javax.sound.sampled.AudioFileFormat$Type
    javazoom.jl.player.Player
     
    ;javafx.scene.media.Media
    ;javafx.scene.media.MediaPlayer
    )
  (:gen-class))

(defonce context (Context.))
(defonce key-reader (.getKeyReader context))
(defonce key-index  (.getKeyIndex key-reader))

(defrecord Sound [bytes]) 

(defn play-sound [^Sound sound]
  (-> (ByteArrayInputStream. (:bytes sound)) Player. .play))

(defn load-sound [res-name]
	(let [res (.getResource key-reader res-name ResourceType/WAV)
	      offset (.getOffset res)
	      length (.getLength res)
	      hdr   (-> res .getReader .getInp (.readBytes offset 8))
	      bytes (-> res .getReader .getInp (.readBytes (+ offset 8) (- length 8)))
	      ]
   (assert (= "BMU V1.0" (String. hdr)))
   (Sound. bytes)))


(defn get-vertices-from-faces [faces]
  (flatten 
    (for [face faces vertex (.getVertices face)]
      [(* 1000 (.x vertex)) (* 1000 (.y vertex)) (* 1000 (.z vertex))])))

(defn get-normals-from-faces [faces]
  (flatten 
    (for [face faces i (range 3)]
      (let [normal (.getNormal face)]
        [(.x normal) (.y normal) (.z normal)]))))

(defn get-tex-coords-from-faces [faces]
  (flatten 
    (for [face faces v (.getTexturePoints face)]
      [(.x v) (.y v)])))


(defn convert-node [node]
  (let [faces (.getFaces node)
        vertices (get-vertices-from-faces faces)]
	  {:name       (.getName node) 
	   :children   (map convert-node (.getChildren node))
	   :vertices   vertices
	   :normals    (get-normals-from-faces faces)
	   :tex-coords (get-tex-coords-from-faces faces)
	   :position   (.getPosition node)
	   :rotation   (.getRotation node)
	   :elements   (range 0 (count vertices))
	   }))

(declare flatten-nodes)

(defn flatten-node [parent-index r n]
  (println "flatten-node" parent-index)
  (let [node-index (+ (:max-index r) 1)
        rn (flatten-nodes n node-index)
        nv (count (:verticies r))
        ]
    {:vertices   (concat (:vertices r) (:vertices rn))
     :normals    (concat (:normals r)  (:normals rn))
     :tex-coords (concat (:tex-coords r) (:tex-coords rn))
     :elements   (concat (:elements r) (map #(+ nv %) (:elements rn)))
     :bones      (concat (:bones r) (:bones rn))
     :max-index  (max node-index (:max-index rn))
     :parents    (assoc (:parents r {}) node-index parent-index)}))

(defn flatten-nodes [node node-index]
  (let [bones (repeat (count (:vertices node)) node-index)]
    (println "bones" (count bones))
    (doall (reduce (fn [r n] (flatten-node node-index r n)) 
             (assoc node :bones bones :max-index node-index) 
             (:children node)))))

(defn load-mesh-node [res-name]
  (let [model-reader (.getMdlReader key-reader res-name)
        mesh (NwnMesh. context (.readModel model-reader))
        anim-mesh (.getAnimMesh mesh)]
    (convert-node (.getRoot anim-mesh))))

(defn load-nwn-model [res-name]
  (let [model-reader (.getMdlReader key-reader res-name)]
        (.readModel model-reader)))

(defn bufferize-node [mesh]
  (assoc mesh 
    :vertices (to-fbuf (:vertices mesh))
    :elements (to-ibuf (:elements mesh))
    :bones (to-ibuf (:bones mesh))
    :tex-coords (to-fbuf (:tex-coords mesh))
    :normals (to-fbuf (:normals mesh))))


(when nil
	(def m1 (bufferize-node (flatten-nodes (load-mesh-node "pmg0_shinr009") 0)))
	(count (:elements m1))
	
	(count (:elements (first (:children (load-mesh-node "pmg0_shinr009")))))
	
	(take 10
	  (for [resource (.values key-index)]
	    (let [entry (.getEntry resource) 
	          type (ResourceType/getType (.getType entry))]
	      { (.getName entry) type })))

 (play-sound (load-sound "vs_nwncomm4_haha"))
)
