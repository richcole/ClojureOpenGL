(ns deforma.billboard
  (:use deforma.gid deforma.gl deforma.vector deforma.textures)
  (:import [org.lwjgl.opengl GL11 GL12 GL14 GL20 GL21 GL30 GL31]
           deforma.FrameBufferGID deforma.RenderBufferGID deforma.TextureGID
           java.nio.ByteBuffer
           deforma.textures.Texture
           )
  (:gen-class))

(defrecord Framebuffer [width height fb rb tb])

(defn new-frame-buffer [^Integer width ^Integer height]
  (let [fb (FrameBufferGID.)
        rb (RenderBufferGID.)
        tb (TextureGID.)
        ^ByteBuffer nil-buffer nil]
    (GL11/glBindTexture GL11/GL_TEXTURE_2D (.getGid tb))
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_S GL11/GL_REPEAT)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_T GL11/GL_REPEAT)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_NEAREST)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_NEAREST)
    (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RGBA8 width height 0 GL12/GL_BGRA GL11/GL_UNSIGNED_BYTE nil-buffer)
    (GL30/glGenerateMipmap GL11/GL_TEXTURE_2D)

    (GL30/glBindFramebuffer GL30/GL_DRAW_FRAMEBUFFER (.getGid fb))
    (GL30/glFramebufferTexture2D GL30/GL_FRAMEBUFFER GL30/GL_COLOR_ATTACHMENT0 GL11/GL_TEXTURE_2D (.getGid tb) 0)
    (GL30/glBindRenderbuffer GL30/GL_RENDERBUFFER (.getGid rb))
    (GL30/glRenderbufferStorage GL30/GL_RENDERBUFFER GL14/GL_DEPTH_COMPONENT24 width height);

    (let [result (Framebuffer. width height fb rb (Texture. tb width height))]
      (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER 0)
      result
    )))

(defn render-framebuffer [^Framebuffer fb pos fwd up render-fn]
  (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER (.getGid (:fb fb)))
  (GL11/glViewport 0 0 (:width fb) (:height fb))
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glFrustum -1 1 -1 1 1 10000) 
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glLoadIdentity)
  (look-at pos (vplus pos fwd) up)
  (render-fn)
  (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER 0)
)
  
  
