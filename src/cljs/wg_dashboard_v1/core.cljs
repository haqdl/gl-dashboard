(ns wg_dashboard_v1.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.set :refer [rename-keys]]
            [wg_dashboard_v1.db :as db]
            [wg_dashboard_v1.subs :as subs]
            [wg_dashboard_v1.handlers :as handlers]
            [wg_dashboard_v1.data.subs :as datasubs]
            [wg_dashboard_v1.widgets.datatable :refer [DataTable]]
            [wg_dashboard_v1.welloverview.subs :as overviewsubs]
            [wg_dashboard_v1.welloverview.handlers :as overviewhandlers]
            [wg_dashboard_v1.utils.common :as com-utils]
            [wg_dashboard_v1.utils.merge :as merge-utils]
            [wg_dashboard_v1.utils.format :as rformat]
            [wg_dashboard_v1.utils.table :as table-utils]
            [wg_dashboard_v1.utils.interpolator :as i-util]
            [wg_dashboard_v1.widgets.highchart :as highchart :refer [HighChart]]
            [wg_dashboard_v1.widgets.loadingoverlay :refer [LoadingOverlay]]
            [wg_dashboard_v1.widgets.box :refer [BoxContainer]]
            [wg_dashboard_v1.router.router :as router])

  (:require-macros [hiccups.core :as hiccups :refer [html]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;;(swap! app-state update-in [:__figwheel_counter] inc))

;;--------admin-lte----------------------------
(defn- logo []
  (fn []
    [:a.logo {:href "#"}
     [:span.logo-mini "WT"]
     [:span.logo-lg "WT Dashboard"]]))

(defn- nav-bar []
  (fn []
    [:nav.navbar.navbar-static-top
     [:div.navbar-custom-menu
      [:ul.nav.navbar-nav]]]))

(defn main-header []
     [:header.main-header
        [logo]
        [nav-bar]])
       ;[:div {:height 100}]])
;; ---------------React-grid-layout---------------

