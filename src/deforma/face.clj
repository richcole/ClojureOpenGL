(ns deforma.face
  (:use deforma.vector)
  (:import org.lwjgl.BufferUtils)
  (:gen-class))

; 2---3/6
; |  / |
; | /  |
; 1/4--5   
(defn new-face [p d0 d1 d2]
  {:vertices 
   [(vplus p d0 (vminus d1) (vminus d2))
    (vplus p d0 (vminus d1) d2)
    (vplus p d0 d1 d2)
    (vplus p d0 (vminus d1) (vminus d2))
    (vplus p d0 d1 (vminus d2))
    (vplus p d0 d1 d2)]
   :tex-coords 
   [(new-vector 0 0 0)
    (new-vector 0 1 0)
    (new-vector 1 1 0)
    (new-vector 0 0 0)
    (new-vector 1 0 0)
    (new-vector 1 1 0)]
   :normals
   [d0 d0 d0 d0 d0 d0]
   })
