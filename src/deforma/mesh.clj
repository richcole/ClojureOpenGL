(ns deforma.mesh
  (:use deforma.textures
        deforma.buffers
        deforma.shaders
        deforma.vector
        deforma.gid
        deforma.util
        deforma.boundingbox
        )
  (:import [org.lwjgl.opengl GL11 GL12 GL13 GL15 GL20 GL21 GL30 GL31]
           deforma.textures.Texture
           deforma.buffers.Buffer
           deforma.BufferGID
           game.math.Quaternion 
           game.math.Vector
           deforma.shaders.Programs
           deforma.boundingbox.BoundingBox
           )
  (:gen-class)
  )

(defprotocol Renderable
  (render [self ^Programs programs])
)

(defprotocol Compilable
  (compile-mesh [self])
)

(deftype RenderableMap [items-ref]
  Renderable
  (render [self programs]
    (doseq [[key item] (deref items-ref)] (render item programs))))

(defn new-renderable-map []
  (RenderableMap. (ref {})))

(defn renderable-map-put [rm key item]
  (dosync 
   (let [items-ref (.items-ref rm)
         items (deref items-ref)]
     (ref-set items-ref (assoc items key item)))))

(defn renderable-ref-set [ref item]
  (dosync (ref-set (.item-ref ref) item)))

(deftype Transform [^Quaternion rot ^Vector tr])

(deftype Mesh      [vertices elements tex tex-coords normals])

(deftype MeshNode  [children 
                    ^Integer bone 
                    ^Integer parent-bone 
                    ^Transform tr])

(deftype AnimPoint [tick ^Transform tr])

; fixme: vao should be a gid object
(deftype CompiledMesh [^Buffer vbo 
                       ^Buffer tbo 
                       ^Buffer nbo 
                       ^Buffer ibo 
                       ^Texture tex 
                       ^Integer vao])

