(ns deforma.mesh
  (:import [org.lwjgl.opengl GL11 GL12 GL13 GL15 GL20 GL21 GL30 GL31]
           org.lwjgl.BufferUtils
           java.nio.FloatBuffer
           java.nio.IntBuffer
           java.nio.ShortBuffer
           deforma.textures.Texture
           deforma.BufferGID
           )
  (:use deforma.textures
        deforma.shaders
        deforma.vector
        deforma.gid
        )
  (:gen-class))

(defrecord Buffer [^BufferGID id size])

(defrecord Mesh [^Buffer vbo 
                 ^Buffer tbo 
                 ^Buffer nbo 
                 ^Buffer ibo 
                 ^Texture tex 
                 ^Integer vao])

(defrecord AnimMesh [^Buffer vbo 
                     ^Buffer tbo 
                     ^Buffer nbo 
                     ^Buffer bbo 
                     ^Buffer ibo 
                     ^Buffer qbuf 
                     ^Buffer pbuf 
                     ^Buffer dvbuf
                     ^Buffer bbuf
                     ^Texture tex 
                     ^Integer vao])

(defn load-buffer [buf type]
  (let [id (BufferGID.)]
    (GL15/glBindBuffer type (.getGid id))
    (.rewind buf)
    (GL15/glBufferData type buf GL15/GL_STATIC_DRAW)
    (Buffer. id (.capacity buf))))

(defn write-fbuf [^FloatBuffer buf vs]
  (doseq [v vs]
    (.put buf (float v)))
  (.flip buf)
  buf)
  
(defn write-sbuf [^ShortBuffer buf vs]
  (doseq [v vs]
    (.put buf (short v)))
  (.flip buf)
  buf)

(defn write-ibuf [^IntBuffer buf vs]
  (doseq [v vs]
    (.put buf (int v)))
  (.flip buf)
  buf)

(defn to-sbuf [xs]
  (let [buf (BufferUtils/createShortBuffer (count xs))]
    (write-sbuf buf xs)))

(defn to-ibuf [xs]
  (let [buf (BufferUtils/createIntBuffer (count xs))]
    (write-ibuf buf xs)))

(defn to-fbuf [xs]
  (let [buf (BufferUtils/createFloatBuffer (count xs))]
    (write-fbuf buf xs)))

(defn node-mesh-get-texture [node-mesh]
  (or (:tex node-mesh) (load-texture (:texture-filename node-mesh))))

(defn new-mesh [node-mesh]
  (let [e (:elements   node-mesh)
        v (:vertices   node-mesh)
        t (:tex-coords node-mesh)
        n (:normals    node-mesh)
        tex (node-mesh-get-texture node-mesh)

        vao  (GL30/glGenVertexArrays)
        _    (GL30/glBindVertexArray vao)

        vbo  (load-buffer v GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)

        tbo  (load-buffer t GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)

        nbo  (load-buffer n GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 2 3 GL11/GL_FLOAT false 0 0)

        ibo  (load-buffer e GL15/GL_ELEMENT_ARRAY_BUFFER)
        ]
        (Mesh. vbo tbo nbo ibo tex vao)))

(defn new-triangle-node-mesh [tex]
  {:elements    (to-sbuf [0 1 2])
   :vertices   (to-fbuf [-1.0 1.0 -10.0 1.0 1.0 -10.0 1.0 -1.0 -10.0])
   :tex-coords  (to-fbuf [0 1 1 1 1 0])
   :normals     (to-fbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0])
   :tex         tex
 })

(defn new-triangle-mesh [tex]
  (new-mesh (new-triangle-node-mesh tex)))
  
(defn new-triangle-mesh-2 [tex]
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

        nbuf (BufferUtils/createFloatBuffer (* 3 3))
        nbuf (write-fbuf nbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0])
        nbo  (load-buffer nbuf GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 2 3 GL11/GL_FLOAT false 0 0)

        ibuf (BufferUtils/createShortBuffer 3)
        ibuf (write-sbuf ibuf  [0 1 2])
        ibo  (load-buffer ibuf  GL15/GL_ELEMENT_ARRAY_BUFFER)
        ]
        (Mesh. vbo tbo nbo ibo tex vao)))

(defn render-mesh [mesh]
  (GL13/glActiveTexture GL13/GL_TEXTURE0)
  (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid (:tex mesh)))

  (GL30/glBindVertexArray (:vao mesh))
  (GL20/glEnableVertexAttribArray 0)
  (GL20/glEnableVertexAttribArray 1)
  (GL20/glEnableVertexAttribArray 2)
 
  (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (gid (:ibo mesh)))
  (GL11/glDrawElements GL11/GL_TRIANGLES (:size (:ibo mesh)) GL11/GL_UNSIGNED_SHORT 0)
    
  (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
  (GL20/glDisableVertexAttribArray 0)
  (GL20/glDisableVertexAttribArray 1)
  (GL30/glBindVertexArray 0)
  )

