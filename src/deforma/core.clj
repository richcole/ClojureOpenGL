(ns deforma.core
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL20 GL30 GL31]
           [org.lwjgl.util.glu GLU]
           [org.lwjgl BufferUtils]
           [org.lwjgl.input Keyboard Mouse]
           [com.jme3.math Quaternion Vector3f]
           java.lang.System
           )
  (:use  deforma.gl_thread
         deforma.shaders
         deforma.images
         deforma.textures
         deforma.mesh
         deforma.load3ds
         deforma.mmap
         deforma.vector
         deforma.cubes
         deforma.billboard
         )
  (:gen-class))


(defrecord State 
    [^Vector3f pos 
     ^Vector3f left 
     ^Vector3f fwd 
     ^Vector3f up 
     ^Vector3f vel 
     ^Double speed 
     ^Double mx 
     ^Double my
     ^Boolean mouse-grabbed])

(def initial-state
  (State. ZERO
          U0
          (vminus U2)
          U1 
          ZERO
          10.0
          0.0
          0.0
          false))

(defonce game-state (ref initial-state))
(defonce simple-program (ref nil))
(defonce anim-program (ref nil))
(defonce stone-texture (ref nil))
(defonce tm (ref nil))
(def tree-mesh (ref nil))
(def anim-mesh (ref nil))

(defn reset-state []
  (dosync (ref-set game-state initial-state)))

(def display-width 800)
(def display-height 800)
(def display-mode (new DisplayMode display-width display-height))

(def view-perp-angle 45.0)
(def view-aspect 1.0)
(def view-z-near 1)
(def view-z-far  100000)

(defn create-color [r g b a]
  (let [buf (BufferUtils/createFloatBuffer 4)]
    (dorun (map (fn [x] (.put buf (float x))) [r g b a]))
    (.flip buf)
    buf
    )
  )

(def gray9 (create-color 0.9 0.9 0.9 1.0))

(defn display-init [] 
  (Display/setDisplayMode display-mode)
  (Display/setVSyncEnabled true)
  (Display/create)
  )

(defn display-destroy []
  (Display/destroy)
)

(defn view-init []
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glShadeModel GL11/GL_SMOOTH)       
  (GL11/glEnable  GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glEnable GL11/GL_DEPTH_TEST)
  (GL11/glDepthFunc GL11/GL_LESS)
  (GL11/glDisable GL11/GL_CULL_FACE)
  (GL11/glFrontFace GL11/GL_CCW)
;  (GL11/glEnable GL11/GL_CULL_FACE)
;  (GL11/glFrontFace GL11/GL_CCW)
  (GL11/glDisable GL11/GL_CULL_FACE)
  (GL11/glEnable GL11/GL_LIGHTING)
  (GL11/glClearColor 0.0 0.0 0.0 0.0)
  (GL11/glClearDepth 1)

  (GL11/glPixelStorei GL11/GL_UNPACK_ALIGNMENT 1)
  (GL11/glHint GL11/GL_PERSPECTIVE_CORRECTION_HINT GL11/GL_NICEST)
  (GL11/glViewport 0 0 display-width display-height)
  (GL11/glLightModel GL11/GL_LIGHT_MODEL_AMBIENT gray9)
)

(defn view-persp [] 
  (GL11/glDisable GL11/GL_TEXTURE_2D)
  (GL11/glEnable GL12/GL_TEXTURE_3D)
  (GL11/glEnable  GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA  GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glEnable GL11/GL_DEPTH_TEST)
  (GL11/glDepthFunc GL11/GL_LEQUAL)
  (GL11/glDisable GL11/GL_CULL_FACE)
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GLU/gluPerspective view-perp-angle view-aspect view-z-near view-z-far)
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glLoadIdentity)
  )
  
(defn view-clear []
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  )

(defn look-at [^Vector3f eye ^Vector3f at ^Vector3f up]
  (GL11/glLoadIdentity)
  (GLU/gluLookAt 
   (vx eye) (vy eye) (vz eye) 
   (vx at ) (vy at ) (vz at ) 
   (vx up ) (vy up ) (vz up )))

(declare update-input-state)

