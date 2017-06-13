(ns wg-dashboard-v1.core
  (:require [reagent.core :as reagent :refer [atom]]
            [wg-dashboard-v1.db :as db]))

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
   :rowHeight 100
   :isResizable true
   :style {:background-color "grey"}
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
                             [:div
                              {:style {:min-width "300px" :max-width "800px"
                                       :height "500px" :overflow "hidden" :margin "0 auto"}}])
                         :component-did-mount
                           (fn [this]
                              (js/Handsontable (reagent/dom-node this)
                                               (clj->js (test-table-config db/table-data))))}))
                              ;(.AddEvent (js/windows  "img" "mousedown" (fn [e] (.log "fired")))))}))

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
;;------------------------------------------------------------------------
(defn main-content []
      ;[:div
      [ResponsiveReactGridLayout  default-responsive-config
        [:div
         {:key "2" :data-grid {:i "well" :x 0 :y 0 :w 2 :h 4 :minH 1 :minW 1}}
         [:div {:style {:height 50 :text-align "center" :font-weight "600" :font-size "large"}} "Wells Summary"]
         [well-table-display]]
        [:div
         {:key "3" :data-grid {:i "chart" :x 5 :y 0 :w 6 :h 4 :minH 1 :minW 3}}
         [chart-display]]
        [:div
         {:key "4" :data-grid {:i "welltest" :x 0 :y 3 :w 6 :h 2 :minH 1 :minW 1}}
         [:div {:style {:height 50 :text-align "center" :font-weight "600" :font-size "large"}} "Well Test History"]
         [welltest-table-display]]])


;;---------------------------------------------------------

(defn home-page []
      [:div
       [main-header]
       [main-content]])


(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))


(defn ^:export init []
  (mount-root))