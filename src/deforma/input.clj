(ns deforma.input
  (:use 
    deforma.util
    deforma.vector
    deforma.state)
  (:import [org.lwjgl.opengl Display DisplayMode GL11 GL12 GL13 GL20 GL30 GL31]
           [org.lwjgl.util.glu GLU]
           [org.lwjgl.input Keyboard Mouse]
           deforma.state.State
           )
  (:gen-class))

(declare update-input-state)

(deftype KeyboardEvent [key down?])
(deftype MouseEvent [left-down? middle-down? right-down? dx dy])

(defn get-keyboard-event []
  (when (Keyboard/next)
    (KeyboardEvent. 
      (Keyboard/getEventKey) 
      (Keyboard/getEventKeyState))))

(defn get-mouse-event []
  (when (Mouse/next)
    (MouseEvent.
    (Mouse/isButtonDown 0) 
    (Mouse/isButtonDown 1) 
    (Mouse/isButtonDown 2) 
    (Mouse/getEventDX)
    (Mouse/getEventDY))))

(defn process-keyboard-event [^State state ^KeyboardEvent event]
  (let [key (.key event)]
    (cond 
     (= key Keyboard/KEY_A) 
       (assoc state :move-left  (.down? event))
     (= key Keyboard/KEY_D) 
       (assoc state :move-right (.down? event))
     (= key Keyboard/KEY_W) 
       (assoc state :move-fwd   (.down? event))
     (= key Keyboard/KEY_S) 
       (assoc state :move-back  (.down? event))
     :else state)))

(defn process-mouse-event [^State state ^MouseEvent event]
  (if (.left-down? event)
    (assoc state 
      :mouse-grabbed true
      :mx (+ (.mx state) (.dx event))
      :my (+ (.my state) (.dy event)))
    (assoc state :mouse-grabbed false)))

(defn update-state-with-keyboard-input [^State state]
  (let [events (take-while not-nil? (repeatedly get-keyboard-event))
        next-state (reduce process-keyboard-event state events)]
    next-state))

(defn update-state-with-mouse-input [^State state]
  (let [events (take-while not-nil? (repeatedly get-mouse-event))
        next-state (reduce process-mouse-event state events)]
    next-state))

(defn update-vel-component [^State state [pred dirn]]
  (if (pred state) 
    (assoc state :vel (vplus (.vel state) dirn))
    state))

(defn update-velocity [^State state]
  (reduce update-vel-component 
          (assoc state :vel ZERO)
          [[:move-left  (.left state)]
           [:move-right (vminus (.left state))]
           [:move-fwd (.fwd state)]
           [:move-back (vminus (.fwd state))]]))

(defn update-position [^State state ^Double dt]
  (let [{:keys [pos fwd up vel speed]} state
        pos (vplus pos (svtimes (* dt speed) vel))]
    (assoc state :pos pos)))

(defn screen-scale [^Double x]
  (/ x 500.0))

(defn update-direction [^State state]
  (let [sx (- (screen-scale (.mx state)))
        sy (screen-scale (.my state))
        dirn (from-angles sy sx 0)
        fwd  (qvtimes dirn (vminus U2))
        up   (qvtimes dirn U1)
        left (vcross up fwd)]
;    (println "sx" sx "sy" sy "fwd" fwd "up" up "left" left )
    (assoc state :dirn dirn :fwd fwd :up up :left left)))

(defn update-state [^Double dt ^State state]
  (-> state 
      update-direction
      update-velocity
      (update-position dt)))

(defn update-mouse-grabbed [state]
  (if (.mouse-grabbed state)
    (when (not (Mouse/isGrabbed)) (Mouse/setGrabbed true))
    (when (Mouse/isGrabbed) (Mouse/setGrabbed false)))
  state)

(defn update-input-state []
  (dosync
      (ref-set game-state 
               (-> @game-state 
                   update-state-with-keyboard-input
                   update-state-with-mouse-input
                   update-mouse-grabbed))))
