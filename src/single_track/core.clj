(ns single-track.core
  (:require [clojure.java.io :as io])
  (:import [java.io File]))

(set! *warn-on-reflection* true)

(def audio-ext #{"mp3" "wav"})

(defn visible-files [^File file]
  (not= \. (-> file .getName first)))

(defn filtered-file-seq
  "A filtered tree seq on java.io.Files"
  [dir pred]
  (tree-seq
    (fn [^File f] (. f (isDirectory)))
    (fn [^File d] (filter pred (. d (listFiles))))
    dir))

(defn file-ext [^File file]
  (let [name (.getName file)]
    (.substring name (inc (.lastIndexOf name ".")))))

(defn audio? [^File file]
  (audio-ext (file-ext file)))

(defn file? [^File file]
  (not (.isDirectory file)))

(defn good? [file]
  (and (file? file) (audio? file)))

(defn -main
  ([] (-main "."))
  ([dir]
   (doseq [^File f (filter good? (filtered-file-seq (io/file dir) visible-files))]
     (println (.getName f)))))

#_(-main)