(def default-responsive-config
  {
   :class "layout"
   :cols {:lg 12 :md 10 :sm 6 :xs 4 :xxs 2}
   :breakpoints {:lg 1200 :md 996 :sm 768 :xs 480 :xxs 0}
   :rowHeight 150
   :isResizable true
   :style {:background-color "transparent"}
   :onResizeStop #(.dispatchEvent js/window (js/Event. "resize"))})

(defonce RGL (aget js/window "deps" "rgl"))

(defonce ReactGridLayout (reagent/adapt-react-class RGL))

(defonce ResponsiveReactGridLayout (reagent/adapt-react-class (RGL.WidthProvider RGL.Responsive)))

;;------------------chart---------------------------

(def chart-state (atom {}))

(defn test-chart-config [data]
  (reset!  chart-state data))

(defn home-render []
  [:div {:style {:min-width "310px" :max-width "800px"
                 :height "800px" :margin "0 auto"}}])

(defn chart-display [this]
  (reagent/create-class {:reagent-render
                         (fn []
                           [:div {:style {:min-width "300px" :max-width "800px"
                                          :height "500px" :margin "0 auto"}}])
                         :component-did-mount
                         (fn [this]
                           (js/Highcharts.Chart. (reagent/dom-node this)
                                                 (clj->js (test-chart-config db/chart-data))))}))

;;-----------------Hanson table----------------------------
(def table-state (atom {}))


(defn test-table-config [data]
  (reset! table-state data))

(defn map-to-vec [a-map]
  ;(println a-map)
  (doseq [[k v] a-map]
    (if (= k :status)
      (println "<img src = 'images/r.png' />"))))


(defn map-data-table [config data]
      (reduce-kv
        (fn [result index well]
            (let [last-game (last result)]
              (assoc-in config [:data] (conj (:data config)
                                             (map-to-vec well)))))
        []
        data))


(defn well-table-display []
  (reagent/create-class {:reagent-render
                           (fn []
                             [:table.display {:width "100%"}])
                             ;[:div
                             ; {:style {:min-width "300px" :max-width "800px"
                             ;          :height "500px" :overflow "hidden" :margin "0 auto"}}])
                         :component-did-mount
                           (fn [this]
                              (js/Handsontable (reagent/dom-node this)
                                               (clj->js (test-table-config db/table-data))))}))

(defn welltest-table-display []
  (reagent/create-class {:reagent-render
                         (fn []
                           [:div
                            {:style {:min-width "300px" :max-width "800px"
                                     :height    "500px" :overflow "hidden" :margin "0 auto"}}])
                         :component-did-mount
                         (fn [this]
                           (js/Handsontable (reagent/dom-node this)
                                            (clj->js (test-table-config db/welltest-table))))}))
;;------------------------------------------------------------------------------------

(defn reset-chart []
  (.log js/console "reset chart"))


(defn reset-welltest [])
(.log js/console "reset welltest")

;;------------Data table------------------------------------------------------------
(defn well-summary-table [data on-select-fn]
  (fn []
    [DataTable
     {
      :data data
      :paging false
      :scrollY 400
      :searching false
      :columns [{:title "Well"
                 :data :name}
                {:title "Diagsnostic Date"
                 :data :date}
                {:title "GL Status"
                 :data :status
                 :render subs/render-gl-status}
                {:title "Valves Status"
                 :data :desciption}
                {:title "GLIR Calculated"
                 :data :rate}]
      :deferRender true
      :select "single"}
     {:select (fn [e dt type index]
                (.log js/console index)
                (on-select-fn (-> (.rows dt index)
                                 (.data)
                                 (aget 0)
                                 (js->clj)
                                 (rename-keys {"well" :name
                                               "date" :date}))))}]))


;;-------------------production-------------
(defn WellSelector [data-source on-select-fn]
  (let [well-list (subs/get-all-well-status)]
    (println "well-list: " well-list)
    [:div
     (if (some? well-list)
       [DataTable
        {:data well-list
         :columns [{:title "Well"
                    :data :well}
                   {:title "Completion"
                    :searchable false
                    :data :cmpl}
                   {:title "GL status"
                    :searchable false
                    :data :glstatus
                    :render subs/render-gl-status}]
         :deferRender true
         :select "single"}
        {:select (fn [e dt type index]
                   (on-select-fn (-> (.rows dt index)
                                     (.data)
                                     (aget 0)
                                     (js->clj)
                                     (rename-keys {"field" :field
                                                   "lease" :lease
                                                   "well" :well
                                                   "cmpl" :cmpl
                                                   "glstatus" :glstatus}))))}]
       [LoadingOverlay])]))

(defn WellPicker []
  (let [selected-data-source (overviewsubs/get-selected-datasource)]

    [:div
     (let [datasources (overviewsubs/get-datasources)]
       (let [selected-data-source (overviewhandlers/set-selected-datasource (first (keys (first datasources))))]
         (println "selected-data-source" selected-data-source)
         (if (nil? selected-data-source)
           [:div
            ;; Well Selector
            [BoxContainer
             {:header
              {:title "Select Well"
               :with-border true}}
             [WellSelector selected-data-source
              #(do
                 (handlers/set-selected-well %))]]])))]))

