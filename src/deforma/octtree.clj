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

(defn octtree-insert [^OctTree tree ^BoundingBox bb value max-depth]
  (if (boundingbox-disjoint? (.bounds tree) bb)
    tree
    (if (>= (.depth tree) max-depth) 
      (assoc tree :items (cons (.items tree) [bb value]))
      (if (boundingbox-covers? bb (.bounds tree))
        (assoc tree :items (cons (.items tree) [bb value]))
        (let [children (.children tree)
              children (if (empty? children) 
                         (gen-children (.bounds tree))
                         children)
              children (map #(octtree-insert % bb value max-depth) children)]
          (assoc tree :children children))))))

(defn select-items [bb items]
  (filter (fn [[item-bb item]] (not (boundingbox-disjoint? item-bb bb))) items))

(defn octtree-find [^OctTree tree ^BoundingBox bb]
  (if (boundingbox-disjoint? bb (.bounds tree) bb)
    '()
    (let [node-items  (select-items bb (.items tree))
          children-items (reduce (fn [result child] 
                                   (concat result (find child bb))) 
                                 '() 
                                 (.children tree))]
      (concat node-items children-items))))
  

