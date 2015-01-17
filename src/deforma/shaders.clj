(ns deforma.shaders
  (:use deforma.gid)
  (:import [org.lwjgl.opengl GL11 GL20 GL31]
           deforma.ProgramGID deforma.ShaderGID)
  (:gen-class))

(defprotocol UsableProgram
  (use-program [self]))

(deftype Program [^ProgramGID id] 
  Gidable 
  (gid [self] (-> self .id .getGid))
)

(deftype Shader [^ShaderGID id]
  Gidable 
  (gid [self] (-> self .id .getGid))
)

(deftype Programs [simple-program anim-program])

(extend-type Program UsableProgram 
  (use-program [program]
    (GL20/glUseProgram (gid program))))

(defn throw-when-gl-error [resource get-status report]
    (when (== (get-status) GL11/GL_FALSE)
      (throw (RuntimeException. 
              (str "OpenGL Error occured (" resource ": " 
                   (report (* 4 1024)))))))  

(defn attach-shader [^Program program ^Shader shader]
  (GL20/glAttachShader (gid program) (gid shader)))

(defn link-program [^Program program]
  (let [id (gid program)]
    (GL20/glLinkProgram id)
    (throw-when-gl-error "program" #(GL20/glGetProgrami id GL20/GL_LINK_STATUS)
                         #(GL20/glGetProgramInfoLog id %))
    program))

(defn new-shader [resource-name shader-type]
  (let [id (GL20/glCreateShader shader-type)
        shader (Shader. (ShaderGID. id))]
    (assert (> id 0))
    (GL20/glShaderSource id (slurp (clojure.java.io/resource resource-name)))
    (GL20/glCompileShader id)
    (throw-when-gl-error resource-name #(GL20/glGetShaderi id GL20/GL_COMPILE_STATUS)
                         #(GL20/glGetShaderInfoLog id %))
    shader))

(defn new-program-from-shader-resources [shaders]
  (let [id (GL20/glCreateProgram)
        program (Program. (ProgramGID. id))]
    (assert (> id 0))
    (doseq [[resource-name type] shaders] 
      (attach-shader program (new-shader resource-name type)))
    (link-program program)
    program))

(defn new-programs []
  (let [programs (Programs. 
	                  (new-program-from-shader-resources 
                     [["simple-vert.glsl" GL20/GL_VERTEX_SHADER]
                      ["simple-frag.glsl" GL20/GL_FRAGMENT_SHADER]])
                   (new-program-from-shader-resources 
                     [["anim-vert.glsl" GL20/GL_VERTEX_SHADER]
                      ["anim-frag.glsl" GL20/GL_FRAGMENT_SHADER]]))
        prog-id (-> programs .anim-program gid)
        ql (GL31/glGetUniformBlockIndex prog-id "Q")
        pl (GL31/glGetUniformBlockIndex prog-id "P")
        vl (GL31/glGetUniformBlockIndex prog-id "DV")
        bl (GL31/glGetUniformBlockIndex prog-id "B")]
     (GL31/glUniformBlockBinding prog-id ql 0)
     (GL31/glUniformBlockBinding prog-id pl 1)
     (GL31/glUniformBlockBinding prog-id vl 2)
     (GL31/glUniformBlockBinding prog-id bl 3)
     programs))

                         
        
