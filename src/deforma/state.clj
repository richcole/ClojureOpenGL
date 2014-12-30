(ns deforma.state
  (:use deforma.vector)
  (:import org.lwjgl.BufferUtils
           game.math.Quaternion 
           game.math.Vector)
  (:gen-class))

(defrecord State 
    [^Vector pos 
     ^Vector left 
     ^Vector fwd 
     ^Vector up 
     ^Vector vel 
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

