(ns deforma.util
  (:use clojure.java.io)
  (:gen-class))

(defn not-nil? [x] (not (nil? x)))

(defn write-to-file [path content]
  (with-open [w (output-stream path)]
    (.write w content)))

(defn choose 
  ([col k]
    (choose (reverse col) k '() '()))
  ([col k curr soln]
  (if (= k 0)
    (cons curr soln)
    (if (> k (count col))
      soln
      (let [soln (choose (rest col) k curr soln)
            curr (cons (first col) curr)]
        (choose (rest col) (- k 1) curr soln))))))

(defn product [& cols]
  (if (empty? cols) 
    '(())
    (for [x (first cols)
          y (apply product (rest cols))]
      (cons x y))))

(defn apposition [xs ys]
  (if (= 1 (count ys)) 
    (for [x xs y ys] [[x y]])
    (apply concat (for [x xs]
                    (map #(cons [x (first ys)] %) (apposition xs (rest ys)))))))

(defn at-indexes [col indexes]
  (map #(nth col %) indexes))

(defn selections 
  ([xs] (selections xs []))
  ([xs ys]
	  (if (empty? xs)
	    '()
		  (cons 
		    [(first xs) (concat (reverse ys) (rest xs))] 
		    (selections (rest xs) (cons (first xs) ys))))))

(defn permutations [xs k]
  (if (= k 0) [[]]
    (for [[y ys] (selections xs)
          zs (permutations ys (- k 1))]
      (cons y zs))))
