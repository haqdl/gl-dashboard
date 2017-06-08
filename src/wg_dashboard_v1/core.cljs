(ns wg-dashboard-v1.core
  (:require [reagent.core :as reagent :refer [atom]]
            [wg-dashboard-v1.db :as db]
            [wg-dashboard-v1.datatable :refer [DataTable]]))


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
     [:span.logo-mini "GL"]
     [:span.logo-lg "GasLift Dashboard"]]))

(defn- nav-bar []
  (fn []
    [:nav.navbar.navbar-static-top
     [:div.navbar-custom-menu
      [:ul.nav.navbar-nav]]]))

(defn main-header []
      [:div [:header.main-header
             [logo]
             [nav-bar]]
            [:div {:height 100}]])
;; -----------React-grid-layout---------------

(def default-responsive-config
  {
   :class "layout"
   :cols {:lg 12 :md 10 :sm 6 :xs 4 :xxs 2}
   :breakpoints {:lg 1200 :md 996 :sm 768 :xs 480 :xxs 0}
   :rowHeight 100
   :isResizable true
   :style {:background-color "white"}
   :onResizeStop #(.dispatchEvent js/window (js/Event. "resize"))})

(defonce RGL (aget js/window "deps" "rgl"))

(defonce ReactGridLayout (reagent/adapt-react-class RGL))

(defonce ResponsiveReactGridLayout (reagent/adapt-react-class (RGL.WidthProvider RGL.Responsive)))

;;----------------chart---------------------------

(def chart-state (atom {}))

(defn test-chart-config [data]
  (reset!  chart-state data))

(defn home-render []
  [:div {:style {:min-width "310px" :max-width "800px"
                 :height "400px" :margin "0 auto"}}])

(defn chart-display [this]
  (reagent/create-class {:reagent-render
                         (fn []
                           [:div {:style {:min-width "300px" :max-width "800px"
                                               :height "400px" :margin "0 auto"}}])
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




(defn table-display []
  (reagent/create-class {:reagent-render
                           (fn []
                             [:div {:style {:min-width "300px" :max-width "800px"
                                            :height "400px" :margin "0 auto"}}])
                         :component-did-mount
                           (fn [this]
                              (js/Handsontable (reagent/dom-node this)
                                               (clj->js (test-table-config db/table-data))))}))
                                               ;(.AddEvent (-.Dom js/Handsontable) img "mousedown" (fn [e] (.preventDefault e)))))}))

;;-------Well table--------------------------------------------------
(defn WellTable [data-source]
  (fn [data-source]
      [:table.table {:style {:margin-bottom "0px"}}
       [:thead
        [:tr
         [:th "Well"]
         [:th "Diagnostic Date"]
         [:th "GL Status"]
         [:th "GL Valves Status"]
         [:th "GLIR Calculated"]]]
       [:tbody
        [:tr
         [:td 1]
         [:td 1]
         [:td 1]
         [:td 1]
         [:td 1]]]]))
;;------------------------------------------------------------------------
(defn main-content []
      ;[:div
      [ResponsiveReactGridLayout  default-responsive-config
        [:div
         {:key "2" :data-grid {:i "table" :x 0 :y 1 :w 4 :h 4 :minH 1 :minW 1}}
         [table-display]]
         ;[WellTable db/well-data]]
        [:div
         {:key "3" :data-grid {:i "chart" :x 4 :y 1 :w 6 :h 4 :minH 1 :minW 3}}
         [chart-display]]])
;;---------------------------------------------------------

(defn home-page []
      [:div
       [main-header]
       [main-content]])



(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))


(defn ^:export init []
  (mount-root))