(defproject com.hypirion/io "0.4-0-SNAPSHOT"
  :description "I/O classes in Java for those with specific needs."
  :url "https://github.com/hyPiRion/com.hypirion.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths []
  :java-source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :deploy-branches ["stable"]
  :profiles
  {:dev {:dependencies [[junit/junit "4.11"]
                        [org.apache.commons/commons-lang3 "3.1"]
                        [commons-io/commons-io "2.4"]]
         :plugins [[lein-shell "0.2.0"]
                   [lein-junit "1.1.3"]]
         :junit ["test"]
         :java-source-paths ["test"]
         :aliases {"javadoc" ["shell" "javadoc" "-d" "0.4.0-SNAPSHOT"
                              "-sourcepath" "src/" "com.hypirion.io"]}}})
