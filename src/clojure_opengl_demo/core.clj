(ns clojure-opengl-demo.core
  (:gen-class)
  (:require [clojure.set]
            [nrepl.server :as nrepl-server]
            [cider.nrepl :as cider])
  (:import (org.lwjgl.opengl GL GL11 GL20)
           (org.lwjgl.glfw GLFW GLFWErrorCallback)))

;; Parse command line args into a map of options.
(defn parse-opts
  ([args]
   (parse-opts args {:fullscreen false
                     :repl false
                     :vsync false}))
  ([args opts]
   (let [head (first args)
         tail (rest args)]
     (cond
       (nil? head) opts
       (or (= head "-f")
           (= head "--fullscreen")) (parse-opts tail (assoc opts :fullscreen true))
       (or (= head "-r")
           (= head "--repl")) (parse-opts tail (assoc opts :repl true))
       (or (= head "-v")
           (= head "--vsync")) (parse-opts tail (assoc opts :vsync true))
       :else (parse-opts tail opts)))))

;; TODO: how do I stop this thread at app termination?
(defn start-cider-nrepl []
  (.start
   (Thread.
    (fn []
      (println "Starting Cider nREPL Server on 7888")
      (nrepl-server/start-server :port 7888 :handler cider/cider-nrepl-handler)))))

(defn -main
  [& args]
  (let [opts (parse-opts args)]
    (when (:repl opts)
      (start-cider-nrepl))
    (GLFW/glfwSetErrorCallback (GLFWErrorCallback/createPrint System/err))
    (when-not (GLFW/glfwInit)
      (throw (RuntimeException. "Failed to init GLFW")))
    (try
      (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE)
      (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3)
      (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MINOR 3)
      (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_PROFILE GLFW/GLFW_OPENGL_CORE_PROFILE)
      (GLFW/glfwWindowHint GLFW/GLFW_OPENGL_FORWARD_COMPAT GLFW/GLFW_TRUE)
      (let [monitor (GLFW/glfwGetPrimaryMonitor)
            mode (GLFW/glfwGetVideoMode monitor)
            width (if (:fullscreen opts) (.width mode) 640)
            height (if (:fullscreen opts) (.height mode) 640)]
        (GLFW/glfwWindowHint GLFW/GLFW_RED_BITS (.redBits mode))
        (GLFW/glfwWindowHint GLFW/GLFW_GREEN_BITS (.greenBits mode))
        (GLFW/glfwWindowHint GLFW/GLFW_BLUE_BITS (.blueBits mode))
        (GLFW/glfwWindowHint GLFW/GLFW_REFRESH_RATE (.refreshRate mode))
        (let [window
              (GLFW/glfwCreateWindow width height "Clojure OpenGL Demo"
                                     (if (:fullscreen opts) monitor 0) 0)]
          (when-not window
            (throw (RuntimeException. "Failed to create GLFW window")))
          (try
            (GLFW/glfwSetInputMode window GLFW/GLFW_STICKY_KEYS GLFW/GLFW_TRUE)
            (GLFW/glfwMakeContextCurrent window)
            (GLFW/glfwSwapInterval (if (:vsync opts) 1 0))
            (GL/createCapabilities)
            (println "OpenGL Vendor:  " (GL11/glGetString GL11/GL_VENDOR))
            (println "OpenGL Renderer:" (GL11/glGetString GL11/GL_RENDERER))
            (println "OpenGL Version: " (GL11/glGetString GL11/GL_VERSION))
            (println "GLSL Version:   " (GL11/glGetString GL20/GL_SHADING_LANGUAGE_VERSION))
            (while (not (GLFW/glfwWindowShouldClose window))
              (when (= (GLFW/glfwGetKey window GLFW/GLFW_KEY_ESCAPE) GLFW/GLFW_PRESS)
                (GLFW/glfwSetWindowShouldClose window true))
              (GL11/glClearColor 0.2 0.3 0.4 0.0)
              (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
              (GLFW/glfwSwapBuffers window)
              (GLFW/glfwPollEvents))
            (finally
              (GLFW/glfwDestroyWindow window)))))
        (finally
          (GLFW/glfwTerminate))))
  (shutdown-agents))
