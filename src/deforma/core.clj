(ns deforma.core
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL20 GL30 GL31]
           [org.lwjgl.util.glu GLU]
           [org.lwjgl BufferUtils]
           game.math.Quaternion 
           game.math.Vector
           java.lang.System
           org.lwjgl.BufferUtils
           )
  (:use  
    deforma.textures
    deforma.gl
    deforma.gl_thread
    deforma.shaders
    deforma.images
    deforma.mesh
    deforma.load3ds
    deforma.mmap
    deforma.vector
    deforma.cubes
    deforma.billboard
    deforma.util
    deforma.state
    deforma.input
    deforma.buffers
    deforma.render
;;    deforma.nwn
    deforma.voxel
    deforma.gid)
  (:gen-class))

(defonce renderable-map (new-renderable-map))

(defn render-objects [& rest]
  (doseq [x rest]
	  (if (and x @programs)
	    (render x @programs))))

(defn render-scene []
  (GL11/glViewport 0 0 display-width display-height)
  (update-input-state)
  (view-clear)

  (let [pos (.pos @game-state) 
       fwd (.fwd @game-state) 
       up (.up @game-state)]
   (look-at pos (vplus pos fwd) up))

  (render-objects renderable-map) 
  (Display/update)
)


(defn tick []
  (let [dt 0.01]
;    (println "tick")
    (Thread/sleep (* 1000 dt))
    (dosync 
     (ref-set game-state (update-state dt @game-state)))))


(defn -main []
  (reset-state)
  (gl-init)
  (gl-compile-shaders)
  (gl-load-textures)
  (gl-do
   (renderable-map-put renderable-map :tr1
                       (compile-mesh (new-triangle-mesh @stone-texture)))
   (renderable-map-put renderable-map :tr2
                       (compile-mesh (new-triangle-anim-mesh @stone-texture)))
   (renderable-map-put renderable-map :tr3
                       (compile-mesh (new-square-mesh @stone-texture
                                   ZERO (svtimes 1 U0) (svtimes 1 U1)))))
  (renderable-map-keys renderable-map)

  (future (while true (gl-do (render-scene))))
  (future (catch-and-print-ex (while true (tick))))
)

(comment

  (-main)

  (renderable-map-clear renderable-map)
  (let [df (new-additive-density-field
            [(new-spherical-density-field ZERO 10.0)
             (new-spherical-density-field (svtimes 6.0 U1) 10.0)])
        p0 ZERO
        dp (svtimes 8.0 U123)
        p1 (vplus p0 (svtimes -1 dp))
        p2 (vplus p0 (svtimes 1 dp))
        faces (cube-faces-over df p1 p2 1.0)
        mesh (new-mesh (mesh-from-faces faces @stone-texture))]
    (gl-do
     (renderable-map-put renderable-map :tr4
                         (compile-mesh mesh))))
  
  ;;
  (let [p (svtimes 3.0 U1)]
    (gl-do
     (renderable-map-put
      renderable-map :sp1
      (compile-mesh
       (new-square-mesh @stone-texture
                        p (svtimes 1.0 U0) (svtimes 1.0 U1))))
     (renderable-map-put
      renderable-map :sp2
      (compile-mesh
       (new-square-mesh @stone-texture
                        p (svtimes 1.0 U0) (svtimes 1.0 U2))))
     (renderable-map-put
      renderable-map :sp3
      (compile-mesh
       (new-square-mesh @stone-texture
                        p (svtimes 1.0 U1) (svtimes 1.0 U2))))
     ))
   
  
  (-main)
  (reset-state)
  
  (def m2 (bufferize-node (flatten-nodes (load-mesh-node "pmg0_shinr009") 1)))
  (def m3 (load-nwn-model "pmg0_shinr009"))

  (count (.getChildren (.getGeometry (.getGeometryHeader m3))))
  
  (gl-do (dosync (ref-set tm (new-mesh (assoc m2 :tex @stone-texture)))))
  (gl-do (dosync (ref-set tm (new-triangle-mesh @stone-texture))))
 
  (gl-do (println (mesh-cube (new-mesh (assoc m2 :tex @stone-texture)))))
  
  (vdot (.left @game-state) (.up @game-state))
  
  (vdot 
    (qvtimes (.dirn @game-state) (.fwd @game-state))
    (qvtimes (.dirn @game-state) (.up @game-state)))
  
  (take 10 (:ibo @tm)) 
   (take 10 (to-list (:buf (:ibo @tm))))
   (def fb (ref nil))
   (gl-do (dosync (ref-set fb (new-frame-buffer 1024 1024))))
   (let [mesh @tree-mesh
         render #(render-objects mesh)
         bb   (mesh-cube mesh)
         c    (cube-center bb)
         du   (cube-du bb)
         fwd  U2
         up   U1
         left  (vcross up fwd)
         dfwd  (Math/abs (vdot fwd du))
         dleft (Math/abs (vdot left du))
         dup   (Math/abs (vdot up   du))
         dfwd  (+ dfwd (max dleft dup))
         pos   (vplus c (svtimes (- dfwd) fwd))
         ]
     (println "bb" bb "pos" pos "fwd" fwd "up" up "dleft" dleft "dup" dup "dfwd" dfwd "c" c)
     (gl-do (render-framebuffer @fb pos fwd up render-scene))
   )
   1
   @game-state
   (to-list (:buf (:vbo @tm)))
   (gl-do (dosync (ref-set tm (new-square-mesh (:tb @fb) (svtimes -10 U2) (svtimes -10 U0) (svtimes 10 U1)))))
   (gl-do (dosync (ref-set tm (new-triangle-mesh @stone-texture))))
   (java.lang.System/gc) 
   
)

