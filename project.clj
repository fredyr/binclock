(defproject binclock "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [om "0.2.3"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "binclock"
              :source-paths ["src"]
              :compiler {
                :output-to "binclock.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
