# clojure-opengl-demo
Cross-platform OpenGL demo using Clojure + LWJGL

## Running
Assuming [Clojure](https://clojure.org/) and [Leiningen](https://leiningen.org/) are installed, simply run:
```
lein run
```

## Building Standalone JAR
We need the extra Leiningen var here since `macos-arm64` natives aren't in LWJGL stable quite yet.
```
LEIN_SNAPSHOTS_IN_RELEASE=true lein uberjar
```

## Platform Support
The JAR built by the above command _should_ run (assuming Java is installed) on:
* Linux x64
* Linux arm64
* Linux arm32
* macOS x64
* macOS arm64
* Windows x64
* Windows x86
* Windows arm64
