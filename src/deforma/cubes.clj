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
    (vector3f 1 1 0)]
   :normals
   [d0 d0 d0 d0 d0 d0]
   })

(defn assoc-concat [m [k v]] 
  (assoc m k [(get m k []) v]))

(defn merge-concat [x y]
  (reduce assoc-concat x y))

(defn has-block [cubes p]
  (not (nil? (get (:blocks cubes) p))))

(def cube-dirns 
  [[U0 U1 U2] [U1 U0 U2] [U2 U0 U1]
   [(vminus U0) U1 U2] [(vminus U1) U0 U2] [(vminus U2) U0 U1]])

(defn join-fields [cube-pairs key]
  (apply concat (map key cube-pairs)))

(defn faces [^Cubes cubes]
  (let [bb (:bb cubes)
        cube-pairs (doall 
                    (filter #(not (nil? %))
                     (for [[p b] (:blocks cubes) [d0 d1 d2] cube-dirns]
                       (when (not (= (has-block cubes p) (has-block cubes (vplus p d0))))
                         (new-face (svtimes 2.0 p) d0 d1 d2)))))
        ]
    {:vertices (join-fields cube-pairs :vertices) 
     :tex-coords (join-fields cube-pairs :tex-coords)
     :normals (join-fields cube-pairs :normals)
     }))

(defn add-cube [cubes p]
  (assoc-in cubes [:blocks p] (Block.)))

(defn cube-mesh [cubes]
  (let [faces (faces cubes)
        num-vertices (count (:vertices faces))
        vbuf  (BufferUtils/createFloatBuffer (* 3 num-vertices))
        nbuf  (BufferUtils/createFloatBuffer (* 3 num-vertices))
        ebuf  (BufferUtils/createIntBuffer num-vertices)
        tbuf  (BufferUtils/createFloatBuffer (* 2 num-vertices))
        ]
    (dorun (for [v (:vertices faces)]
             (do
               (.put vbuf (float (vx v)))
               (.put vbuf (float (vy v)))
               (.put vbuf (float (vz v))))))
    (dorun (for [i (range 0 num-vertices)]
             (do
               (.put ebuf (int i)))))
    (dorun (for [v (:tex-coords faces)]
             (do
               (.put tbuf (float (vx v)))
               (.put tbuf (float (vy v)))
             )))
    (dorun (for [v (:normals faces)]
             (do
               (.put nbuf (float (vx v)))
               (.put nbuf (float (vy v)))
               (.put nbuf (float (vz v)))
             )))
    (.flip vbuf)
    (.flip ebuf)
    (.flip tbuf)
    (.flip nbuf)
    {:vertices vbuf :normals nbuf :elements ebuf 
     :tex-coords tbuf :texture-filename "stone_texture.jpg"}
    ))

(defn avg [ & rest ]
  (float (/ (apply + rest) (count rest))))

;    x1  x3  x2
; y1 h1  h6  h2
; y3 h7  h5  h8
; y2 h3  h9  h4         

(defn height-map [hm x1 y1 x2 y2 h1 h2 h3 h4]
  (if (or (> x1 x2) (> y1 y2))
    hm
    (if (and (= (int x1) (int x2)) (= (int y1) (int y2)))
      (assoc hm [(int x1) (int y2)] (avg h1 h2 h3 h4))
      (let [x3 (Math/floor (avg x1 x2)) 
            x4 (+ 1 x3)
            y3 (Math/floor (avg y1 y2))
            y4 (+ 1 y3)
            h5 (+ (rand-int 5) (avg h1 h2 h3 h4))
            h6 (avg h1 h2)
            h7 (avg h1 h3)
            h8 (avg h2 h4)
            h9 (avg h3 h4)]
        (-> hm 
            (height-map x1 y1 x3 y3 h1 h6 h7 h5)
            (height-map x4 y1 x2 y3 h6 h2 h5 h8) 
            (height-map x1 y4 x3 y2 h7 h5 h2 h9)
          (height-map x4 y4 x2 y2 h5 h9 h9 h4))))))



(defn cubes-from-height-map [hm bb]
  (let [solid-blocks (for [x (range 0 (nth bb 0))
                           z (range 0 (nth bb 1))
                           y (range 0 (get hm [x z]))]
                         (vector3f x y z))
        blocks (reduce (fn [m p] (assoc m p (Block.))) {} solid-blocks)]
    (Cubes. blocks bb)))

(Math/floor (avg 1 2))

(defn terrain-map [dx dy]
  (cubes-from-height-map (height-map {} 0 0 dx dy 2.0 2.0 2.0 2.0) [dx dy]))

(count
  (cubes-from-height-map (height-map {} 0 0 50 50 1.0 2.0 3.0 4.0) [50 50]))

(has-block @cubes (vector3f 0 0 0))
(has-block @cubes (vector3f 0 0 1))





      

 
