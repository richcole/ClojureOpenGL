(ns deforma.render
  (:import [org.lwjgl.opengl GL11 GL12 GL14 GL20 GL21 GL30 GL31]
           deforma.FrameBufferGID deforma.RenderBufferGID deforma.TextureGID
           java.nio.ByteBuffer
           )
  (:use deforma.gid)
  (:gen-class))

(defprotocol Renderable 
  (render [this])
)
  
  
  
  