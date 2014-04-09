(ns single-track.core
  (:require [clojure.java.io :as io])
  (:import (java.io File))
  (:gen-class :main true))

(set! *warn-on-reflection* true)

(defn visible-files [^File file]
  (not= \. (-> file .getName first)))

(defn filtered-file-seq
  "A filtered tree seq on java.io.Files"
  [pred dir]
  (tree-seq
    (fn [^File f] (.isDirectory f))
    (fn [^File d] (filter pred (.listFiles d)))
    dir))

(defn visible-file-seq [file]
  (filtered-file-seq visible-files file))

(defn file-ext [^File file]
  (let [name (.getName file)]
    (.substring name (inc (.lastIndexOf name ".")))))

(def audio-ext #{"mp3" "wav" "m4a"})
(defn audio? [file] (audio-ext (file-ext file)))
(defn file? [^File file] (not (.isDirectory file)))

(defn all? [& fns] (fn [x] (every? #(% x) fns)))

(defn -main
  ([] (-main "."))
  ([dir]
   (doseq [^File f (filter (all? file? audio?) (visible-file-seq (io/file dir)))]
     (println (.getPath f)))))

#_(-main)
