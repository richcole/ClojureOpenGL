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

(defn q-to-list [^Quaternion q] [(.getX q) (.getY q) (.getZ q) (.getW q)])

(defn vx [u] (.x u)) 
(defn vy [u] (.y u)) 
(defn vz [u] (.z u))

(comment 
  (defn ldot [u v]
    (if (empty? u) 
      0
      (+ (* (first u) (first v)) 
         (ldot (rest u) (rest v)))))

  (defn lplus [u v]
    (if (empty? u) 
      []
      (cons (+ (first u) (first v))
         (lplus (rest u) (rest v)))))
    
  (defn lstimes [s u]
    (map #(* s %) u))
    
  
  (let [a (q-to-list (from-angles 0.4 0   0))
        b (q-to-list (from-angles 0   0   0))
        c (ldot a b)
        aa (ldot a a)
        bb (ldot b b)
        t  0
        angle (Math/acos c)
        sa (Math/sin angle)
        sx (/ (Math/sin (* (- 1 t) angle)) sa)
        sz (/ (Math/sin (* t angle)) sa)
        r  (lplus (lstimes sx a) (lstimes sz b))]
    [aa bb c angle sx sz r a]
  )
)

      