(deftype CompiledAnimMesh [^Buffer vbo 
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

; deprecated
(deftype AnimMesh [elements
                   vertices
                   tex-coords
                   normals
                   bones
                   q
                   p
                   dv
                   b
                   tex])

(defn new-mesh [{:keys [vertices elements tex texture-filename 
                        tex-coords normals]}]
  (assert tex)
  (assert (and vertices elements tex tex-coords normals))
  (Mesh. vertices elements tex tex-coords normals))

(defn new-transform [{:keys [rot tr]}]
  (Transform. rot tr))

(defn new-anim-mesh 
  [{:keys [elements
           vertices
           tex-coords
           normals
           bones
           q
           p
           dv
           b
           tex]}]
  (AnimMesh. 
    elements
    vertices
    tex-coords
    normals
    bones
    q
    p
    dv
    b
    tex))

(extend-type Mesh Compilable
  (compile-mesh [self] 
    (let [vao  (GL30/glGenVertexArrays)
	        _    (GL30/glBindVertexArray vao)
	
	        vbo  (-> self .vertices to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
	        _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)
	
	        tbo  (-> self .tex-coords to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
	        _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)
	
	        nbo  (-> self .normals to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
	        _    (GL20/glVertexAttribPointer 2 3 GL11/GL_FLOAT false 0 0)
	
	        ibo  (-> self .elements to-ibuf (load-buffer GL15/GL_ELEMENT_ARRAY_BUFFER))
	        ]
	        (CompiledMesh. vbo tbo nbo ibo (.tex self) vao)))
  )

(defn new-triangle-mesh [tex]
  (new-mesh {:elements    [0 1 2]
             :vertices    [-1.0 1.0 -10.0 
                           1.0 1.0 -10.0 
                           1.0 -1.0 -10.0]
             :tex-coords  [0 1 1 1 1 0]
             :normals     [0 0 -1.0   0 0 -1.0   0 0 -1.0]
             :tex         tex}))

(defn new-square-mesh 
  ([tex]
	  (new-mesh {:elements    [0 1 2 0 3 2]
              :vertices    [-1.0  1.0  -10.0 
                            1.0  1.0  -10.0 
                            1.0 -1.0  -10.0
                            -1.0 -1.0  -10.0]
              :tex-coords  [0 1 
                            1 1 
                            1 0
                            0 0]
              :normals     [0 0 -1.0   0 0 -1.0   0 0 -1.0   0 0 -1.0]
              :tex         tex
              }))
  ([tex pos dx dy]
    (let [mdx (vminus dx)
          mdy (vminus dy)]
      (new-mesh {:elements    [0 1 2 0 3 2]
                 :vertices    (lv-to-list 
                                [(vplus pos mdx dy)
                                 (vplus pos dx  dy)
                                 (vplus pos dx  mdy)
                                 (vplus pos mdx mdy)])
                 :tex-coords [0 1 
                              1 1 
                              1 0
                              0 0]
                 :normals     (lv-to-list (repeat 4 (vcross dx dy)))
                 :tex         tex
       })))
  )

(extend-type CompiledMesh Renderable
  (render [^CompiledMesh mesh ^Programs programs]
    (use-program (.simple-program programs))
    (GL13/glActiveTexture GL13/GL_TEXTURE0)
    (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid (.tex mesh)))

    (GL30/glBindVertexArray (.vao mesh))
    (GL20/glEnableVertexAttribArray 0)
    (GL20/glEnableVertexAttribArray 1)
    (GL20/glEnableVertexAttribArray 2)
 
    (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (gid (.ibo mesh)))
    (GL11/glDrawElements GL11/GL_TRIANGLES (.size (.ibo mesh)) GL11/GL_UNSIGNED_INT 0)
    
    (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
    (GL20/glDisableVertexAttribArray 0)
    (GL20/glDisableVertexAttribArray 1)
    (GL30/glBindVertexArray 0)
    ))

(extend-type AnimMesh Compilable 
  (compile-mesh [self]
    (let [tex (.tex self)

          vao  (GL30/glGenVertexArrays)
          _    (GL30/glBindVertexArray vao)

          vbo  (-> self .vertices to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
          _    (GL20/glVertexAttribPointer 0 3 GL11/GL_FLOAT false 0 0)

          tbo  (-> self .tex-coords to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
          _    (GL20/glVertexAttribPointer 1 2 GL11/GL_FLOAT false 0 0)

          nbo  (-> self .normals to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
          _    (GL20/glVertexAttribPointer 2 3 GL11/GL_FLOAT false 0 0)

          bbo  (-> self .bones to-fbuf (load-buffer GL15/GL_ARRAY_BUFFER))
          _    (GL20/glVertexAttribPointer 3 1 GL11/GL_FLOAT false 0 0)

          ibo  (-> self .elements to-ibuf (load-buffer GL15/GL_ELEMENT_ARRAY_BUFFER))

          qbuf  (-> self .q to-fbuf  (load-buffer GL31/GL_UNIFORM_BUFFER))
          pbuf  (-> self .p to-ibuf (load-buffer GL31/GL_UNIFORM_BUFFER))
          dvbuf (-> self .dv to-fbuf (load-buffer GL31/GL_UNIFORM_BUFFER))
          bbuf  (-> self .b to-ibuf (load-buffer GL31/GL_UNIFORM_BUFFER))
          ]
          (CompiledAnimMesh. vbo tbo nbo bbo ibo qbuf pbuf dvbuf bbuf tex vao)
          )))

(defn boundingbox-reduce [[lower upper] v]
  [(if (nil? lower) v (lmin lower v)) 
   (if (nil? upper) v (lmax upper v))])

(defn mesh-boundingbox ^BoundingBox [^CompiledMesh mesh]
  (let [[lower upper] (reduce boundingbox-reduce [nil nil] (partition 3 (to-list (.buf (.vbo mesh)))))] 
	  (BoundingBox. (apply vector lower) (apply vector upper))))

(defn to-verticies [[[x1 x2] ys]]
  (let [p  (reduce (fn [p [s v]] (vplus p (svtimes s v))) ZERO ys)
        dx (fn [x] (let [[s v] (nth ys x)]
             (vplus p (svtimes (* -2 s) v))))]
    [(dx x1) p (dx x2)]))

(defn to-normals [[x y z]]
  (let [n (vcross (vminus x y) (vminus z y))]
    [n n n]))

(defn sign-to-texc [s]
  (/ (+ s 1) 2))
  
(defn to-tex-coords []
  [0 0 0 1 1 1])

(defn new-triangle-anim-mesh [tex]
  (new-anim-mesh 
    {:elements   (to-ibuf [0 1 2])
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
     :tex     tex}))

(defn get-tick []
  (/ (mod (java.lang.System/currentTimeMillis) 5000) 5000.0))

(defn get-frame []
  (mod (int (/ (java.lang.System/currentTimeMillis) 5000)) 2))

(extend-type CompiledAnimMesh Renderable
  (render [mesh ^Programs programs]
    (let [anim-program (.anim-program programs)]
      (use-program (.anim-program programs))
      (let [prog-id (gid anim-program)
            alpha (get-tick)
            frame (get-frame)
            ]
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 0 (gid (.qbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 1 (gid (.pbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 2 (gid (.dvbuf mesh)))
        (GL30/glBindBufferBase GL31/GL_UNIFORM_BUFFER 3 (gid (.bbuf mesh)))
        (GL20/glUniform1i (GL20/glGetUniformLocation prog-id "frame") frame)
        (GL20/glUniform1f (GL20/glGetUniformLocation prog-id "alpha") alpha)
        )

        (GL13/glActiveTexture GL13/GL_TEXTURE0)
        (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid (.tex mesh)))

        (GL30/glBindVertexArray (.vao mesh))
        (GL20/glEnableVertexAttribArray 0)
        (GL20/glEnableVertexAttribArray 1)
        (GL20/glEnableVertexAttribArray 2)
        (GL20/glEnableVertexAttribArray 3)

        (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (gid (.ibo mesh)))
        (GL11/glDrawElements GL11/GL_TRIANGLES (.size (.ibo mesh)) GL11/GL_UNSIGNED_INT 0)
      
        (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
        (GL20/glDisableVertexAttribArray 0)
        (GL20/glDisableVertexAttribArray 1)
        (GL20/glDisableVertexAttribArray 2)
        (GL20/glDisableVertexAttribArray 3)
        (GL30/glBindVertexArray 0)
        )))
