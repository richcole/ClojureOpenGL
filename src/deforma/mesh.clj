(ns deforma.mesh
  (:import [org.lwjgl.opengl GL11 GL12 GL13 GL15 GL20 GL21 GL30 GL31]
           org.lwjgl.BufferUtils
           java.nio.FloatBuffer
           java.nio.IntBuffer
           java.nio.ShortBuffer
           deforma.textures.Texture
           )
  (:use deforma.textures
        deforma.shaders)
  (:gen-class))

(defrecord Buffer [id size])

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
                     ^Buffer pbuf 
                     ^Buffer trbuf 
                     ^Buffer tribuf 
                     ^Texture tex 
                     ^Integer vao])

(defn load-buffer [buf type]
  (let [id (GL15/glGenBuffers)]
    (GL15/glBindBuffer type id)
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
  (GL11/glBindTexture GL11/GL_TEXTURE_2D (:id (:tex mesh)))

  (GL30/glBindVertexArray (:vao mesh))
  (GL20/glEnableVertexAttribArray 0)
  (GL20/glEnableVertexAttribArray 1)
  (GL20/glEnableVertexAttribArray 2)
 
  (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (:id (:ibo mesh)))
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
        b   (:bone-index node-mesh)
        p   (:parents    node-mesh)
        tr  (:tr node-mesh)
        tri (:tr-index node-mesh)
        tex (node-mesh-get-texture node-mesh)

        vao  (GL30/glGenVertexArrays)
        _    (GL30/glBindVertexArray vao)

        vbo  (load-buffer v GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)

        tbo  (load-buffer t GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)

        nbo  (load-buffer n GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 2 3 GL11/GL_FLOAT false 0 0)

        bbo  (load-buffer b GL15/GL_ARRAY_BUFFER)
        _    (GL20/glVertexAttribPointer 3 1 GL11/GL_SHORT false 0 0)

        ibo  (load-buffer e GL15/GL_ELEMENT_ARRAY_BUFFER)

        pbuf   (load-buffer p GL31/GL_UNIFORM_BUFFER)
        trbuf  (load-buffer tr GL31/GL_UNIFORM_BUFFER)
        tribuf (load-buffer tri GL31/GL_UNIFORM_BUFFER)

        ]
        (AnimMesh. vbo tbo nbo bbo ibo pbuf trbuf tribuf tex vao)
        ))

(defn new-triangle-anim-node-mesh [tex]
  {:elements   (to-sbuf [0 1 2])
   :vertices   (to-fbuf [-1.0 1.0 -9.0 1.0 1.0 -9.0 1.0 -1.0 -9.0])
   :tex-coords (to-fbuf [0 1 1 1 1 0])
   :normals    (to-fbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0])
   :bone-index (to-ibuf [0 0 0])
   :parents    (to-ibuf [-1])
   :tr         (to-fbuf [1  0  0  0 
                         0  1  0  0
                         0  0  1  0
                         1  0  0  1])
   :tr-index   (to-ibuf [0])
   :tex        tex})

(defn new-triangle-anim-mesh [tex]
  (new-anim-mesh (new-triangle-anim-node-mesh tex)))

(defn render-anim-mesh [anim-program mesh]
  (if anim-program
    (do

      (use-program anim-program)
      
      (let [prog-id (:id anim-program) 
            q-loc  (GL31/glGetUniformBlockIndex prog-id "Q")
            p-loc  (GL31/glGetUniformBlockIndex prog-id "P")
            qi-loc (GL31/glGetUniformBlockIndex prog-id "QI")]
;        (println "q-loc" q-loc "ploc" p-loc "qi-loc" qi-loc)
        (GL31/glUniformBlockBinding prog-id q-loc  0)
        (GL31/glUniformBlockBinding prog-id p-loc  1)
        (GL31/glUniformBlockBinding prog-id qi-loc 2)
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 0 (:id (:trbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 1 (:id (:pbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 2 (:id (:tribuf mesh)))
        )

      (GL13/glActiveTexture GL13/GL_TEXTURE0)
      (GL11/glBindTexture GL11/GL_TEXTURE_2D (:id (:tex mesh)))

      (GL30/glBindVertexArray (:vao mesh))
      (GL20/glEnableVertexAttribArray 0)
      (GL20/glEnableVertexAttribArray 1)
      (GL20/glEnableVertexAttribArray 2)
      (GL20/glEnableVertexAttribArray 3)

      (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (:id (:ibo mesh)))
      (GL11/glDrawElements GL11/GL_TRIANGLES (:size (:ibo mesh)) GL11/GL_UNSIGNED_SHORT 0)
      
      (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
      (GL20/glDisableVertexAttribArray 0)
      (GL20/glDisableVertexAttribArray 1)
      (GL20/glDisableVertexAttribArray 2)
      (GL20/glDisableVertexAttribArray 3)
      (GL30/glBindVertexArray 0)
      )))

                  

        
        
    
    

