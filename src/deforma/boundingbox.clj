(ns deforma.boundingbox
  (:use deforma.vector)
  (:import game.math.Vector)
  (:gen-class))

(defrecord BoundingBox [^Vector lower ^Vector upper])

(defn new-boundingbox [{:keys [^Vector lower ^Vector upper]}]
  (BoundingBox. lower upper))

(defn boundingbox-of 
  ([^Vector u]
    (BoundingBox. u u))
  ([^Vector u ^Vector v]
    (BoundingBox. (vmin u v) (vmax u v))))

(defn boundingbox-grow ^BoundingBox [^BoundingBox b ^Double scale]
  (let [dx (svtimes scale (vplus U0 U1 U2))]
    (BoundingBox. (vminus (:lower b) dx) (vplus (:upper b) dx))))

(defn boundingbox-intersection ^BoundingBox [^BoundingBox u ^BoundingBox v]
  (BoundingBox. (vmax (.lower u) (.lower v)) (vmin (.upper u) (.upper v))))

(defn boundingbox-disjoint? ^Boolean [^BoundingBox u ^BoundingBox v]
  (let [bb (boundingbox-intersection u v)]
    (not (vleq (.lower bb) (.upper bb)))))

(defn boundingbox-covers? ^Boolean [^BoundingBox u ^BoundingBox v]
  (and (vleq (.lower u) (.lower v))
       (vleq (.upper v) (.upper u))))

(defn boundingbox-contains? ^Boolean [^BoundingBox u ^Vector p]
  (and (vleq (.lower u) p)
       (vleq p (.upper u))))

(defn boundingbox-center ^Vector [^BoundingBox b]
  (svtimes 0.5 (vplus (:lower b) (:upper b))))

(defn boundingbox-du ^Vector [^BoundingBox b]
  (svtimes 0.5 (vminus (:upper b) (:lower b))))
