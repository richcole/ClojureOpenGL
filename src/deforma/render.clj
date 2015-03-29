(ns deforma.render
  (:use deforma.shaders)
  (:import deforma.shaders.Programs)
  (:use deforma.gid)
  (:gen-class))

(defprotocol Renderable
  (render [self ^Programs programs])
)

(deftype RenderableMap [items-ref]
  Renderable
  (render [self programs]
    (doseq [[key item] (deref items-ref)] (render item programs))))

(defn new-renderable-map []
  (RenderableMap. (ref {})))

(defn renderable-map-put [rm key item]
  (dosync 
   (let [items-ref (.items-ref rm)
         items (deref items-ref)]
     (ref-set items-ref (assoc items key item)))))

(defn renderable-map-keys [rm]
  (dosync 
   (let [items-ref (.items-ref rm)
         items (deref items-ref)]
     (keys items))))

(defn renderable-map-clear [rm]
  (dosync (ref-set (.items-ref rm) {})))

(defn renderable-ref-set [ref item]
  (dosync (ref-set (.item-ref ref) item)))




