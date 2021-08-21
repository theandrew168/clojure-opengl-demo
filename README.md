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
