(ns deforma.test_utils
  (:use clojure.test deforma.util)
)

(deftest test-permutations
  (testing "Permutations"
    (is (= (* 50 49) (count (permutations (range 50) 2))))))

(deftest test-apposition
  (testing "Apposition"
    (is (= (apposition [-1 1] [:x :y])
          '(([-1 :x] [-1 :y]) 
                     ([-1 :x] [1  :y]) 
                     ([1  :x] [-1 :y]) 
                     ([1  :x] [1  :y]))
          ))))

(deftest test-selections 
  (testing "Selections"
    (is (= (selections [1 2 3])
          '([1 (2 3)] [2 (1 3)] [3 (1 2)]))))) 


(when nil
  (run-tests)
)
  
