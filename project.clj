(defproject cljsinvaders "0.0.0"
  :min-lein-version "2.2.0"
  :dependencies  [[org.clojure/clojure "1.5.1"]
                  [org.clojure/clojurescript "0.0-1859"]]
  :plugins [[lein-cljsbuild "0.3.3"]] 
  :cljsbuild {
    :builds [{
        :source-paths ["src/"]
        :compiler {
          :output-to "resources/public/game.js"
          :optimizations :whitespace
          :pretty-print true
                   }}]} 
)

