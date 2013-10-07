(defproject my-stuff "0.1.0-SNAPSHOT"
  :description "Experiements in OpenGL with Clojure"
  :url "http://example.com/FIXME"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies 
  [
   [org.clojure/clojure "1.5.1"]
   [org.lwjgl.lwjgl/lwjgl "2.9.0"]
   [org.lwjgl.lwjgl/lwjgl-platform "2.9.0"
    :classifier "natives-linux"
    :native-prefix ""
    ]
   [org.lwjgl.lwjgl/lwjgl_util "2.9.0"]
   ]
  :main ^:skip-aot my-stuff.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :native-path "native"
  )