(defn render-triangle []
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glBindTexture GL11/GL_TEXTURE_2D (:id @stone-texture))
  (use-program @simple-program)
  
  (GL11/glBegin GL11/GL_TRIANGLES)
  
  (GL11/glTexCoord2f 0 1)
  (GL11/glVertex3f -1.0  1.0 -10.0)
  
  (GL11/glTexCoord2f 1 1)
  (GL11/glVertex3f  1.0  1.0 -10.0)
  
  (GL11/glTexCoord2f 1 0)
  (GL11/glVertex3f  1.0 -1.0 -10.0)
  (GL11/glEnd)
)

(defn render []
  (update-input-state)
  (view-clear)

  (let [{:keys [pos fwd up]} @game-state]
    (look-at pos (vplus pos fwd) up))

  (when @simple-program (use-program @simple-program)
    (when @tm (render-mesh @tm))
    (when @tree-mesh (render-mesh @tree-mesh))
  )

  (when @anim-mesh 
    (render-anim-mesh @anim-program @anim-mesh))

  (when false
    (when (and @simple-program @anim-mesh)
      (use-program @simple-program)
      (render-mesh @anim-mesh)))

  (when false
    (render-triangle)
  )

  (Display/update)
)

(defn get-keyboard-event []
  (when (Keyboard/next)
    {:key (Keyboard/getEventKey) :down? (Keyboard/getEventKeyState)}))

(defn get-mouse-event []
  (when (Mouse/next)
    {:left-down?   (Mouse/isButtonDown 0) 
     :middle-down? (Mouse/isButtonDown 1) 
     :right-down?  (Mouse/isButtonDown 2) 
     :dx (Mouse/getEventDX)
     :dy (Mouse/getEventDY)}))

(defn process-keyboard-event [^State state event]
  (let [key (:key event)]
    (cond 
     (= key Keyboard/KEY_A) 
       (assoc state :move-left  (:down? event))
     (= key Keyboard/KEY_D) 
       (assoc state :move-right (:down? event))
     (= key Keyboard/KEY_W) 
       (assoc state :move-fwd   (:down? event))
     (= key Keyboard/KEY_S) 
       (assoc state :move-back  (:down? event))
     :else state)))

(defn process-mouse-event [^State state event]
  (if (:left-down? event)
    (assoc state 
      :mouse-grabbed true
      :mx (+ (:mx state) (:dx event))
      :my (+ (:my state) (:dy event)))
    (assoc state :mouse-grabbed false)))

(defn update-state-with-keyboard-input [^State state]
  (let [events (take-while not-nil? (repeatedly get-keyboard-event))
        next-state (reduce process-keyboard-event state events)]
    next-state))

(defn update-state-with-mouse-input [^State state]
  (let [events (take-while not-nil? (repeatedly get-mouse-event))
        next-state (reduce process-mouse-event state events)]
    next-state))

(defn update-vel-component [^State state [pred dirn]]
  (if (pred state) 
    (assoc state :vel (vplus (:vel state) dirn))
    state))

(defn update-velocity [^State state]
  (reduce update-vel-component 
          (assoc state :vel ZERO)
          [[:move-left  (:left state)]
           [:move-right (vminus (:left state))]
           [:move-fwd (:fwd state)]
           [:move-back (vminus (:fwd state))]]))

(defn update-position [^State state ^Double dt]
  (let [{:keys [pos fwd up vel speed]} state
        pos (vplus pos (svtimes (* dt speed) vel))]
    (assoc state :pos pos)))

(defn screen-scale [^Double x]
  (/ x 500.0))

(defn update-direction [^State state]
  (let [sx (- (screen-scale (:mx state)))
        sy (screen-scale (:my state))
        dirn (from-angles sy sx 0)
        fwd  (qvtimes dirn (vminus U2))
        up   (qvtimes dirn U1)
        left (vcross up fwd)]
;    (println "sx" sx "sy" sy "fwd" fwd "up" up "left" left )
    (assoc state :dirn dirn :fwd fwd :up up :left left)))

(defn update-state [^Double dt ^State state]
  (-> state 
      update-direction
      update-velocity
      (update-position dt)))

(defn update-mouse-grabbed [state]
  (if (:mouse-grabbed state)
    (when (not (Mouse/isGrabbed)) (Mouse/setGrabbed true))
    (when (Mouse/isGrabbed) (Mouse/setGrabbed false)))
  state)

