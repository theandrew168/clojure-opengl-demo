;; https://www.eclipse.org/swt/faq.php#swtawtosxmore
(def jvm-opts
  {"Mac OS X" ["-XstartOnFirstThread"]})

(def lwjgl-ns "org.lwjgl")
(def lwjgl-version "3.3.0-SNAPSHOT")

;; Minimal OpenGL
(def lwjgl-modules
  ["lwjgl"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-opengl"
   "lwjgl-stb"])

;; These modules don't have any associated native packages.
;; TODO: lwjgl-vulkan has natives only on macos-arm64
(def no-natives?
  #{"lwjgl-cuda"
    "lwjgl-egl"
    "lwjgl-jawt"
    "lwjgl-odbc"
    "lwjgl-opencl"
;;  "lwjgl-vulkan"
    })

;; It's safe to just include all native dependencies, but you might
;; save some space if you know you don't need some platform(s).
(def lwjgl-platforms
  ["linux"
   "linux-arm32"
   "linux-arm64"
   "macos"
   "macos-arm64"
   "windows"
   "windows-x86"
   "windows-arm64"])

(defn lwjgl-dependencies []
  (apply concat
         (for [m lwjgl-modules]
           (let [prefix [(symbol lwjgl-ns m) lwjgl-version]]
             (into [prefix]
                   (if (no-natives? m)
                     []
                     (for [p lwjgl-platforms]
                       (into prefix [:classifier (str "natives-" p)]))))))))

(def all-dependencies
  (into ;; Add your non-LWJGL dependencies here
   '[[org.clojure/clojure "1.10.1"]]
   (lwjgl-dependencies)))

(comment
  (lwjgl-dependencies)
  all-dependencies


  .)

(defproject clojure-opengl-demo "0.0.1"
  :description "Cross-platform OpenGL demo using Clojure + LWJGL"
  :url "https://github.com/theandrew168/clojure-opengl-demo"
  :license {:name "MIT License"
            :url "https://spdx.org/licenses/MIT.html"}
  :repositories [["lwjgl-snaphots" "https://oss.sonatype.org/content/repositories/snapshots/"]]
  :dependencies ~all-dependencies
  :jvm-opts ~(jvm-opts (System/getProperty "os.name"))
  :main ^:skip-aot clojure-opengl-demo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
