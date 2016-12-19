(defproject libreforge "0.1.0-SNAPSHOT"
  :description "The study group platform"
  :url "https://www.libreforge.com"
  :license {:name "Apache 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}

  :source-paths ["src"]
  :main "libreforge.app"
 :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                [org.clojure/tools.logging "0.3.1"]
                [buddy "1.2.0"]
                [funcool/struct "1.0.0"]
                [funcool/cuerdas "2.0.1"]
                [funcool/promesa "1.6.0"]
                [funcool/catacumba "2.0.0-SNAPSHOT"]
                [funcool/suricatta "1.2.0"]
                [hikari-cp "0.13.0" :exclusions [com.zaxxer/HikariCP]]
                [com.zaxxer/HikariCP-java6 "2.2.5"]
                [org.postgresql/postgresql "9.4.1212"]
                [graphql-clj "0.1.20"]
                [niwinz/migrante "0.1.0"]])
