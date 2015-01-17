(ns deforma.creatures
  (:use deforma.vector)
  (:import game.math.Vector)
  (:gen-class))

(defn vleq [^Vector x ^Vector y]
  (and (<= (vx x) (vx y)) (<= (vy x) (vy y)) (<= (vz x) (vz y))))

(defn vrange [^Vector lower ^Vector upper ^Vector dx]
  (if (vleq lower upper)
    (cons lower (vrange (vplus lower dx) upper dx))
    '()))

(defn vproject [x y]
  (svtimes (vdot x y) y))

(defrecord CreatureType [^String name ^String display-char ^Integer hp attack defense])
(defrecord Creature [^Vector pos ^Integer hp ^CreatureType type ^Integer id])
(defrecord ItemType [^String name ^Boolean eadable])
(defrecord Item [^ItemType type])
(defrecord Square [^Creature creature ^Boolean is-wall items]) 
(defrecord Map [squares ^Vector lower ^Vector upper creatures ^Integer max-creature-id])

(defn new-item-type [{:keys [name eadable]}]
  (ItemType. name eadable))

(defn new-item [{:keys [type]}]
  (Item. type))

(defn new-map ^Map [{:keys [lower upper creatures squares max-creature-id]}]
  (let [squares (or squares {})
        creatures (or creatures {})
        max-creature-id (or max-creature-id 0)]
    (Map. squares lower upper creatures max-creature-id)))

(defn new-square ^Square [{:keys [creature is-wall items]}]
  (let [items (or items [])]
    (Square. creature is-wall items)))

(defn new-creature [{:keys [type pos hp id]}]
  (let [hp (or hp (.hp type))]
    (Creature. pos hp type id)))

(defn new-creature-type [{:keys [name display-char hp attack defense]}]
  (CreatureType. name display-char hp attack defense))

(defn update-creature [^Map a-map ^Creature src-creature ^Creature dst-creature]
  (let [src (.pos src-creature)
        dst (.pos dst-creature) 
        creatures (:creatures a-map)
        creatures (assoc creatures (.id dst-creature) dst-creature)
        squares (:squares a-map)
        squares (assoc squares dst (assoc (square-at a-map dst) :creature dst-creature))
        squares (assoc squares src (assoc (square-at a-map src) :creature nil))]
    (assoc a-map :creatures creatures :squares squares)))

(defn square-at ^Square [^Map map ^Vector pos]
  (or (get-in map [:squares pos]) (new-square {})))

(defn wall-at ^Square [^Map map ^Vector pos]
  (:is-wall (or (get-in map [:squares pos]) (new-square {}))))

(defn draw-wall ^Map [^Map map ^Vector pos]
  (let [square (square-at map pos)]
    (assoc-in map [:squares pos] (assoc square :is-wall true))))

(defn draw-line-wall ^Map [^Map map ^Vector lower ^Vector upper ^Vector dx]
  (reduce draw-wall map (vrange lower upper dx)))

(defn draw-square-wall ^Map [^Map map ^Vector lower ^Vector upper]
  (-> map 
    (draw-line-wall lower upper U0)
    (draw-line-wall lower upper U1)
    (draw-line-wall (vplus lower (vproject upper U0)) upper U1)
    (draw-line-wall (vplus lower (vproject upper U1)) upper U0))
)

(defn print-square [map pos]
  (let [square (square-at map pos)
        creature (.creature square)]
    (if (nil? creature)
      (if (.is-wall square) "#" ".")
      (-> creature .type .display-char))))
      
(defn print-map [^Map a-map] 
  (doall 
    (let [lower (.lower a-map) upper (.upper a-map)] 
	    (for [x (vrange (vproject lower U0) (vproject upper U0) U0)]
	      (let [ys (vrange (vproject lower U1) (vproject upper U1) U1)]
	        (println 
	          (clojure.string/join (map #(print-square a-map (vplus x %)) ys))))))))      

(defn creature-at ^Creature [^Map map ^Vector pos]
  (let [square (get-in map [:squares pos])]
    (when square (.creature square))))

(defn spawn ^Map [^Map map ^CreatureType type ^Vector pos]
  (if (nil? (creature-at map pos))
    (let [creature (new-creature {:type type 
                                  :pos pos 
                                  :id (+ 1 (.max-creature-id map))})
          square (assoc (square-at map pos) :creature creature)]
      (let [result (-> map 
                     (assoc-in [:squares pos] square)
                     (assoc-in [:max-creature-id] (.id creature))
                     (assoc-in [:creatures] (assoc (.creatures map) (.id creature) creature)))]
        (println (square-at result pos))
        result)
    )
    map))

(defn square [u]
  (* u u))

(defn vlength-squared [u]
  (+ (square (vx u)) (square (vy u)) (square (vz u))))

(defn cmp-dist [dst x y]
  (- 
	  (vlength-squared (vminus dst x))
    (vlength-squared (vminus dst y))))

(defn subsets [xs]
  (if (empty? xs)
    '(())
	  (concat 
	    (map #(cons (first xs) %) (subsets (rest xs)))
	    (subsets (rest xs)))))

(defn neighbourhood [a-map u]
  (assert (not (nil? u)))
  (filter #(not (wall-at a-map %)) 
          (map #(apply vplus (cons u %)) (filter #(not (empty? %)) (subsets [U0 U1])))))

(defn dist-neighbourhood [a-map u dist]
  (filter #(not (wall-at a-map %)) 
    (for [dx (vrange (vminus (svtimes dist U0)) (svtimes dist U0) U0)
          dy (vrange (vminus (svtimes dist U1)) (svtimes dist U1) U1)]
      (vplus u dx dy))))

(defn shortest-path 
  ([^Map a-map ^Vector src ^Vector dst]
    (shortest-path a-map src dst #{} #{}))
  ([^Map a-map ^Vector src ^Vector dst visited boundary] 
    (if (= src dst)
      [dst]
	    (let [boundary (remove #(= % src) (into boundary (neighbourhood a-map src)))
	          visited  (cons src visited)
            next     (first (sort #(cmp-dist dst %1 %2) boundary))]
       (cons src (shortest-path a-map next dst visited boundary))))))

(defn choose-random [xs]
  (nth (rand-nth (count xs)) xs))

(defn wander [^Map a-map ^Creature creature]
  (let [p  (rand-nth (neighbourhood a-map (.pos creature)))
        ns (filter #(not (empty (.items (square-at a-map %)))) (dist-neighbourhood a-map (.pos creature) 3))]
    (if (empty? ns) 
      (update-creature a-map creature (assoc creature :pos p))
      (update-creature a-map creature (first (shortest-path a-map (first ns)))))))
      

(def map1 (ref (new-map {:lower ZERO :upper (svtimes 10 (vplus U0 U1))})))

(def goblin (new-creature-type {:name "Goblin" :display-char "G" :hp 8 :attack 1 :defense 1})) 
(dosync (ref-set map1 (draw-square-wall @map1 (.lower @map1) (.upper @map1))))
(dosync (ref-set map1 (draw-square-wall @map1 (svtimes 2 (vplus U0 U1)) (svtimes 2 (vplus U0 U1)))))
(dosync (ref-set map1 (spawn @map1 goblin (new-vector 1 1 0))))

(square-at @map1 (new-vector 2.0 2.0 0.0))

(= (new-vector 2.0 2.0 0.0) (svtimes 2 (vplus U0 U1)))
(square-at @map1 (svtimes 2 (vplus U0 U1)))
(print-square @map1 (svtimes 2 (vplus U0 U1)))
(print-map @map1)
(shortest-path @map1 (vplus U1 U0) (svtimes 3 (vplus U1 U0)))

(dosync (ref-set map1 (reduce #(wander %1 (second %2)) @map1 (.creatures @map1))))
(print-map @map1)
