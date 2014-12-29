(ns deforma.mesh
  (:use deforma.textures
        deforma.buffers
        deforma.shaders
        deforma.vector
        deforma.gid
        )
  (:import [org.lwjgl.opengl GL11 GL12 GL13 GL15 GL20 GL21 GL30 GL31]
           deforma.textures.Texture
           deforma.buffers.Buffer
           deforma.BufferGID
           com.jme3.math.Quaternion 
           com.jme3.math.Vector3f
           )
  (:gen-class)
  )


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
  {:elements    (to-ibuf [0 1 2])
   :vertices    (to-fbuf [-1.0 1.0 -10.0 1.0 1.0 -10.0 1.0 -1.0 -10.0])
   :tex-coords  (to-fbuf [0 1 1 1 1 0])
   :normals     (to-fbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0])
   :tex         tex
 })

(defn new-triangle-node-mesh [tex]
  {:elements    (to-ibuf [0 1 2])
   :vertices    (to-fbuf [-1.0 1.0  -10.0 
                          1.0  1.0  -10.0 
                          1.0 -1.0  -10.0
                          ])
   :tex-coords  (to-fbuf [0 1 1 1 1 0])
   :normals     (to-fbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0])
   :tex         tex
 })

(defn new-square-node-mesh 
  ([tex]
	  {:elements    (to-ibuf [0 1 2 0 3 2])
	   :vertices    (to-fbuf [-1.0  1.0  -10.0 
	                           1.0  1.0  -10.0 
	                           1.0 -1.0  -10.0
	                          -1.0 -1.0  -10.0])
	   :tex-coords  (to-fbuf [0 1 
	                          1 1 
	                          1 0
	                          0 0])
	   :normals     (to-fbuf [0 0 -1.0   0 0 -1.0   0 0 -1.0   0 0 -1.0])
	   :tex         tex
	 })
  ([tex pos dx dy]
    (let [mdx (vminus dx)
          mdy (vminus dy)]
    {:elements    (to-ibuf [0 1 2 0 3 2])
	   :vertices    (to-fbuf (lv-to-list 
                            [(vplus pos mdx dy)
                             (vplus pos dx  dy)
                             (vplus pos dx  mdy)
                             (vplus pos mdx mdy)]))
	   :tex-coords  (to-fbuf [0 1 
	                          1 1 
	                          1 0
	                          0 0])
	   :normals     (to-fbuf (lv-to-list (repeat 4 (vcross dx dy))))
	   :tex         tex
	 }))
)

(defn new-triangle-mesh [tex]
  (new-mesh (new-triangle-node-mesh tex)))

(defn new-square-mesh 
  ([tex] 
    (new-mesh (new-square-node-mesh tex)))
  ([tex pos dx dy]
    (new-mesh (new-square-node-mesh tex pos dx dy))))

(defn render-mesh [mesh]
  (GL13/glActiveTexture GL13/GL_TEXTURE0)
  (GL11/glBindTexture GL11/GL_TEXTURE_2D (gid (:tex mesh)))

  (GL30/glBindVertexArray (:vao mesh))
  (GL20/glEnableVertexAttribArray 0)
  (GL20/glEnableVertexAttribArray 1)
  (GL20/glEnableVertexAttribArray 2)
 
  (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER (gid (:ibo mesh)))
  (GL11/glDrawElements GL11/GL_TRIANGLES (:size (:ibo mesh)) GL11/GL_UNSIGNED_INT 0)
    
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

(defrecord BoundingBox [lower upper])

(defn bounding-box-reduce [[lower upper] v]
  [(if (nil? lower) v (lmin lower v)) 
   (if (nil? upper) v (lmax upper v))])

(defn mesh-bounding-box ^BoundingBox [^Mesh mesh]
  (let [[lower upper] (reduce bounding-box-reduce [nil nil] (partition 3 (to-list (:buf (:vbo mesh)))))] 
	  (BoundingBox. (apply vector3f lower) (apply vector3f upper))))

(defn bounding-box-grow ^BoundingBox [^BoundingBox b scale]
  (let [dx (svtimes scale (vplus U0 U1 U2))]
    (BoundingBox. (vminus (:lower b) dx) (vplus (:upper b) dx))))

(defn bounding-box-center ^Vector3f [^BoundingBox b]
  (svtimes 0.5 (vplus (:lower b) (:upper b))))

(defn bounding-box-du ^Vector3f [^BoundingBox b]
  (svtimes 0.5 (vminus (:upper b) (:lower b))))

(defn choose 
  ([col k]
    (choose (reverse col) k '() '()))
  ([col k curr soln]
  (if (= k 0)
    (cons curr soln)
    (if (> k (count col))
      soln
      (let [soln (choose (rest col) k curr soln)
            curr (cons (first col) curr)]
        (choose (rest col) (- k 1) curr soln))))))

(defn product [& cols]
  (if (empty? cols) 
    '(())
    (for [x (first cols)
          y (apply product (rest cols))]
      (cons x y))))

(defn apposition [xs ys]
  (if (= 1 (count ys)) 
    (for [x xs y ys] [[x y]])
    (apply concat (for [x xs]
                    (map #(cons [x (first ys)] %) (apposition xs (rest ys)))))))

(defn at-indexes [col indexes]
  (map #(nth col %) indexes))

(defn selections 
  ([xs] (selections xs []))
  ([xs ys]
	  (if (empty? xs)
	    '()
		  (cons 
		    [(first xs) (concat (reverse ys) (rest xs))] 
		    (selections (rest xs) (cons (first xs) ys))))))

(defn permutations [xs k]
  (if (= k 0) [[]]
    (for [[y ys] (selections xs)
          zs (permutations ys (- k 1))]
      (cons y zs))))
  
(permutations (range 50) 2)  

(selections [1 2 3])

(count (apposition [-1 1] [:x :y :z]))

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

(defn box-mesh [tex pos dx dy dz]
  (let [corners   (apposition [-1 1] [dx dy dz])
        tries     (product (choose (range 3) 2) corners)
        tvectors  (map to-verticies tries)
        vertices  (to-fbuf (lv-to-list (flatten tvectors)))
        elements  (to-ibuf (range (* 3 (count tvectors))))
        normals   (to-fbuf (lv-to-list (flatten (map to-normals tvectors))))
        tex-coords (to-fbuf (flatten (repeat (count tries) (to-tex-coords))))
        ]
        {:vertices vertices :normals normals :elements elements :tex-coords tex-coords :tex tex}))
         

(defn new-triangle-anim-node-mesh [tex]
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
      (GL11/glDrawElements GL11/GL_TRIANGLES (:size (:ibo mesh)) GL11/GL_UNSIGNED_INT 0)
      
      (GL15/glBindBuffer GL15/GL_ELEMENT_ARRAY_BUFFER 0)
      (GL20/glDisableVertexAttribArray 0)
      (GL20/glDisableVertexAttribArray 1)
      (GL20/glDisableVertexAttribArray 2)
      (GL20/glDisableVertexAttribArray 3)
      (GL30/glBindVertexArray 0)
      )))

                  

        
        
    
    

