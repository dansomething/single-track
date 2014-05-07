(ns single-track.core-test
  (:require [clojure.test :refer :all]
            [single-track.core :refer :all])
  (:import (java.io File)
           (org.jaudiotagger.audio AudioFile)))

(defn audio-file-mock [file] (AudioFile.))
(defn audio-tags-mock [audiofile] {:artist "foo"})
(defn audio-header-mock [audiofile] {:track-length 360 :bit-rate 256 :format "AAC"})

(deftest metadata-test
  (with-redefs [audio-file audio-file-mock
                audio-header audio-header-mock
                audio-tags audio-tags-mock]
    (testing "reading audio metadata"
      (let [md (metadata (File. "foo.m4a"))]
        (is (= (:track-length md) 360))
        (is (= (:bit-rate md) 256))
        (is (= (:format md) "AAC"))
        (is (= (:name md) "foo.m4a"))
        (is (= (:path md) "foo.m4a"))
        (is (= (:artist md) "foo"))))))

(deftest visible-files-test
  (testing "filtering hidden files"
    (are [n] (not (visible-files (File. n)))
         ".gitignore"
         ".git"
         ".DS_Store"))
  (testing "showing normal files"
    (are [n] (visible-files (File. n))
         "autoexec.bat"
         "music.mp3"
         "misleading.")))

(deftest audio-files-test
  (testing "finding audio files"
    (are [n] (audio? (File. n))
         "awesome.mp3"
         "music.mp3"
         "dothe.wav")))

(deftest file-ext-test
  (testing "getting file extension"
    (are [n e] (= e (file-ext (File. n)))
         "foo.txt" "txt"
         "bar.mp3" "mp3")))

#_(run-tests)
