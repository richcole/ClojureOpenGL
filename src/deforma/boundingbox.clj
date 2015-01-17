(ns deforma.boundingbox
  (:use deforma.vector)
  (:require [clojure.core.typed :as typed])
  (:import game.math.Vector)
  (:gen-class))

(defrecord BoundingBox [^Vector lower ^Vector upper])
(typed/ann-datatype BoundingBox [lower :- Vector, upper :- Vector])

(defn new-boundingbox [{:keys [^Vector lower ^Vector upper]}]
  (BoundingBox. lower upper))
(typed/ann new-boundingbox [Vector Vector -> BoundingBox])

(defn boundingbox-of 
  ([^Vector u]
    (BoundingBox. u u))
  ([^Vector u ^Vector v]
    (BoundingBox. (vmin u v) (vmax u v))))
(typed/ann boundingbox-of [Vector Vector -> BoundingBox])

(defn boundingbox-grow ^BoundingBox [^BoundingBox b ^Double scale]
  (let [dx (svtimes scale (vplus U0 U1 U2))]
    (BoundingBox. (vminus (:lower b) dx) (vplus (:upper b) dx))))
(typed/ann boundingbox-grow [BoundingBox Double -> BoundingBox])

(defn boundingbox-intersection ^BoundingBox [^BoundingBox u ^BoundingBox v]
  (BoundingBox. (vmax (.lower u) (.lower v)) (vmin (.upper u) (.upper v))))
(typed/ann boundingbox-intersection [BoundingBox BoundingBox -> BoundingBox])

(defn boundingbox-disjoint? ^Boolean [^BoundingBox u ^BoundingBox v]
  (let [bb (boundingbox-intersection u v)]
    (not (vleq (.lower u) (.upper v)))))

(defn boundingbox-covers? ^Boolean [^BoundingBox u ^BoundingBox v]
  (and (vleq (.lower u) (.lower v))
       (vleq (.upper v) (.upper u))))
(typed/ann boundingbox-covers? [BoundingBox BoundingBox -> Boolean])

(defn boundingbox-center ^Vector [^BoundingBox b]
  (svtimes 0.5 (vplus (:lower b) (:upper b))))
(typed/ann boundingbox-center [BoundingBox -> Vector])

(defn boundingbox-du ^Vector [^BoundingBox b]
  (svtimes 0.5 (vminus (:upper b) (:lower b))))
(typed/ann boundingbox-grow [typed/Fn (BoundingBox) -> Vector])
