(ns my-stuff.core
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13]
           [org.lwjgl.util.glu GLU]
           [org.lwjgl BufferUtils]
           [org.lwjgl.input Keyboard Mouse])
  (:require [clojure.core.async :as async])
  (:gen-class))

(defonce queue (new java.util.concurrent.SynchronousQueue))

(defn exec [item]
  (try (item)
    (catch Exception e 
      (println "Caught exception:" (.getMessage e)))))

(defn process-queue [queue]
  (exec (.take queue)))

(defn run [queue]
  (while true 
    (process-queue queue)))

(def orig-state 
  {:pos [0.0 0.0 0.0] 
   :fwd [0.0 0.0 -1.0] 
   :up  [0.0 1.0 0.0] 
   :vel [0.0 0.0 0.0]})

(def state (ref nil))

(defn reset-state []
  (dosync (ref-set state orig-state)))

(reset-state)

(defonce runner (future (run queue)))

(defn gl-do [f]
  (.put queue f))

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
  (Display/create)
  )

(defn display-destroy []
  (Display/destroy)
)

(defn view-init []
  (GL11/glDisable GL11/GL_TEXTURE_2D)
  (GL11/glDisable GL11/GL_TEXTURE_2D)
  (GL11/glEnable GL12/GL_TEXTURE_3D)
  (GL11/glShadeModel GL11/GL_SMOOTH)       
  (GL11/glEnable  GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glEnable GL11/GL_DEPTH_TEST)
  (GL11/glDepthFunc GL11/GL_LESS)
  (GL11/glEnable GL11/GL_CULL_FACE)
  (GL11/glFrontFace GL11/GL_CCW)
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

(defn vplus [[x1 y1 z1] [x2 y2 z2]]
  [(+ x1 x2) (+ y1 y2) (+ z1 z2)])

(defn stimes [s [x2 y2 z2]]
  [(* s x2) (* s y2) (* s z2)])

(defn look-at [x1 x2 x3 x4 x5 x6 x7 x8 x9]
  (GL11/glLoadIdentity)
  (GLU/gluLookAt x1 x2 x3 x4 x5 x6 x7 x8 x9))

(defn render []
  (view-clear)
  (let [{:keys [pos fwd up]} @state]
    (apply look-at (concat pos (vplus pos fwd) up)))
  (GL11/glBegin GL11/GL_TRIANGLES)
  (GL11/glVertex3f -1.0  1.0 -10.0)
  (GL11/glVertex3f  1.0  1.0 -10.0)
  (GL11/glVertex3f  1.0 -1.0 -10.0)
  (GL11/glEnd)
  (Display/update)
)

(defn main []
  (display-init)
  (view-init)
  (view-persp)
  (view-clear)
  )

(gl-do (fn [] (println (Keyboard/next))))

(gl-do main)
(gl-do render)

(future (while true (gl-do render)))

(defn tick [dt]
  (dosync 
   (ref-set state 
            (let [{:keys [pos fwd up vel]} @state]
              (assoc @state 
                :pos (vplus pos (stimes dt vel))
                :fwd [0.0 0.0 -1.0] 
                :up  [0.0 1.0 0.0])))))

(dosync (ref-set state (assoc @state :vel [0.5 0.5 0.0])))

(future (while true (Thread/sleep 1000) (tick 1)))

(reset-state)
(stimes 1 [1.0 1.0 1.0])
(tick 1)
(stimes 1 (:vel @state))
(println "Hello")
