(defproject deforma "0.1.0-SNAPSHOT"
  :description "Experiements in OpenGL with Clojure"
  :url "http://example.com/FIXME"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :jvm-opts ^:replace []
  :dependencies 
  [[org.clojure/clojure "1.5.1"]
   [org.lwjgl.lwjgl/lwjgl "2.9.0"]
   [org.lwjgl.lwjgl/lwjgl-platform "2.9.0"
    :classifier "natives-linux"
    :native-prefix ""]
   [org.lwjgl.lwjgl/lwjgl_util "2.9.0"]
   [com.jme3/jme3-core "3.0.10"]
   [com.jme3/jme3-desktop "3.0.10"]
   [com.jme3/jme3-lwjgl "3.0.10"]
;   [com.jme3/jme3-blender "3.0.10"]
;   [com.jme3/jme3-plugins "3.0.10"]

;   [clj-audio "0.3.0-SNAPSHOT"]
;   [net.javazoom/jlayer "1.0.1"]
;   [net.javazoom/mp3spi "1.9.5"]

   [org.clojure/core.typed "0.2.77"]
   [net.mikera/cljunit "0.3.1"]

   [criterium "0.4.3"]
   
   [log4j/log4j "1.2.17"]
   [java3d/vecmath "1.3.1"]
   [com.google.inject/guice "3.0"]
   [com.google.guava/guava "13.0.1"]
   [com.google.code.gson/gson "2.2.4"]
   [gov.nist.math.jama/gov.nist.math.jama "1.1.1"]

   [cz.advel.jbullet/jbullet "20101010-1"]
   ]
  :repositories [["jme3" "http://updates.jmonkeyengine.org/maven/"]
                 ["snapshots" "file:///home/local/ANT/richcole/LocalClojure"]
                 ["releases" "file:///home/local/ANT/richcole/LocalClojure"]]
  :main deforma.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :native-path "native"
  :java-source-paths ["src/java/"]
  :resource-paths ["resources" "jars/jl1.0.1.jar"]
  )

  
;   [org.clojars.trptcolin/core.async "0.1.242.1"]
;   [net.mikera/vectorz-clj "0.26.2"]
