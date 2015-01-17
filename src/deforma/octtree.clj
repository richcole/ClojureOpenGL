(ns deforma.octtree
  (:use deforma.boundingbox deforma.vector)
  (:import deforma.boundingbox.BoundingBox)
  (:gen-class))

(defrecord OctTree [^BoundingBox bounds children items depth])

(defn new-octtree 
  ([bounds] (OctTree. bounds [] [] 1))
  ([bounds depth] (OctTree. bounds [] [] depth)))

(defn gen-children [bb]
  (let [du     (boundingbox-du bb)
        center (boundingbox-center bb)]
    (for [sx [1.0 -1.0]
          sy [1.0 -1.0] 
          sz [1.0 -1.0]]   
      (new-octtree (boundingbox-of
                     center
                     (vplus center 
                            (apply vplus 
                                   (map 
                                     (fn [[s u]] (svtimes s (vproject du u))) 
                                     [[sx U0] [sy U1] [sz U2]]))))))))

(defn insert [^OctTree tree ^BoundingBox bb value max-depth]
  (if (boundingbox-disjoint? (.bounds tree) bb)
    tree
    (if (>= (.depth tree) max-depth) 
      (assoc tree :items (cons (.items tree) [bb value]))
      (if (boundingbox-covers? (.bounds tree) bb)
        (assoc tree :items (cons (.items tree) [bb value]))
        (let [children (.children tree)
              children (if (empty? children) (gen-children (.bounds tree)) (children))]
          (assoc tree :children (map #(insert % bb value max-depth) children)))))))

