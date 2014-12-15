(ns deforma.vector
  (:import [com.jme3.math Quaternion Vector3f])
  (:gen-class))

(defn vector3f [x y z]
  (Vector3f. x y z))

(defn quaternion []
  (Quaternion.))

(defn from-angles [^Double x ^Double y ^Double z]
  (let [q (quaternion)]
    (.fromAngles q x y z)))

(def ZERO (Vector3f/ZERO))
(def U0 (Vector3f/UNIT_X))
(def U1 (Vector3f/UNIT_Y))
(def U2 (Vector3f/UNIT_Z))

(defn vminus 
  ([^Vector3f v]   (.negate v))
  ([^Vector3f u ^Vector3f v] (.add u (.negate v))))

(defn vplus 
  ([^Vector3f u ^Vector3f v] (.add u v))
  ([^Vector3f u ^Vector3f v ^Vector3f w] (.add (.add u v) w))
  ([^Vector3f u ^Vector3f v ^Vector3f w ^Vector3f x] 
     (-> u (.add v) (.add w) (.add x))))

(defn vcross [^Vector3f u ^Vector3f v] (.cross u v))

(defn svtimes [^Float s ^Vector3f u] (.mult u s))

(defn qvtimes [^Quaternion q ^Vector3f u] (.mult q u))

(defn vx [u] (.x u)) 
(defn vy [u] (.y u)) 
(defn vz [u] (.z u))

