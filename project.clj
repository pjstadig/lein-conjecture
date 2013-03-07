(defproject name.stadig/lein-conjecture "0.2.0-SNAPSHOT"
  :description "leiningen plugin for conjecture"
  :url "http://github.com/pjstadig/lein-conjecture"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[name.stadig/conjecture "0.1.0"]]
  :aliases {"test" "conjecture"})
