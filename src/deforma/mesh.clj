(ns deforma.mesh
  (:import [org.lwjgl.opengl GL11 GL12 GL13 GL15 GL20 GL30]
           org.lwjgl.BufferUtils
           java.nio.FloatBuffer
           java.nio.IntBuffer
           java.nio.ShortBuffer
           deforma.textures.Texture
           )
  (:use deforma.textures)
  (:gen-class))

(defrecord Buffer [id size])

(defrecord Mesh [^Buffer vbo ^Buffer tbo ^Buffer ido ^Texture tex ^Integer vao])

(defn load-buffer [buf type]
  (let [id (GL15/glGenBuffers)]
    (GL15/glBindBuffer type id)
    (GL15/glBufferData type buf GL15/GL_STATIC_DRAW)
    (Buffer. id (.capacity buf))))

(defn write-fbuf [^FloatBuffer buf vs]
  (doseq [v vs]
    (.put buf (float v)))
  (.flip buf)
  buf)
  
(defn write-ibuf [^ShortBuffer buf vs]
  (doseq [v vs]
    (.put buf (short v)))
  (.flip buf)
  buf)

(defn new-mesh [node-mesh]
  (let [e (:elements node-mesh)
        v (:vertices node-mesh)
        t (:tex-coords node-mesh)
        tex (load-texture (:texture-filename node-mesh))

        vao  (GL30/glGenVertexArrays)
        _    (GL30/glBindVertexArray vao)

        vbo  (load-buffer v GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)

        tbo   nil
        tbo  (load-buffer t  GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)

        ibo  (load-buffer e GL15/GL_ELEMENT_ARRAY_BUFFER)
        ]
        (Mesh. vbo tbo ibo tex vao)))
  
(defn new-triangle-mesh [tex]
  (let [vao  (GL30/glGenVertexArrays)
        _    (GL30/glBindVertexArray vao)

        vbuf (BufferUtils/createFloatBuffer (* 3 3))
        vbuf (write-fbuf vbuf [-1.0 1.0 -10.0 1.0 1.0 -10.0 1.0 -1.0 -10.0])
        vbo  (load-buffer vbuf GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)

        tbuf (BufferUtils/createFloatBuffer (* 3 2))
        tbuf (write-fbuf tbuf [0 1 1 1 1 0])
        tbo  (load-buffer tbuf  GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)

        ibuf (BufferUtils/createShortBuffer 3)
        ibuf (write-ibuf ibuf  [0 1 2])
        ibo  (load-buffer ibuf  GL15/GL_ELEMENT_ARRAY_BUFFER)
        ]
        (Mesh. vbo tbo ibo tex vao)))

(defn render-mesh [mesh]
  (GL13/glActiveTexture GL13/GL_TEXTURE0)
  (GL11/glBindTexture GL11/GL_TEXTURE_2D (:id (:tex mesh)))

  (GL30/glBindVertexArray (:vao mesh))
  (GL20/glEnableVertexAttribArray 0)
  (GL20/glEnableVertexAttribArray 1)
 
  (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (:id (:ido mesh)))
  (GL11/glDrawElements GL11/GL_TRIANGLES (:size (:ido mesh)) GL11/GL_UNSIGNED_SHORT 0)
    
  (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
  (GL20/glDisableVertexAttribArray 0)
  (GL20/glDisableVertexAttribArray 1)
  (GL30/glBindVertexArray 0)
  )

        
        
    
    

