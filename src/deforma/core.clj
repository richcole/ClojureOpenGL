(ns deforma.core
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL20]
           [org.lwjgl.util.glu GLU]
           [org.lwjgl BufferUtils]
           [org.lwjgl.input Keyboard Mouse]
           [com.jme3.math Quaternion Vector3f])
  (:use  deforma.gl_thread
         deforma.shaders
         deforma.images
         deforma.textures
         deforma.mesh
         deforma.load3ds
         deforma.mmap
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
     ^Double my])

(defn vector3f [x y z]
  (Vector3f. x y z))

(def ZERO (Vector3f/ZERO))
(def U0 (Vector3f/UNIT_X))
(def U1 (Vector3f/UNIT_Y))
(def U2 (Vector3f/UNIT_Z))

(defn vminus 
  ([^Vector3f v]   (.negate v))
  ([^Vector3f u ^Vector3f v] (.add u (.negate v))))

(defn vplus [^Vector3f u ^Vector3f v] (.add u v))

(defn vcross [^Vector3f u ^Vector3f v] (.cross u v))

(defn svtimes [^Float s ^Vector3f u] (.mult u s))

(defn qvtimes [^Quaternion q ^Vector3f u] (.mult q u))

(defn vx [u] (.x u)) 
(defn vy [u] (.y u)) 
(defn vz [u] (.z u))

(def initial-state
  (State. ZERO
          U0
          (vminus U2)
          U1 
          ZERO
          10.0
          0.0
          0.0))

(defonce game-state (ref initial-state))
(defonce simple-program (ref nil))
(defonce stone-texture (ref nil))
(defonce tm (ref nil))
(def tree-mesh (ref nil))

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

(defn render []
  (view-clear)

  (let [{:keys [pos fwd up]} @game-state]
    (look-at pos (vplus pos fwd) up))

  (when @simple-program (use-program @simple-program))
  (when @tm (render-mesh @tm))
  (when @tree-mesh (render-mesh @tree-mesh))

  (when false
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
    (when (not (Mouse/isGrabbed)) (Mouse/setGrabbed true))
    (when (Mouse/isGrabbed) (Mouse/setGrabbed false)))
  (if (:left-down? event)
    (assoc state 
      :mx (+ (:mx state) (:dx event))
      :my (+ (:my state) (:dy event)))
    state))

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

(defn from-angles [^Double x ^Double y ^Double z]
  (let [q (Quaternion.)]
    (.fromAngles q x y z)))

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
      update-state-with-keyboard-input
      update-state-with-mouse-input
      update-direction
      update-velocity
      (update-position dt)))

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
               ["simple-frag.glsl" GL20/GL_FRAGMENT_SHADER]])))))

(defn gl-init []
  (gl-do 
   (display-init)
   (view-init)
   (view-persp)
   (view-clear)
))

(gl-do (view-init))

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

  (def tree-file (mmap-resource "u1.3ds")) ; trees9.3ds
  (def tree (read-3ds tree-file))
  (print-3ds "" tree)

  (print-3ds "" (first-node (match-name-fn "Axe") tree))

  (def tree-node-mesh (node-mesh (first-node (match-name-fn "Axe") tree)))

  (gl-do (dosync (ref-set tree-mesh (new-mesh tree-node-mesh @stone-texture))))
  (check-node-mesh tree-node-mesh)

  (to-list (:vertices (first-node (match-id-fn 0x4110) tree)))
  (count (to-list (:elements (first-node (match-id-fn 0x4120) tree))))

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
)


 

