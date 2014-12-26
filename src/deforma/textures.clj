(ns deforma.textures
  (:import [org.lwjgl.opengl GL11 GL20]
           javax.imageio.ImageIO
           org.lwjgl.BufferUtils 
           java.nio.IntBuffer
           java.awt.image.BufferedImage
           deforma.TextureGID)
  (:use  deforma.images
         deforma.gid)
  (:gen-class))

(defrecord Texture [^TextureGID id ^Integer width ^Integer height])

(defn new-texture [width height]
  (let [id (TextureGID. (GL11/glGenTextures))]
    (Texture. id width height)))

(defn load-texture [path]
  (let [image   (load-image path)
        width   (.getWidth image)
        height  (.getHeight image)
        buf     (get-rgba image)
        texture (new-texture width height)]

    (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid texture))

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

(def texture-assets (ref {}))

(defn get-texture [path]
  (let [texture (get @texture-assets path)]
    (if (nil? texture)
      (dosync (let [texture (load-texture path)]
                (ref-set texture-assets (assoc @texture-assets path texture))
                texture)))
      texture))









