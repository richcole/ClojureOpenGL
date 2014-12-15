(ns deforma.cubes
  (:use deforma.vector)
  (:import org.lwjgl.BufferUtils)
  (:gen-class))

(defrecord Block [])

(defrecord Cubes [blocks bb])

(def cubes (ref (Cubes. {} [100 100 100])))

; 2---3/6
; |  / |
; | /  |
; 1/4--5   
(defn new-face [p d0 d1 d2]
  {:vertices 
   [(vplus p d0 (vminus d1) (vminus d2))
    (vplus p d0 (vminus d1) d2)
    (vplus p d0 d1 d2)
    (vplus p d0 (vminus d1) (vminus d2))
    (vplus p d0 d1 (vminus d2))
    (vplus p d0 d1 d2)]
   :tex-coords 
   [(vector3f 0 0 0)
    (vector3f 0 1 0)
    (vector3f 1 1 0)
    (vector3f 0 0 0)
    (vector3f 1 0 0)
    (vector3f 1 1 0)]})

(defn merge-concat [x y]
  (let [assoc-fn (fn [m [k v]] (assoc m k (concat (get m k []) v)))]
    (reduce assoc-fn x y)))

(defn has-block [cubes p]
  (not (nil? (get (:blocks cubes) p))))

(def cube-dirns 
  [[U0 U1 U2] [U1 U0 U2] [U2 U0 U1]
   [(vminus U0) U1 U2] [(vminus U1) U0 U2] [(vminus U2) U0 U1]])

(defn faces [^Cubes cubes]
  (let [bb (:bb cubes)
        reduce-faces (fn [faces [p [d0 d1 d2]]]
                       (if (= (has-block cubes p) (has-block cubes (vplus p d0)))
                         faces
                         (merge-concat faces (new-face (svtimes 2.0 p) d0 d1 d2))))
        cube-pairs (for [[p b] (:blocks cubes) dirn cube-dirns]
                     [p dirn])
;        cube-pairs (for [x (range -1 (+ (nth bb 0) 1))
;                         y (range -1 (+ (nth bb 1) 1))
;                         z (range -1 (+ (nth bb 2) 1))
;                         dirn cube-dirns]
;                     [(vector3f x y z) dirn])]
        ]
    (reduce reduce-faces {:vertices [] :tex-coords []} cube-pairs)))

(defn add-cube [cubes p]
  (assoc-in cubes [:blocks p] (Block.)))

(defn cube-mesh [cubes]
  (let [faces (faces cubes)
        num-vertices (count (:vertices faces))
        vbuf  (BufferUtils/createFloatBuffer (* 3 num-vertices))
        ebuf  (BufferUtils/createShortBuffer num-vertices)
        tbuf  (BufferUtils/createFloatBuffer (* 2 num-vertices))
        ]
    (doall (for [v (:vertices faces)]
             (do
               (.put vbuf (float (vx v)))
               (.put vbuf (float (vy v)))
               (.put vbuf (float (vz v))))))
    (doall (for [i (range 0 num-vertices)]
             (do
               (.put ebuf (short i)))))
    (doall (for [v (:tex-coords faces)]
             (do
               (.put tbuf (float (vx v)))
               (.put tbuf (float (vy v)))
             )))
    (.flip vbuf)
    (.flip ebuf)
    (.flip tbuf)
    {:vertices vbuf :elements ebuf :tex-coords tbuf :texture-filename "stone_texture.jpg"}
    ))



(has-block @cubes (vector3f 0 0 0))
(has-block @cubes (vector3f 0 0 1))





      

 