(defn update-input-state []
  (dosync
      (ref-set game-state 
               (-> @game-state 
                   update-state-with-keyboard-input
                   update-state-with-mouse-input
                   update-mouse-grabbed))))

(defn tick []
  (let [dt 0.01]
;    (println "tick")
    (Thread/sleep (* 1000 dt))
    (dosync 
     (ref-set game-state (update-state dt @game-state)))))

(defn gl-load-textures []
  (gl-do
   (dosync (ref-set stone-texture (load-texture "stone_texture.jpg")))))

(defn gl-compile-shaders []
  (gl-do 
   (dosync 
    (ref-set simple-program
             (new-program-from-shader-resources 
              [["simple-vert.glsl" GL20/GL_VERTEX_SHADER]
               ["simple-frag.glsl" GL20/GL_FRAGMENT_SHADER]])))
   (dosync 
    (ref-set anim-program
             (new-program-from-shader-resources 
              [["anim-vert.glsl" GL20/GL_VERTEX_SHADER]
               ["anim-frag.glsl" GL20/GL_FRAGMENT_SHADER]]))
    )
   (let [prog-id (:id @anim-program)
         ql (GL31/glGetUniformBlockIndex prog-id "Q")
         pl (GL31/glGetUniformBlockIndex prog-id "P")
         vl (GL31/glGetUniformBlockIndex prog-id "DV")
         bl (GL31/glGetUniformBlockIndex prog-id "B")
         ]
     (GL31/glUniformBlockBinding prog-id ql 0)
     (GL31/glUniformBlockBinding prog-id pl 1)
     (GL31/glUniformBlockBinding prog-id vl 2)
     (GL31/glUniformBlockBinding prog-id bl 3)
    )))
  


(defn gl-init []
  (gl-do 
   (display-init)
   (view-init)
   (view-persp)
   (view-clear)
))

(defn -main []
  (reset-state)
  (gl-init)
  (gl-compile-shaders)
  (gl-load-textures)
  (gl-do (dosync (ref-set tm (new-triangle-mesh @stone-texture))))
  

  (future (while true (gl-do (render))))
  (future (catch-and-print-ex (while true (tick))))
)

(defn to-list [buf]
  (.rewind buf)
  (doall (map (fn [x] (.get buf)) (range 0 (.limit buf)))))


(comment  

   (-main)
   (tick)
   (Mouse/isCreated) 
   (reset-state)
   
   ref-set tm
   
   (def fb (ref nil))
   (gl-do (dosync (ref-set fb (new-frame-buffer 256 256))))
   (gl-do (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER 0))

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
             (vector3f (rand-int 10) (rand-int 10) (rand-int 10)))))
  (gl-do 
   (dosync 
    (ref-set cubes 
             (reduce add-cube @cubes (random-blocks 100)))
    (add-cube @cubes (vector3f 0 1 1))
    (ref-set tree-mesh (new-mesh (cube-mesh @cubes)))))

  (gl-do (dosync (ref-set tm (new-mesh (new-triangle-anim-node-mesh @stone-texture)))))

  (def ta (ref nil))
  (gl-do (dosync (ref-set ta (new-triangle-anim-node-mesh @stone-texture))))

  (to-list (:normals @ta))
  @tm

  (gl-do 
   (dosync 
    (ref-set tree-mesh (new-mesh (cube-mesh (terrain-map 20 20))))))

  (java.lang.System/gc)
  
  (gl-compile-shaders)
  (gl-do 
   (dosync 
    (ref-set anim-mesh (new-triangle-anim-mesh @stone-texture))))

  (to-list (:bones (new-triangle-anim-node-mesh @stone-texture)))

  (gl-do
   (when @anim-mesh (render-anim-mesh @anim-program @anim-mesh)))

  (:id (:tribuf @anim-mesh))

  (def t (terrain-map 2 2))
  (to-list (:normals t))
  (to-list (:normals (cube-mesh (terrain-map 2 2))))
  (count (:vertices (faces (terrain-map 30 30))))

  (gl-do (dosync (ref-set tree-mesh (new-mesh cm))))
  (count (to-list (:elements (node-mesh (first-node (match-name-fn "Cube") tree)))))

  (def tm (ref nil))
  (gl-do (render-mesh @tm))

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


)


 

