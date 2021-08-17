;; LWJGL version
(def lwjgl-version "3.3.0-SNAPSHOT")

;; TODO: consider just maintaining a matrix of module to available natives?
;; Throw a warning during build that the JAR might not run on certain platforms?

;; Everything
#_(def lwjgl-modules
  ["lwjgl"
   "lwjgl-assimp"
;   "lwjgl-bgfx"  ; no natives for: windows-arm64
   "lwjgl-cuda"
   "lwjgl-driftfx"
   "lwjgl-egl"
   "lwjgl-glfw"
   "lwjgl-jawt"
   "lwjgl-jemalloc"
   "lwjgl-libdivide"
   "lwjgl-llvm"
   "lwjgl-lmdb"
   "lwjgl-lz4"
;   "lwjgl-meow"  ; no natives for: linux-arm32
   "lwjgl-meshoptimizer"
   "lwjgl-nanovg"
   "lwjgl-nfd"
   "lwjgl-nuklear"
   "lwjgl-odbc"
   "lwjgl-openal"
   "lwjgl-opencl"
   "lwjgl-opengl"
   "lwjgl-opengles"
;   "lwjgl-openvr"  ; no natives for: linux-arm32, linux-arm64, macos-arm64, windows-arm64
   "lwjgl-opus"
;   "lwjgl-ovr"  ; no natives for: linux, linux-arm32, linux-arm64, macos, macos-arm64, windows-arm64
   "lwjgl-par"
   "lwjgl-remotery"
   "lwjgl-rpmalloc"
   "lwjgl-shaderc"
   "lwjgl-spvc"
;   "lwjgl-sse"  ; no natives for: linux-arm32, linux-arm64, macos-arm64, windows-arm64
   "lwjgl-stb"
   "lwjgl-tinyexr"
   "lwjgl-tinyfd"
;   "lwjgl-tootle"  ; no natives for: linux-arm32, linux-arm64, macos-arm64, windows-arm64
   "lwjgl-vma"
   "lwjgl-vulkan"
   "lwjgl-xxhash"
   "lwjgl-yoga"
   "lwjgl-zstd"])

;; Getting Started
#_(def lwjgl-modules
  ["lwjgl"
   "lwjgl-assimp"
;   "lwjgl-bgfx"  ; no natives for: windows-arm64
   "lwjgl-glfw"
   "lwjgl-nanovg"
   "lwjgl-nuklear"
   "lwjgl-openal"
   "lwjgl-opengl"
   "lwjgl-par"
   "lwjgl-stb"
   "lwjgl-vulkan"])

;; Minimal OpenGL
(def lwjgl-modules
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-opengl"
   "lwjgl-stb"])

;; Minimal OpenGL ES
#_(def lwjgl-modules
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-egl"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-opengles"
   "lwjgl-stb"])

;; Minimal Vulkan
#_(def lwjgl-modules
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-stb"
   "lwjgl-vulkan"])

;; Targeted platforms / architectures
(def lwjgl-platforms
  ["linux"
   "linux-arm32"
   "linux-arm64"
   "macos"
   "macos-arm64"
   "windows"
   "windows-x86"
   "windows-arm64"])

;; Modules without native packages
(def lwjgl-no-natives
  #{"lwjgl-cuda"
    "lwjgl-egl"
    "lwjgl-jawt"
    "lwjgl-odbc"
    "lwjgl-opencl"
    "lwjgl-vulkan"})

;; Module lwjgl-vulkan has natives but only on macOS (macos and macos-arm64).
(defn has-natives? [module platform]
  (or (not (contains? lwjgl-no-natives module))
      (and (= module "lwjgl-vulkan")
           (some #(= platform %) ["macos" "macos-arm64"]))))

;; Get the native package names for a given LWJGL module.
(defn get-natives [module]
  (reduce
   (fn [natives platform]
     (if (has-natives? module platform)
       (conj natives (str "natives-" platform))
       natives))
   []
   lwjgl-platforms))

;; Get all dependencies for a given module (including natives).
(defn get-dependencies [module]
  (let [lwjgl-ns "org.lwjgl"
        primary [(symbol lwjgl-ns module) lwjgl-version]
        natives (get-natives module)]
    (reduce
     (fn [dependencies native]
       (let [dependency (concat primary [:classifier native])]
         (conj dependencies dependency)))
     [primary]
     natives)))

;; Get the LWJGL dependencies for the chosen modules.
(defn lwjgl-dependencies []
  (apply concat (map get-dependencies lwjgl-modules)))

;; Get all project dependencies (LWJGL + others)
(defn all-dependencies []
  (into  ; add non-LWJGL dependencies here
   '[[org.clojure/clojure "1.10.1"]]
   (lwjgl-dependencies)))

(comment
  (lwjgl-dependencies)
  (all-dependencies)

  (has-natives? "lwjgl-vulkan" "macos-arm64")
  (has-natives? "lwjgl-vulkan" "windows")
  (has-natives? "lwjgl-glfw" "windows")
  (get-natives "lwjgl-vulkan")
  (get-natives "lwjgl-glfw")
  (get-dependencies "lwjgl-vulkan")

  .)

;; https://www.eclipse.org/swt/faq.php#swtawtosxmore
(def jvm-opts
  {"Mac OS X" ["-XstartOnFirstThread"]})

(defproject clojure-opengl-demo "0.0.1"
  :description "Cross-platform OpenGL demo using Clojure + LWJGL"
  :url "https://github.com/theandrew168/clojure-opengl-demo"
  :license {:name "MIT License"
            :url "https://spdx.org/licenses/MIT.html"}
  :repositories [["lwjgl-snaphots" "https://oss.sonatype.org/content/repositories/snapshots/"]]
  :dependencies ~(all-dependencies)
  :jvm-opts ~(jvm-opts (System/getProperty "os.name"))
  :main ^:skip-aot clojure-opengl-demo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
