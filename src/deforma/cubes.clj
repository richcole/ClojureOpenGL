(ns deforma.cubes
  (:use deforma.vector 
        deforma.face 
        deforma.mesh 
        deforma.geom 
        deforma.util
        deforma.render)
  (:import org.lwjgl.BufferUtils 
           deforma.geom.Cube 
           deforma.geom.Line
           deforma.mesh.CompiledMesh 
           )
  (:gen-class))

(deftype Block [])

(deftype Cubes [blocks])

(deftype CubeArray [^Cubes cubes ^Cube bb ^CompiledMesh mesh]
  Renderable
  (render [self programs]
    (render mesh programs)))

(defn cube-array-cubes [^CubeArray ca]
  (for [[point block] (.blocks (.cubes ca))]
    (Cube. (.minus point U123) (.plus point U123))))

(defn cube-array-line-pick [^CubeArray ca ^Line line]
  (when (cube-line-intersect? (.bb ca) line)
    (arg-min #(vdistsq (.p0 line) (cube-center %)) 
             (filter #(cube-line-intersect? % line) (cube-array-cubes ca)))))

(defn assoc-concat [m [k v]] 
  (assoc m k [(get m k []) v]))

(defn merge-concat [x y]
  (reduce assoc-concat x y))

(defn has-block [cubes p]
  (not (nil? (get (.blocks cubes) p))))

(def cube-dirns 
  [[U0 U1 U2] [U1 U0 U2] [U2 U0 U1]
   [(vminus U0) U1 U2] [(vminus U1) U0 U2] [(vminus U2) U0 U1]])

(defn join-fields [cube-pairs key]
  (apply concat (map key cube-pairs)))

(defn faces [^Cubes cubes]
  (let [cube-pairs (doall 
                    (filter #(not (nil? %))
                     (for [[p b] (.blocks cubes) [d0 d1 d2] cube-dirns]
                       (when (not (= (has-block cubes p) (has-block cubes (vplus p d0))))
                         (new-face (svtimes 2.0 p) d0 d1 d2)))))
        ]
    {:vertices (join-fields cube-pairs :vertices) 
     :tex-coords (join-fields cube-pairs :tex-coords)
     :normals (join-fields cube-pairs :normals)
     }))

(defn cube-mesh [^Cubes cubes]
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
    {:vertices vbuf :normals nbuf :elements ebuf :tex-coords tbuf}
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



(defn cubes-from-height-map [hm x1 z1 x2 z2]
  (let [solid-blocks (for [x (range x1 x2)
                           z (range z1 z2)
                           y (range 0 (get hm [x z]))]
                         (new-vector x y z))
        blocks (reduce (fn [m p] (assoc m p (Block.))) {} solid-blocks)]
    (Cubes. blocks)))

(defn terrain-map 
  ([ex ey]
     (terrain-map 0 0 ex ey))
  ([sx sy ex ey]
     (let [hm (height-map {} sx sy ex ey 2.0 2.0 2.0 2.0)]
       (cubes-from-height-map hm sx sy ex ey))))




      

 
