(ns single-track.core-test
  (:require [clojure.test :refer :all]
            [single-track.core :refer :all])
  (:import [java.io File]))

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