(defn DVSPChart []
  (let [data-source (overviewsubs/get-selected-datasource)
        well (overviewsubs/get-selected-well)
        dvsp-config (overviewsubs/get-dvsp-config)
        depth-profile (datasubs/get-depth-profile)
        equilibrium-profile (datasubs/get-equilibrium-profile)
        ppm-curve (:production-string-depth-profile-map depth-profile)
        ipm-curve (:injection-string-depth-profile-map depth-profile)
        valve-map (:valves-status-map depth-profile)
        mandrel-survey (datasubs/get-mandrel-survey)
        vert-depth-list (:vert-depth-list ppm-curve)
        max-depth (* (+ (int (/ (apply max vert-depth-list) 1000)) 1) 1000)
        plot-lines (vec (for [mandrel (:vert-depth-list valve-map)]
                          {:color "#000000"
                           :value mandrel
                           :marker {:enabled false}
                           :zIndex 1 ;; Show above gridlines
                           :width 2
                           :label {:text (str "Mandrel Line: " (rformat/format-dec mandrel 2) " ft")}
                           :tooltip {:headerFormat
                                                     (str "<span style=\"font-size: 10px\">Mandrel</span><br/>")
                                     :pointFormatter com-utils/mandrel-point-formatter}}))
        filtered-valve-map (com-utils/filter-dummy-valves-from-valve-map valve-map)
        chart-config (-> (merge-utils/deep-merge
                           (highchart/prep-chart-config
                             dvsp-config
                             {:ppm (table-utils/map-table-to-array
                                     ppm-curve :flow-press-list :vert-depth-list)}
                             {:ipm (table-utils/map-table-to-array
                                     ipm-curve :flow-press-list :vert-depth-list)}
                             {:eq (table-utils/map-table-to-array
                                    equilibrium-profile :flow-press-list :vert-depth-list)}
                             {:open-points (table-utils/map-table-to-array
                                             filtered-valve-map :open-press-list :vert-depth-list)}
                             {:close-points (table-utils/map-table-to-array
                                              filtered-valve-map :close-press-list :vert-depth-list)}
                             {:vpc-bfp (table-utils/map-table-to-array
                                         filtered-valve-map :vpc-begin-flow-list :vert-depth-list)})
                           {:chart {:height 500}}
                           {:yAxis {:plotLines plot-lines
                                    :max max-depth}})
                         ;; There's no way to turn plotlines off/on so we have a dummy series and remove/add the
                         ;; plot lines
                         (update-in [:series] #(into %1 %2)
                                    [{:marker {:enabled false} ;; Don't show the little marker on the legend to remain consistent with other curves
                                      :events {:show #(let [y-axis (aget (js* "this") "chart" "yAxis" 0)]
                                                        (.update y-axis (clj->js {:plotLines plot-lines})))
                                               :hide #(let [chart-plot-lines (aget % "target" "chart" "yAxis" 0 "plotLinesAndBands")]
                                                        (doall
                                                          (for [idx (range (- (aget chart-plot-lines "length") 1) -1 -1)]
                                                            (.destroy (aget chart-plot-lines idx)))))}
                                      :name "Mandrel Lines"}]))]
    (if (and
          (some? depth-profile)
          (some? max-depth))
      [:div.row
       [:div.col-xs-3.col-sm-2
        (if (and (some? max-depth)
                 (some? depth-profile))
          ;[TubingPipe data-source well {:max-depth max-depth :height 480}]
          [LoadingOverlay])]
       [:div.col-xs-9.col-sm-10
        (if (and  (some? depth-profile)
                  (some? max-depth)
                  (some? equilibrium-profile)
                  (some? ppm-curve)
                  (some? ipm-curve)
                  (some? valve-map)
                  (some? mandrel-survey))
          [HighChart chart-config]
          [LoadingOverlay])]]
      [LoadingOverlay])))

