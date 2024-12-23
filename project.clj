(defproject invaders "1.0.0"
  :description "Detect space invaders"
  :url "https://github.com/mll"
  :main invaders.core/-main
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/spec.alpha "0.5.238"]]
  :repl-options {:init-ns invaders.core})
