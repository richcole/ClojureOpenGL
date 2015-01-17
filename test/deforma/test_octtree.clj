(ns deforma.test_octtree
  (:use clojure.test deforma.vector deforma.octtree deforma.boundingbox)
)

(def B0 (boundingbox-of ZERO (vplus U0 U1 U2)))
(def B1 (boundingbox-of ZERO (svtimes 100 (vplus U0 U1 U2))))
(def B2 (boundingbox-of ZERO))

(deftest test-gen-children
  (testing 
    "GenChildren"
    (let [children (gen-children B0)]
      (is (= (new-octtree 
               (boundingbox-of (new-vector 0.5 0.5 0.5) 
                               (new-vector 1.0 1.0 1.0)))
             (first children)))
      (is (= (new-octtree 
               (boundingbox-of (new-vector 0.5 0.5 0) 
                               (new-vector 1.0 1.0 0.5)))
             (second children)))
      (is (= 8
             (count children))))))

(deftest test-insert
  (testing 
    "GenChildren"
    (let [tree (insert (new-octtree B1) B2 :x 4)]
      (println tree)
      )))

(run-tests)