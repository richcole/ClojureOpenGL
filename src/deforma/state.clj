(ns deforma.state
  (:use deforma.vector)
  (:import org.lwjgl.BufferUtils
           [com.jme3.math Quaternion Vector3f])
  (:gen-class))

(defrecord State 
    [^Vector3f pos 
     ^Vector3f left 
     ^Vector3f fwd 
     ^Vector3f up 
     ^Vector3f vel 
     ^Double speed 
     ^Double mx 
     ^Double my
     ^Boolean mouse-grabbed])

(def initial-state
  (State. ZERO
          U0
          (vminus U2)
          U1 
          ZERO
          10.0
          0.0
          0.0
          false))

(defonce game-state (ref initial-state))

(defn reset-state []
  (dosync (ref-set game-state initial-state)))