(defn new-anim-mesh [node-mesh]
  (let [e   (:elements   node-mesh)
        v   (:vertices   node-mesh)
        t   (:tex-coords node-mesh)
        n   (:normals    node-mesh)
        bn  (:bones      node-mesh)

        q   (:q    node-mesh)
        p   (:p    node-mesh)
        dv  (:dv   node-mesh)
        b   (:b    node-mesh)

        tex (node-mesh-get-texture node-mesh)

        vao  (GL30/glGenVertexArrays)
        _    (GL30/glBindVertexArray vao)

        vbo  (load-buffer v GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)

        tbo  (load-buffer t GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)

        nbo  (load-buffer n GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 2 3 GL11/GL_FLOAT false 0 0)

        bbo  (load-buffer bn GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 3 1 GL11/GL_FLOAT false 0 0)

        ibo  (load-buffer e GL15/GL_ELEMENT_ARRAY_BUFFER)

        qbuf  (load-buffer q GL31/GL_UNIFORM_BUFFER)
        pbuf  (load-buffer p GL31/GL_UNIFORM_BUFFER)
        dvbuf (load-buffer dv GL31/GL_UNIFORM_BUFFER)
        bbuf  (load-buffer b GL31/GL_UNIFORM_BUFFER)

        ]
        (AnimMesh. vbo tbo nbo bbo ibo qbuf pbuf dvbuf bbuf tex vao)
        ))

(defn new-triangle-anim-node-mesh [tex]
  {:elements   (to-sbuf [0 1 2])
   :vertices   (to-fbuf [0.0 0.0 0.0 
                         1.0 0.0 0.0 
                         1.0 1.0 0.0])
   :tex-coords (to-fbuf [0 1 1 1 1 0])
   :normals    (to-fbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0])
   :bones      (to-fbuf [1 1 1])
   :q       (to-fbuf 
             (apply concat 
                    (map q-to-list 
                         [; -- frame 1 
                          (from-angles 0 0 0) ; bone 0
                          (from-angles 0 0 0) ; bone 1
                          ; -- frame 2
                          (from-angles 3.0 0 0) ; bone 0
                          (from-angles 3.0 0 0) ; bone 1
                          ])))  ; bone frame quat
   :p       (to-ibuf [-1 0])    ; parent pointer
   :dv      (to-fbuf [1 1 1 1 1 1])  ; displayment from parent
   :b       (to-ibuf [2 2])     ; num-bones num-frames
   :tex     tex})

(defn new-triangle-anim-mesh [tex]
  (new-anim-mesh (new-triangle-anim-node-mesh tex)))

(defn get-tick []
  (/ (mod (java.lang.System/currentTimeMillis) 5000) 5000.0))

(defn get-frame []
  (mod (int (/ (java.lang.System/currentTimeMillis) 5000)) 2))

(defn render-anim-mesh [anim-program mesh]
  (if anim-program
    (do

      (use-program anim-program)
      
      (let [prog-id (gid anim-program)
            alpha (get-tick)
            frame (get-frame)
            ]
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 0 (gid (:qbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 1 (gid (:pbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 2 (gid (:dvbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 3 (gid (:bbuf mesh)))
        (GL20/glUniform1i (GL20/glGetUniformLocation prog-id "frame") frame)
        (GL20/glUniform1f (GL20/glGetUniformLocation prog-id "alpha") alpha)
        )

      (GL13/glActiveTexture GL13/GL_TEXTURE0)
      (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid (:tex mesh)))

      (GL30/glBindVertexArray (:vao mesh))
      (GL20/glEnableVertexAttribArray 0)
      (GL20/glEnableVertexAttribArray 1)
      (GL20/glEnableVertexAttribArray 2)
      (GL20/glEnableVertexAttribArray 3)

      (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (gid (:ibo mesh)))
      (GL11/glDrawElements GL11/GL_TRIANGLES (:size (:ibo mesh)) GL11/GL_UNSIGNED_SHORT 0)
      
      (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
      (GL20/glDisableVertexAttribArray 0)
      (GL20/glDisableVertexAttribArray 1)
      (GL20/glDisableVertexAttribArray 2)
      (GL20/glDisableVertexAttribArray 3)
      (GL30/glBindVertexArray 0)
      )))

                  

        
        
    
    

