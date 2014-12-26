(ns deforma.gid
  (:import deforma.GID)
  (:gen-class))

(defn gid [x] (.getGid (:id x)))
