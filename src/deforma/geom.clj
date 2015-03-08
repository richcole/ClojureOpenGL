(ns deforma.geom
  (:import game.math.Quaternion game.math.Vector)
  (:use deforma.util deforma.vector)
  (:gen-class))

(deftype Line [^Vector l0 ^Vector l1]
  ; p = l0 + alpha l1
)

(deftype Plane [^Vector n ^Double c]
  ; n . p = c
  ; p0 + alpha du + beta dv
  ; c = n.p0
)

(deftype Cube [^Vector lower ^Vector upper])

(deftype CubeFace [^Vector p0 ^Vector du ^Vector dv ^Vector n])

(defn vector-project [^Vector a ^Vector b]
  (svtimes (.dot a b) a))

(defn line-plane-intersecion [^Line line ^Plane plane]
  (let [l0 (.l0 line)
        l1 (.l1 line)
        n  (.n plane)
        c (.c plane)
        d (.dot l1 n)]
    (when (not (or (= d 0.0) (= d -0.0)))
      (let [nc (/ (- c (.dot l0 n)) d)]
        (.plus l0 (.times l1 nc))))))

(defn new-line [^Vector l0 ^Vector l1]
  (Line. l0 l1))

(defn new-plane [^Vector n ^Vector p0]
     (Plane. n (.dot n p0)))

(defn new-cube [^Vector lower ^Vector upper]
  (Cube. lower upper))

(defn cube-center ^Vector [^Cube b]
  (svtimes 0.5 (vplus (:lower b) (:upper b))))

(defn cube-du ^Vector [^Cube b]
  (svtimes 0.5 (vminus (:upper b) (:lower b))))

(defn line-point-project [^Line line ^Vector p]
  (let [l0 (.l0 line) 
        du (.minus p l0)]
    (.plus l0 (vector-project (.l1 line) du))))

(defn cube-grow ^Cube [^Cube b ^Double scale]
  (let [dx (svtimes scale (vplus U0 U1 U2))]
    (Cube. (vminus (.lower b) dx) (vplus (.upper b) dx))))

(defn cube-intersection ^Cube [^Cube u ^Cube v]
  (Cube. (vmax (.lower u) (.lower v)) (vmin (.upper u) (.upper v))))

(defn cube-disjoint? ^Boolean [^Cube u ^Cube v]
  (let [bb (cube-intersection u v)]
    (not (vleq (.lower bb) (.upper bb)))))

(defn cube-covers? ^Boolean [^Cube u ^Cube v]
  (and (vleq (.lower u) (.lower v))
       (vleq (.upper v) (.upper u))))

(defn cube-contains? ^Boolean [^Cube u ^Vector p]
  (and (vleq (.lower u) p)
       (vleq p (.upper u))))

(defn point-on-face? [^Vector p ^CubeFace face]
  (let [duv (.plus (.du face) (.dv face))
        c   (.p0 face)
        bb  (new-cube (.minus c duv) (.plus c duv))]
    (cube-contains? bb p)))

(defn new-cube-face [^Vector p0 ^Vector du ^Vector dv]
  (CubeFace. p0 du dv (.cross du dv)))

(defn cube-faces [^Cube cube]
  (let [du (cube-du cube)
        p0 (cube-center cube)]
    (for [[du dv] (permutations (map #(vector-project % du) [U0 U1 U2]) 2)]
      (let [n (vector-project (.cross du dv) du)]
        (new-cube-face (vplus p0 n) du dv)))))

(defn cube-line-intersect? [^Cube cube ^Line line]
  (let [p (line-point-project line (cube-center cube))]
    (cube-contains? p)))

(comment 
  (cube-line-intersect? 
   (new-cube ZERO (vplus U0 U1 U2))
   (new-line (svtimes 10 U0) (vplus U0 U1 U2)))
)
