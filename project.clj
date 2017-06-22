(defproject wg-dashboard-v1 "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.clojure/core.async  "0.3.442"
                  :exclusions [org.clojure/tools.reader]]
                 [org.clojure/tools.logging "0.3.1"]
                 [reagent "0.6.2"]
                 [reagent-utils "0.2.1"]
                 [cljsjs/highcharts "5.0.4-0"]
                 [cljsjs/bootstrap "3.3.6-1"]
                 [cljsjs/bootstrap-slider "7.0.1-0"]
                 [environ "1.1.0"]
                 [http-kit "2.2.0"]
                 [clj-http "0.6.1" :exclusions [common-io]]
                 [compojure "1.5.1" :exclusion [ring/ring-core]]
                 [hiccups "0.3.0"]
                 [com.taoensso/sente "1.11.0"]
                 [com.taoensso/timbre "4.7.4"]
                 [secretary "1.2.3"]
                 [binaryage/devtools "0.8.3"]
                 [durable-atom "0.0.3"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring-transit "0.1.6" :exclusions [commons-codec]]
                 [stuarth/clj-oauth2 "0.3.2"]]

  :plugins [[lein-figwheel "0.5.10"]
            [lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]
            [lein-ring "0.12.0"]
            [lein-ancient "0.6.10"]
            [cider/cider-nrepl "0.13.0"]
            [deraen/lein-sass4clj "0.3.1"]
            [lein-asset-minifier "0.3.2" :exclusions [org.clojure/clojure]]
            [lein-pdo "0.1.1"]]


  :source-paths ["src/clj" "src/cljs"]

  :target-path "target/%s"

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]
                :figwheel {:on-jsload "wg-dashboard-v1.core/init"}
                           ;:open-urls ["http://localhost:3449/index.html"]}

                :compiler {:main wg-dashboard-v1.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/wg_dashboard_v1.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :externs ["src/cljs/js/highchart.ext.js"
                                     "src/cljs/js/datatables.ext.js"]
                           :preloads [devtools.preload]}}

               {:id "min"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/compiled/wg_dashboard_v1.js"
                           :main wg-dashboard-v1.core
                           :optimizations :advanced
                           :pretty-print false
                           :externs ["src/cljs/js/highchart.ext.js"
                                     "src/cljs/js/datatables.ext.js"]}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources"
              :server-port 4449 ;; default
             ;; :server-ip "127.0.0.1"
              :css-dirs ["resources/public/css"]} ;; watch and update CSS

  ;:hooks [leiningen.cljsbuild]

  ;; setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.2"]
                                  [figwheel-sidecar "0.5.10"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src/clj" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"]}

             :uberjar { :omit-source true
                        :aot :all}}

  :minify-assets {:assets {"resources/public/js/compiled/site.min.js"
                                                                ["src/cljs/js/tubingpipe.js"]}}

  :aliases {"build-dev" ["do" ["minify-assets"] ["cljsbuild" "once" "dev"]]
            "build-min" ["do" ["minify-assets"] ["cljsbuild" "once" "min"]]
            "run-dev" ["pdo" ["run" "dev"]]
            "start-dev" ["build-dev" "run-dev"]}

  :repl-options {
                 :timeout 120000}

  :main wg_dashboard_v1.ringmaster)