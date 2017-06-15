(ns wg-dashboard-v1.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.set :refer [rename-keys]]
            [wg-dashboard-v1.db :as db]
            [wg-dashboard-v1.datatable :refer [DataTable]]))
  ;(:gen-class))

(enable-console-print!)

(println "This text is printed from src/wg-dashboard-v1/core.cljs. Go ahead and edit it and see reloading in action.")

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
                              ;(.addEventListener js/window "mousedown" (.log js/console "fired")))}))


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
  (.log js/console "aaa"))


(defn reset-welltest [])

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
                 :data :status}
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


(defn main-content []
  [:div
      [:div
      ;[ResponsiveReactGridLayout  default-responsive-config
        [:div {:style {:width "45%" :float "left" :margin "40px 20px 20px 20px"}}
         ;{:key "1" :data-grid {:i "well" :x 0 :y 0 :w 5 :h 4 :minH 1 :minW 1}}
         [:div {:style {:height 50 :text-align "center" :font-weight "600" :font-size "large"}} "Wells Summary"]
         [well-summary-table db/well-data
                            (do
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
  (mount-root))