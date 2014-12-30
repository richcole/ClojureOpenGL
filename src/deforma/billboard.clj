(ns deforma.billboard
  (:use deforma.gl deforma.vector deforma.textures deforma.gid)
  (:import [org.lwjgl.opengl GL11 GL12 GL14 GL20 GL21 GL30 GL31]
           deforma.FrameBufferGID 
           deforma.RenderBufferGID 
           deforma.TextureGID
           game.math.Vector
           java.nio.ByteBuffer
           deforma.textures.Texture
           )
  (:gen-class))

(deftype Framebuffer 
  [^Integer width ^Integer height ^FrameBufferGID fb ^RenderBufferGID rb 
   ^Texture tb ^Texture tbd]
  Gidable
  (gid [self] (-> self .id .getGid)))

(defn new-framebuffer-texture [width height]
  (let [t (TextureGID.)
        ^ByteBuffer nil-buffer nil]
    (GL11/glBindTexture GL11/GL_TEXTURE_2D (.getGid t))
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_S GL11/GL_REPEAT)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_WRAP_T GL11/GL_REPEAT)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MIN_FILTER GL11/GL_NEAREST)
    (GL11/glTexParameteri GL11/GL_TEXTURE_2D GL11/GL_TEXTURE_MAG_FILTER GL11/GL_NEAREST)
    (GL11/glTexImage2D GL11/GL_TEXTURE_2D 0 GL11/GL_RGBA8 width height 0 GL12/GL_BGRA GL11/GL_UNSIGNED_BYTE nil-buffer)
    (GL30/glGenerateMipmap GL11/GL_TEXTURE_2D)
    t))

(defn new-frame-buffer [^Integer width ^Integer height]
  (let [fb (FrameBufferGID.)
        rb (RenderBufferGID.)
        tbc (new-framebuffer-texture width height)
        tbd (new-framebuffer-texture width height)
        ]
    (GL30/glBindFramebuffer GL30/GL_DRAW_FRAMEBUFFER (.getGid fb))
    (GL30/glFramebufferTexture2D GL30/GL_FRAMEBUFFER GL30/GL_COLOR_ATTACHMENT0 GL11/GL_TEXTURE_2D (.getGid tbc) 0)
    (GL30/glBindRenderbuffer GL30/GL_RENDERBUFFER (.getGid rb))
    (GL30/glRenderbufferStorage GL30/GL_RENDERBUFFER GL14/GL_DEPTH_COMPONENT24 width height);
    (GL30/glFramebufferRenderbuffer GL30/GL_FRAMEBUFFER GL30/GL_DEPTH_ATTACHMENT GL30/GL_RENDERBUFFER (.getGid rb))

    (let [result (Framebuffer. width height fb rb (Texture. tbc width height) (Texture. tbd width height))]
      (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER 0)
      result
    )))

(defn render-framebuffer [^Framebuffer fb ^Vector pos ^Vector fwd ^Vector up render-fn]
  (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER (gid fb))
  (GL11/glViewport 0 0 (.width fb) (.height fb))
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glFrustum -1 1 -1 1 1 10000) 
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glClearDepth 1.0);
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  (GL11/glLoadIdentity)
  (GL11/glEnable GL11/GL_DEPTH_TEST)
  (look-at pos (vplus pos fwd) up)
  (render-fn)
  (GL30/glBindFramebuffer GL30/GL_FRAMEBUFFER 0)
)

  