(defn WellTestInfos []
  (let [welltest-hist-map (datasubs/get-welltest-hist)
        welltest-list (vals welltest-hist-map)
        indata (map (fn [in] {:welltest-date (rformat/format-iso-date (:welltest-date in))
                              :calib-oil-rate (rformat/format-dec (:calib-oil-rate in) 2)
                              :calib-water-rate (rformat/format-dec (:calib-water-rate in) 2)
                              :calib-liquid-rate (rformat/format-dec (:calib-liquid-rate in) 2)
                              :calib-wc (rformat/format-dec (:calib-wc in) 2)
                              :calib-total-gas (rformat/format-dec (:calib-total-gas in) 2)
                              :calib-flowing-tubing-press (rformat/format-dec (:calib-flowing-tubing-press in) 2)
                              :calib-casing-head-press (rformat/format-dec (:calib-casing-head-press in) 2)
                              :est-fbhp (rformat/format-dec (:est-fbhp in) 2)
                              :calib-lift-gas-rate (rformat/format-dec (:calib-lift-gas-rate in) 2)
                              :calib-total-glr (rformat/format-dec (:calib-total-glr in) 2)
                              :calib-formation-gas-rate (:calib-formation-gas-rate in) })(sort-by :welltest-date > welltest-list))]
    [:div
     [BoxContainer
      {:header
       {:title "Well Test Infos"
        :with-border true}}
      (if (> (count indata) 1)
        [DataTable
         {:data indata
          :columns [{:title "Date"
                     :data :welltest-date}
                    {:title "Oil (bbl/day)"
                     :data :calib-oil-rate}
                    {:title "Water (bbl/day)"
                     :data :calib-water-rate}
                    {:title "Total liquid (bbl/day)"
                     :data :calib-liquid-rate}
                    {:title "Watercut (%)"
                     :data :calib-wc}
                    {:title "Total Gas (MCF/data)"
                     :data :calib-total-gas}
                    {:title "Tubing Press. (psig)"
                     :data :calib-flowing-tubing-press}
                    {:title "Casing Press. (psig)"
                     :data :calib-casing-head-press}
                    {:title "Stored FBHP (psig)"
                     :data :est-fbhp}
                    {:title "Gas LG (MCF/data)"
                     :data :calib-lift-gas-rate}
                    {:title "Total GL Rate (MCF/day)"
                     :data :calib-total-glr}]
          :deferRender true
          :select "single"}
         {:select (fn [e dt type index])}]
        [LoadingOverlay])]]))

(defn OilrateInf []
  (let [welltest-hist-map (datasubs/get-welltest-hist)
        welltest-list (vals welltest-hist-map)
        indata (vec (map (fn [in] [(com-utils/getUTCtime (:welltest-date in)) (js/parseFloat (rformat/format-dec (:calib-oil-rate in) 2))])
                         (sort-by :welltest-date > welltest-list)))
        chart-config {:chart {:type "spline"}
                      :title {:text "Oil rate vs. time"}
                      :xAxis {:type "datetime"
                              :labels {:format "{value:%Y-%m-%d}"}
                              :title {:text "Date"}}
                      :yAxis {:title {:text "Oil rate (bbq/day)"}}
                      :series [{:name "Oil rate"
                                :data indata}]}]
    [:div
     [BoxContainer
      {:header
       {:title "Oil rate vs. time"
        :with-border true}}
      [HighChart chart-config]]]))

;;-------------------------------

(defn main-content []
  [:div
      [:div
      ;[ResponsiveReactGridLayout  default-responsive-config
        [:div {:style {:width "45%" :float "left" :margin "40px 20px 20px 20px"}}
         ;{:key "1" :data-grid {:i "well" :x 0 :y 0 :w 5 :h 4 :minH 1 :minW 1}}
         [:div {:style {:height 50 :text-align "center" :font-weight "600" :font-size "large"}} "Wells Summary"]
         ;[WellPicker]
         [well-summary-table db/well-data
                            #(do
                              (reset-chart)
                              (reset-welltest))]]
        [:div {:style {:width "50%" :float "right" :margin "50px 20px 0 0"}}
         ;{:key "2" :data-grid {:i "chart" :x 5 :y 0 :w 6 :h 4 :minH 1 :minW 3}}
         [chart-display]]]
      [:div
       [:div{:style {:width "100%" :float "left"}}
        ;{:key "3" :data-grid {:i "welltest" :x 0 :y 2 :w 6 :h 2 :minH 1 :minW 1}}
        [:div {:style {:height 50 :text-align "center" :font-weight "600" :font-size "large"}} "Well Test History"]
        [welltest-table-display]]]])
         ;[:div
         ; {:key "4" :data-grid {:i "datatable" :x 0 :y 3 :w 6 :h 2 :minH 1 :minW 1}}
         ; [:div {:style {:height 50 :text-align "center" :font-weight "600" :font-size "large"}} "Well Test Table"]
         ; [well-table-display]]])

;;---------------------------------------------------------

(defn home-page []
      [:div
       [main-header]
       [main-content]])

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))


(defn ^:export init []
  (router/init-routes)
  (mount-root))