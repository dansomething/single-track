(ns single-track.core
  (:require [clj-logging-config.jul :refer [set-logger-level!]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.set :refer [rename-keys]])
  (:import (java.io File)
           (java.util.logging Level)
           (org.jaudiotagger.audio AudioFileIO))
  (:gen-class :main true))

(set! *warn-on-reflection* false)

(defn audio-file [file]
  (try (AudioFileIO/read file)
       (catch Exception e nil)))

(defn audio-header [audiofile]
  (rename-keys (select-keys (bean (.getAudioHeader audiofile))
                            [:trackLength :bitRateAsNumber :format])
               {:bitRateAsNumber :bitRate}))

(defn audio-tags [audiofile]
  (bean (.getTag audiofile)))

(defn metadata [file]
  (merge {:name (.getName file) :path (.getPath file)}
         (when-let [af (audio-file file)]
           {:header (audio-header af)})))

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
   (set-logger-level! "org.jaudiotagger" Level/OFF)
   (doseq [^File f (filter (all? file? audio?) (visible-file-seq (io/file dir)))]
     (pprint (metadata f)))))

#_(-main)
