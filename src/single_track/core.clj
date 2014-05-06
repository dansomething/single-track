(ns single-track.core
  (:require [camel-snake-kebab :refer [->kebab-case-keyword]]
            [clj-logging-config.jul :refer [set-logger-level!]]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.set :refer [rename-keys]]
            [clojure.string :refer [join lower-case]])
  (:import (java.io File)
           (java.util.logging Level)
           (org.jaudiotagger.audio AudioFileIO)
           (org.jaudiotagger.tag FieldKey))
  (:gen-class :main true))

(set! *warn-on-reflection* false)

(defn audio-file [file]
  (try (AudioFileIO/read file)
       (catch Exception e nil)))

(defn audio-header [audiofile]
  (rename-keys (select-keys (bean (.getAudioHeader audiofile))
                            [:trackLength :bitRateAsNumber :format])
               {:trackLength :track-length :bitRateAsNumber :bit-rate}))

(defn convert-longs [m & ks]
  (apply assoc m (mapcat #(vector % (try (Long/parseLong (m %)) (catch Exception e nil))) ks)))

(def tags
  [FieldKey/ALBUM FieldKey/ARTIST FieldKey/TITLE FieldKey/TRACK FieldKey/ALBUM_ARTIST FieldKey/GENRE FieldKey/YEAR])

(defn tag-val [tag tagname]
  (vector (->kebab-case-keyword (.name tagname)) (.getFirst tag tagname)))

(defn audio-tags [audiofile]
  (when-let [tag (.getTag audiofile)]
    (try ((convert-longs (into {} (map #(tag-val tag %1) tags))
                         :year :track))
         (catch Exception e {})) ))

(defn metadata [file]
  (merge {:name (.getName file)
          :path (.getPath file)
          :size (.length file)}
         (when-let [af (audio-file file)]
           (merge (audio-header af)
                  (audio-tags af)))))

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
