(defproject clojure-opengl-demo "0.0.1"
  :description "Cross-platform OpenGL demo using Clojure + LWJGL"
  :url "https://github.com/theandrew168/clojure-opengl-demo"
  :license {:name "MIT License"
            :url "https://spdx.org/licenses/MIT.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :main ^:skip-aot clojure-opengl-demo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
