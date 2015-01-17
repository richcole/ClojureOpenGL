(ns deforma.java2d
  (:import 
    java.awt.Frame 
    java.awt.event.WindowAdapter 
    java.awt.event.WindowEvent 
    java.lang.System
    java.awt.Graphics
    java.awt.Graphics2D
    java.awt.Color)
  (:gen-class))

(defn paint-frame [^Graphics2D g] 
  (.setColor g Color/red)
  (.drawRect g 50 50 200 200);
)

(defn new-frame []
  (let [frame (proxy [Frame] [] 
                (paint [^Graphics g] (paint-frame g)))]
    (.setName frame "An Example")
    (.setSize frame 400 300)
    (.setVisible frame true)
    (.addWindowListener frame (proxy [WindowAdapter] []
      (windowClosing [^WindowEvent e] 
         (.dispose frame)
         )))
    frame
))

(future (new-frame))