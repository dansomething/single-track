(ns single-track.core
  (:require [clojure.java.io :as io])
  (:import [java.io File]))

(set! *warn-on-reflection* true)

(defn visible-files [^File file]
  (not= \. (-> file .getName first)))

(defn filtered-file-seq
  "A filtered tree seq on java.io.Files"
  [dir pred]
  (tree-seq
    (fn [^File f] (. f (isDirectory)))
    (fn [^File d] (filter pred (. d (listFiles))))
    dir))

(defn -main
  ([] (-main "."))
  ([dir]
   (doseq [f (filtered-file-seq (io/file dir) visible-files)]
     (println (.getName f)))))
