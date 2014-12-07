(ns my-stuff.shaders
  (:import [org.lwjgl.opengl GL11 GL20])
  (:use  my-stuff.gl-thread)
  (:gen-class))

; FIXME: these leak shader ids on error

(defrecord Program [^Integer id])
(defrecord Shader [^Integer id])

(defn use-program [^Program program]
  (GL20/glUseProgram (:id program)))

(defn throw-when-gl-error [get-status report]
    (when (== (get-status) GL11/GL_FALSE)
      (throw (RuntimeException. 
              (str "OpenGL Error occured: " 
                   (report (* 4 1024)))))))  

(defn attach-shader [^Program program ^Shader shader]
  (GL20/glAttachShader (:id program) (:id shader)))

(defn link-program [^Program program]
  (let [id (:id program)]
    (GL20/glLinkProgram id)
    (throw-when-gl-error #(GL20/glGetProgrami id GL20/GL_LINK_STATUS)
                         #(GL20/glGetProgramInfoLog id %))
    program))

(defn new-shader [resource-name shader-type]
  (let [id (GL20/glCreateShader shader-type)
        shader (Shader. id)]
    (assert (> id 0))
    (println "Compiling shader " resource-name shader-type)
    (GL20/glShaderSource id (slurp (clojure.java.io/resource resource-name)))
    (GL20/glCompileShader id)
    (throw-when-gl-error #(GL20/glGetShaderi id GL20/GL_COMPILE_STATUS)
                         #(GL20/glGetShaderInfoLog id %))
    shader))

(defn new-program-from-shader-resources [shaders]
  (println "shaders" shaders)
  (let [id (GL20/glCreateProgram)
        program (Program. id)]
    (assert (> id 0))
    (doseq [[resource-name type] shaders] 
      (println "resource-name" resource-name "type" type)
      (attach-shader program (new-shader resource-name type)))
    (link-program program)
    program))

                         
        
