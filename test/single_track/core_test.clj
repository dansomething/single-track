(ns single-track.core-test
  (:require [clojure.test :refer :all]
            [single-track.core :refer :all])
  (:import [java.io File]))

(deftest hidden-file-filter
  (testing "filtering hidden files"
    (are [n] (not (visible-files (File. n)))
         ".gitignore" ".git" ".DS_Store"))
  (testing "showing normal files"
    (are [n] (visible-files (File. n))
         "autoexec.bat" "music.mp3" "misleading.")))

#_(run-tests)