(comment
  (Mouse/isCreated) 
   
   (def tm1 (ref nil))
   (gl-do (dosync (ref-set tm1 (new-triangle-anim-node-mesh (:tb @fb)))))
   (gl-do (dosync (ref-set tm2 (new-mesh @tm1))))

   (def tm2 (ref nil))
   (gl-do (dosync (ref-set tm (new-mesh (box-mesh @stone-texture ZERO U0 U1 U2)))))
   
   (gl-do (view-init))

  (def tree-file (mmap-resource "dragon.3ds")) ; trees9.3ds
  (def tree (read-3ds tree-file))
  (print-3ds "" tree)

  (print-3ds "" (first-node (match-name-fn "Big_Dragon") tree))

  (nth-last 2 (first-path (match-field-fn :name "Axe") tree))

  (node-texture-filename tree "MaterialMyAx")

  (def example-texture (ref nil))

  (gl-do (dosync (ref-set example-texture 
                          (get-texture (node-texture-filename tree "MaterialMyAx")))))

  (first-node (match-field-fn :id 0x4000 :name "Axe") tree)

  (count (each-path (match-field-fn :name "Axe") tree))

  (map :name (each-node (match-field-fn :id 0x4000) tree))
  
  (count 
   (each-node 
    (match-field-fn :id 0x4130)
    (first-node 
     (match-field-fn :id 0x4000 :name "Gnarly_t")
     tree)
    ))

  (def tree-node-mesh (node-mesh tree "Gnarly_t"))
  (first-path (match-field-fn :id 0xA000 :name "MaterialMyAx") tree)

  (gl-do (dosync (ref-set tree-mesh (new-mesh tree-node-mesh))))
  (check-node-mesh tree-node-mesh)

  (to-list (:vertices (first-node (match-id-fn 0x4110) tree)))
  (count (to-list (:elements (first-node (match-id-fn 0x4120) tree))))

  (def cm (cube-mesh @cubes))
  (count (to-list (:vertices cm)))
  (count (to-list (:elements cm)))
  (count (to-list (:tex-coords cm)))

  (defn random-blocks [n]
    (doall (for [x (range 0 n)]
             (new-vector (rand-int 10) (rand-int 10) (rand-int 10)))))

  (gl-do (dosync (ref-set tm (new-mesh (new-triangle-anim-node-mesh @stone-texture)))))

  (def ta (ref nil))
  (gl-do (dosync (ref-set ta (new-triangle-anim-node-mesh @stone-texture))))

  (dosync (ref-set tm nil))

  (to-list (:normals @ta))
  @tm


  (bench (-> (terrain-map 50 50) cube-mesh))

  (let [cm  (-> (terrain-map 50 50) cube-mesh)]
    (gl-do 
     (let [tex (get-texture "stone_texture.jpg")
           tm  (-> (merge cm {:tex tex}) new-mesh compile-mesh)]
       (dosync (ref-set tree-mesh tm)))))

  (dosync (ref-set tree-mesh (new-renderable-map)))

  (def cubeland (new-renderable-map))
  (renderable-map-put renderable-map :cubeland cubeland)

  (doseq [i (range 5) j (range 5)] 
    (let [sx (* i 10)
          sy (* j 10)
          ex (+ sx 10)
          ey (+ sy 10)
          cm (-> (terrain-map sx sy ex ey) cube-mesh)]
      (gl-do 
       (let [tex (get-texture "stone_texture.jpg")
             tm  (-> (merge cm {:tex tex}) new-mesh compile-mesh)]
         (renderable-map-put cubeland [i j] tm)))))

  (let [cm (-> (terrain-map 10 10 20 20) cube-mesh)]
    (gl-do 
     (let [tex (get-texture "stone_texture.jpg")
           tm  (-> (merge cm {:tex tex}) new-mesh compile-mesh)]
       (renderable-map-put (deref tree-mesh) [1 1] tm))))

  (dosync ref-set tree-mesh nil)

  (gl-do
   (let [tex (get-texture "stone_texture.jpg")
         cm  (-> (terrain-map 10 10) (cube-mesh tex))
         tm  (-> cm new-mesh compile-mesh)]
     (println "tex" (:tex cm))))
   

  (dosync (ref-set tree-mesh nil))

  (java.lang.System/gc)
  
  (gl-compile-shaders)
  (gl-do 
   (dosync 
    (ref-set anim-mesh (new-triangle-anim-mesh @stone-texture))))

   (gl-do
     (when @anim-mesh (render @anim-program @anim-mesh)))
   
  (to-list (:bones (new-triangle-anim-node-mesh @stone-texture)))

  (:id (:tribuf @anim-mesh))

  (def t (terrain-map 2 2))
  (to-list (:normals t))
  (to-list (:normals (cube-mesh (terrain-map 2 2))))
  (count (:vertices (faces (terrain-map 30 30))))

  (gl-do (dosync (ref-set tree-mesh (new-mesh cm))))
  (count (to-list (:elements (node-mesh (first-node (match-name-fn "Cube") tree)))))

  (def tm (ref nil))
  (gl-do (render @tm @programs))

  (gl-do
   (GL11/glEnable GL11/GL_CULL_FACE)
   (GL11/glFrontFace GL11/GL_CCW)
   )

  (gl-do
   (GL11/glEnable GL11/GL_CULL_FACE)
   (GL11/glFrontFace GL11/GL_CW)
   )

  (gl-do
   (GL11/glDisable GL11/GL_CULL_FACE)
   (GL11/glFrontFace GL11/GL_CW)
   )

   (gl-do view-persp)

  (gl-do
    (load-texture "stone_texture.jpg"))

  (gl-do
	  (GL11/glMatrixMode GL11/GL_PROJECTION)
	  (GL11/glLoadIdentity)
	  (GL11/glFrustum -1 1 -1 1 1 10000) 
  )
)


 

