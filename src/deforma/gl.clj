(ns deforma.gl
  (:use deforma.vector 
    deforma.gid 
    deforma.shaders 
    deforma.state 
    deforma.gl_thread
    deforma.textures)
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL20 GL30 GL31]
           [org.lwjgl.util.glu GLU]
           deforma.textures.Texture
           org.lwjgl.BufferUtils
           com.jme3.math.Quaternion 
           com.jme3.math.Vector3f           
    )
  (:gen-class))

(defonce simple-program (ref nil))
(defonce anim-program (ref nil))
(defonce stone-texture (ref nil))
(defonce tm (ref nil))
(defonce tree-mesh (ref nil))
(defonce anim-mesh (ref nil))


(def display-width 800)
(def display-height 800)
(def display-mode (new DisplayMode display-width display-height))

(defn create-color [r g b a]
  (let [buf (BufferUtils/createFloatBuffer 4)]
    (dorun (map (fn [x] (.put buf (float x))) [r g b a]))
    (.flip buf)
    buf
    ))

(def gray9 (create-color 0.9 0.9 0.9 1.0))

(defn view-clear []
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  )

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
  (GL11/glFrustum -1 1 -1 1 1 10000) 
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glLoadIdentity)
  )

(defn look-at [^Vector3f eye ^Vector3f at ^Vector3f up]
  (GL11/glLoadIdentity)
  (GLU/gluLookAt 
   (vx eye) (vy eye) (vz eye) 
   (vx at ) (vy at ) (vz at ) 
   (vx up ) (vy up ) (vz up )))

(defn render-triangle [^Texture tex]
  (GL11/glEnable GL11/GL_TEXTURE_2D)
  (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid tex))
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
   (let [prog-id (gid @anim-program)
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
  
