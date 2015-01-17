(ns deforma.test_boundingbox
  (:use clojure.test 
        deforma.vector 
        deforma.boundingbox)
  (:import game.math.Vector)
  (:import deforma.boundingbox.BoundingBox)
  (:require [clojure.core.typed :as typed])
  (:gen-class)
)

(def B0 (new-boundingbox {:lower ZERO :upper ZERO}))
(typed/ann B0 BoundingBox)

(def B1 (new-boundingbox {:lower (vminus U0) :upper U1}))
(typed/ann B1 BoundingBox)

(deftest test-boundingbox-grow 
  (is (= ZERO (vminus ZERO)))
  (is (= (boundingbox-center B0) ZERO))
  (is (= (boundingbox-center B1) (svtimes 0.5 (vminus U1 U0))))
  (is (= (boundingbox-intersection B0 B1) B0))
)

(run-tests)
; (typed/check-ns)