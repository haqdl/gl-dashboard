(ns wg_dashboard_v1.router.router
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [wg_dashboard_v1.db :as mydb]
            [wg_dashboard_v1.handlers :as mainpagehandler]))

(defn- hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn init-routes []
  (.log js/console "begin route")
  (defroute "/" []

            (swap! mydb/well-state assoc :current-page :home-page))

  (defroute "*" []

            (swap! mydb/well-state assoc :current-page :page404))

  (hook-browser-navigation!))
