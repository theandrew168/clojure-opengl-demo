(require 'clojure.set)

;; Desired LWJGL version.
(def lwjgl-version "3.3.0-SNAPSHOT")

;; Union of all platforms supported by the LWJGL project.
(def lwjgl-all-platforms
  #{"linux"
    "linux-arm32"
    "linux-arm64"
    "macos"
    "macos-arm64"
    "windows"
    "windows-x86"
    "windows-arm64"})

;; Mapping of LWJGL modules to their available platform natives.
;; Note that some modules don't require natives at all while others
;; only require natives on specific platforms (lwjgl-vulkan, for example).
(def lwjgl-module-platform-natives
  {"lwjgl" lwjgl-all-platforms
   "lwjgl-assimp" lwjgl-all-platforms
   "lwjgl-bgfx" (disj lwjgl-all-platforms "windows-arm64")
   "lwjgl-cuda" #{}
   "lwjgl-driftfx" lwjgl-all-platforms
   "lwjgl-egl" #{}
   "lwjgl-glfw" lwjgl-all-platforms
   "lwjgl-jawt" #{}
   "lwjgl-jemalloc" lwjgl-all-platforms
   "lwjgl-libdivide" lwjgl-all-platforms
   "lwjgl-llvm" lwjgl-all-platforms
   "lwjgl-lmdb" lwjgl-all-platforms
   "lwjgl-lz4" lwjgl-all-platforms
   "lwjgl-meow" (disj lwjgl-all-platforms "linux-arm32")
   "lwjgl-meshoptimizer" lwjgl-all-platforms
   "lwjgl-nanovg" lwjgl-all-platforms
   "lwjgl-nfd" lwjgl-all-platforms
   "lwjgl-nuklear" lwjgl-all-platforms
   "lwjgl-odbc" #{}
   "lwjgl-openal" lwjgl-all-platforms
   "lwjgl-opencl" #{}
   "lwjgl-opengl" lwjgl-all-platforms
   "lwjgl-opengles" lwjgl-all-platforms
   "lwjgl-openvr" #{"linux" "macos" "windows" "windows-x86"}
   "lwjgl-opus" lwjgl-all-platforms
   "lwjgl-ovr" #{"windows" "windows-x86"}
   "lwjgl-par" lwjgl-all-platforms
   "lwjgl-remotery" lwjgl-all-platforms
   "lwjgl-shaderc" lwjgl-all-platforms
   "lwjgl-spvc" lwjgl-all-platforms
   "lwjgl-sse" #{"linux" "macos" "windows" "windows-x86"}
   "lwjgl-stb" lwjgl-all-platforms
   "lwjgl-tinyexr" lwjgl-all-platforms
   "lwjgl-tinyfd" lwjgl-all-platforms
   "lwjgl-tootle" #{"linux" "macos" "windows" "windows-x86"}
   "lwjgl-vma" lwjgl-all-platforms
   "lwjgl-vulkan" #{"macos" "macos-arm64"}  ; only requires natives on macOS
   "lwjgl-xxhash" lwjgl-all-platforms
   "lwjgl-yoga" lwjgl-all-platforms
   "lwjgl-zstd" lwjgl-all-platforms})

;; Everything
(def lwjgl-modules-everything
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-bgfx"
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
   "lwjgl-meow"
   "lwjgl-meshoptimizer"
   "lwjgl-nanovg"
   "lwjgl-nfd"
   "lwjgl-nuklear"
   "lwjgl-odbc"
   "lwjgl-openal"
   "lwjgl-opencl"
   "lwjgl-opengl"
   "lwjgl-opengles"
   "lwjgl-openvr"
   "lwjgl-opus"
   "lwjgl-ovr"
   "lwjgl-par"
   "lwjgl-remotery"
   "lwjgl-rpmalloc"
   "lwjgl-shaderc"
   "lwjgl-spvc"
   "lwjgl-sse"
   "lwjgl-stb"
   "lwjgl-tinyexr"
   "lwjgl-tinyfd"
   "lwjgl-tootle"
   "lwjgl-vma"
   "lwjgl-vulkan"
   "lwjgl-xxhash"
   "lwjgl-yoga"
   "lwjgl-zstd"])

;; Getting Started
(def lwjgl-modules-getting-started
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-bgfx"
   "lwjgl-glfw"
   "lwjgl-nanovg"
   "lwjgl-nuklear"
   "lwjgl-openal"
   "lwjgl-opengl"
   "lwjgl-par"
   "lwjgl-stb"
   "lwjgl-vulkan"])

;; Minimal OpenGL
(def lwjgl-modules-minimal-opengl
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-opengl"
   "lwjgl-stb"])

;; Minimal OpenGL ES
(def lwjgl-modules-minimal-opengles
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-egl"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-opengles"
   "lwjgl-stb"])

;; Minimal Vulkan
(def lwjgl-modules-minimal-vulkan
  ["lwjgl"
   "lwjgl-assimp"
   "lwjgl-glfw"
   "lwjgl-openal"
   "lwjgl-stb"
   "lwjgl-vulkan"])

;; Choose which set of LWJGL modules to use (can be a custom set).
(def lwjgl-modules lwjgl-modules-minimal-opengl)

;; Choose which platforms to target (can be a custom set).
(def lwjgl-platforms lwjgl-all-platforms)

;; Check if a module is supported (considering natives) on a given platform.
(defn platform-supported? [module platform]
  (let [natives (lwjgl-module-platform-natives module)]
    (cond
      (empty? natives) true
      (= module "lwjgl-vulkan") true
      (contains? natives platform) true
      :else false)))

;; Check if a module has natives for a given platform.
(defn platform-has-natives? [module platform]
  (contains? (lwjgl-module-platform-natives module) platform))

;; Get all dependencies for a given module (including natives).
;; TODO: can this be made any clearer / cleaner?
(defn get-dependencies [module]
  (let [lwjgl-ns "org.lwjgl"
        primary [(symbol lwjgl-ns module) lwjgl-version]]
    (reduce
     (fn [dependencies platform]
       (if (platform-supported? module platform)
         (if (platform-has-natives? module platform)
           (let [classifier (str "natives-" platform)
                 dependency (concat primary [:classifier classifier])]
             (conj dependencies dependency))
           dependencies)
         (do
           (printf "WARN: module %s might not work on %s\n" module platform)
           (flush)
           dependencies)))
     [primary]
     lwjgl-platforms)))

;; Get the LWJGL dependencies for the chosen modules.
(defn lwjgl-dependencies []
  (apply concat (map get-dependencies lwjgl-modules)))

;; Get all project dependencies (LWJGL + others)
(defn all-dependencies []
  (into  ; add non-LWJGL dependencies here
   '[[org.clojure/clojure "1.10.1"]
     [cider/cider-nrepl "0.26.0"]
     [nrepl "0.8.3"]]
   (lwjgl-dependencies)))

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
