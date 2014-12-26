(defproject deforma "0.1.0-SNAPSHOT"
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
    :native-prefix ""]
   [org.lwjgl.lwjgl/lwjgl_util "2.9.0"]
   [com.jme3/jme3-core "3.0.10"]
   [com.jme3/jme3-desktop "3.0.10"]
   [com.jme3/jme3-lwjgl "3.0.10"]
   [com.jme3/jme3-blender "3.0.10"]
   [com.jme3/jme3-plugins "3.0.10"]
   ]
  :repositories [["jme3" "http://updates.jmonkeyengine.org/maven/"]]
  :main deforma.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :native-path "native"
  :java-source-paths ["src/java/"]
  )

;   [org.clojars.trptcolin/core.async "0.1.242.1"]
;   [net.mikera/vectorz-clj "0.26.2"]
