(ns deforma.vector
  (:import game.math.Quaternion game.math.Vector)
  (:require [clojure.core.typed :as typed])
  (:use deforma.util)
  (:gen-class))

(defn new-vector [x y z]
  (Vector. x y z))

(defn quaternion []
  (Quaternion.))

(defn from-angles [^Double x ^Double y ^Double z]
  (let [q (quaternion)]
    (.fromAngles q x y z)))

(defn vminus 
  ([^Vector v]   (.minus v))
  ([^Vector u ^Vector v] (.plus u (.minus v))))

(defn vplus 
  (^Vector [^Vector u ^Vector v] (.plus u v))
  (^Vector [^Vector u ^Vector v ^Vector w] (.plus (.plus u v) w))
  (^Vector [^Vector u ^Vector v ^Vector w ^Vector x] 
     (-> u (.plus v) (.plus w) (.plus x))))

(defn vcross [^Vector u ^Vector v] (.cross u v))

(defn svtimes [^Double s ^Vector u] (.times u s))

(defn qvtimes [^Quaternion q ^Vector u] (.times q u))

(defn qtimes [^Quaternion p ^Vector q] (.times p q))

(defn sqtimes [^Double s ^Quaternion p] (.times p s))

(defn vdot [^Vector u ^Vector v] (.dot u v))

(defn vlength ^Double [^Vector u]
  (.length u))

(defn vproject [^Vector u ^Vector v]
  (svtimes (/ (vdot u v) (vlength v)) v))

(defn vnormalize [^Vector v]
  (svtimes (/ 1.0 (vlength v)) v))

(defn q-to-list [^Quaternion q] (doall (map #(.get q %) (range 4))))

(defn vx [u] (.x u)) 
(defn vy [u] (.y u)) 
(defn vz [u] (.z u))

(defn vmin ^Vector [^Vector a ^Vector b]
  (if (nil? a) b
	  (Vector. 
	    (min (vx a) (vx b))
	    (min (vy a) (vy b))
	    (min (vz a) (vz b)))))

(defn vmax ^Vector [^Vector a ^Vector b]
  (if (nil? a) b
	  (Vector. 
	    (max (vx a) (vx b))
	    (max (vy a) (vy b))
	    (max (vz a) (vz b)))))

(defn vleq ^Boolean [^Vector a ^Vector b]
  (.leq a b))

(defn vgeq ^Boolean [^Vector a ^Vector b]
  (.leq b a))

(defn vdist ^Double [^Vector a ^Vector b]
  (.length (.minus a b)))

(defn vdistsq ^Double [^Vector a ^Vector b]
  (.lengthSquared (.minus a b)))

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

(defn lmin [u v]
  (if (empty? u) 
     ()
     (cons (min (first u) (first v)) (lmin (rest u) (rest v)))))
       
(defn lmax [u v]
  (if (empty? u) 
     ()
     (cons (max (first u) (first v)) (lmax (rest u) (rest v)))))
  
(defn lstimes [s u]
  (map #(* s %) u))

(defn lv-to-list [vs]
  (apply concat (map (fn [v] [(vx v) (vy v) (vz v)]) vs)))

(defn lv-to-xy-list [vs]
  (apply concat (map (fn [v] [(vx v) (vy v)]) vs)))

(def ZERO (Vector/Z))

(def U0 (Vector/U1))

(def U1 (Vector/U2))

(def U2 (Vector/U3))

(def U123 (vplus U0 U1 U2))

(comment
  (lv-to-list [(new-vector 1.0 2.0 3.0)])
  
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
      

