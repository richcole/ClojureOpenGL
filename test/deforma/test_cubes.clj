(ns deforma.test_cubes
  (:use clojure.test deforma.cubes)
)


(when nil
  (Math/floor (avg 1 2))

  (count
    (cubes-from-height-map (height-map {} 0 0 50 50 1.0 2.0 3.0 4.0) [50 50]))

	(has-block @cubes (new-vector 0 0 0))
	(has-block @cubes (new-vector 0 0 1))

 (run-tests)
)
  
