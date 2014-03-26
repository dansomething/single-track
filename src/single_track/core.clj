(ns single-track.core
  (:require [clojure.java.io :as io]))

(defn file-filter [file]
  (not= \. (-> file .getName first)))

(defn my-file-seq
  "A tree seq on java.io.Files"
  ([dir]
    (my-file-seq dir (fn [_] true)))
  ([dir pred]
    (tree-seq
     (fn [^java.io.File f] (. f (isDirectory)))
     (fn [^java.io.File d] (filter pred (seq (. d (listFiles)))))
     dir)))

(defn -main [dir]
  (doseq [f (my-file-seq (io/file dir) file-filter)]
    (println f)))
