(defproject com.hypirion/io "0.3.1"
  :description "I/O classes in Java for those with specific needs."
  :url "https://github.com/hyPiRion/com.hypirion.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths []
  :java-source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :deploy-branches ["stable"]
  :profiles
  {:dev {:plugins [[lein-shell "0.2.0"]]
         :aliases {"javadoc" ["shell" "javadoc" "-d" "0.4.0-SNAPSHOT"
                              "-sourcepath" "src/" "com.hypirion.io"]}}})
