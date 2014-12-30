(ns deforma.gid
  (:gen-class))

(defprotocol Gidable 
  (gid [self]))