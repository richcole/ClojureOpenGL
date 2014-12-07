(ns my-stuff.textures
  (:import [org.lwjgl.opengl GL11 GL20]
           javax.imageio.ImageIO
           org.lwjgl.BufferUtils 
           java.nio.IntBuffer
           java.awt.image.BufferedImage)
  (:use  my-stuff.images)
  (:gen-class))

(defrecord Texture [^Integer id ^Integer width ^Integer height])

(defn new-texture 
  (^Texture 
   [width height]
   (let [^IntBuffer id-buf (BufferUtils/createIntBuffer 1)]
     (GL11/glGenTextures id-buf)
     (let [id (.get id-buf 0)]
       (assert (> id 0))
       (Texture. id width height)))))

(defn load-texture [path]
  (let [image   (load-image path)
        width   (.getWidth image)
        height  (.getHeight image)
        buf     (get-rgba image)
        texture (new-texture width height)]

    (GL11/glBindTexture GL11/GL_TEXTURE_2D (:id texture))

    (GL11/glTexParameteri GL11/GL_TEXTURE_2D 
                          GL11/GL_TEXTURE_WRAP_S GL11/GL_REPEAT)

    (GL11/glTexParameteri GL11/GL_TEXTURE_2D 
                          GL11/GL_TEXTURE_WRAP_T GL11/GL_REPEAT)

    (GL11/glTexParameteri GL11/GL_TEXTURE_2D 
                          GL11/GL_TEXTURE_MAG_FILTER GL11/GL_LINEAR)

    (GL11/glTexParameteri GL11/GL_TEXTURE_2D 
                          GL11/GL_TEXTURE_MIN_FILTER GL11/GL_LINEAR)

    (GL11/glTexEnvf GL11/GL_TEXTURE_ENV 
                    GL11/GL_TEXTURE_ENV_MODE GL11/GL_MODULATE)

    (GL11/glTexImage2D GL11/GL_TEXTURE_2D 
                       0 GL11/GL_RGB 
                       width height 0 
                       GL11/GL_RGBA GL11/GL_UNSIGNED_BYTE 
                       buf)

    texture))
    
    
    
              



