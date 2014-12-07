(ns my-stuff.images
  (:import java.nio.ByteBuffer
           java.nio.ByteOrder
           javax.imageio.ImageIO
           java.awt.image.BufferedImage)
  (:use  my-stuff.gl-thread)
  (:gen-class))

(defn load-image [path]
  (ImageIO/read (clojure.java.io/resource path)))

(defn get-rgba [^BufferedImage image]
  (let [width (.getWidth image)
        height (.getHeight image)
        buffer (ByteBuffer/allocateDirect (* width height 4))]
    (.order buffer (ByteOrder/nativeOrder))
    (doseq [y (range 0 height) x (range 0 width)]
      (let [rgb (.getRGB image x y)]
        (.put buffer (unchecked-byte (bit-and (bit-shift-right rgb 16) 0xff)))
        (.put buffer (unchecked-byte (bit-and (bit-shift-right rgb 8) 0xff)))
        (.put buffer (unchecked-byte (bit-and (bit-shift-right rgb 0) 0xff)))
        (.put buffer (unchecked-byte (bit-and (bit-shift-right rgb 24) 0xff)))))
    (.flip buffer)
    buffer))


