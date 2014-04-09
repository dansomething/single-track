(defproject single-track "0.1.0-SNAPSHOT"
  :description "Duplicate music track finder"
  :url "https://github.com/dansomething/single-track"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[slamhound "1.5.4"]]}
             :uberjar {:aot :all}}
  :main single-track.core
  :uberjar-name "single-track.jar")
