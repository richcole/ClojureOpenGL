(ns deforma.billboard
  (:import [org.lwjgl.opengl GL11 GL12 GL14 GL20 GL21 GL30 GL31]
           deforma.FrameBufferGID deforma.RenderBufferGID deforma.TextureGID
           java.nio.ByteBuffer
           )
  (:use deforma.gid)
  (:gen-class))

(defrecord Framebuffer [fb rb tb])

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

    (Framebuffer. fb rb tb)
    ))
