(ns wg_dashboard_v1.handlers
  (:require [wg_dashboard_v1.serverevents :as se]
            [wg_dashboard_v1.db :as mydb]))

(defn set-selected-well [well]
  (let [rawwell well
        cleanwell (dissoc rawwell :glstatus)
        welldata (->> @mydb/field-state
                      (:wells)
                      (filter #(= cleanwell (:well %))))
        welldoc (:welldoc (first welldata))]
    ;(.log js/console (str "well: " cleanwell))
    ;(.log js/console (str "welldoc: " welldoc))
    (swap! mydb/well-state assoc :current-well cleanwell)
    ;(if (some? welldoc)
    ;  (swap! mydb/well-state assoc :welldoc welldoc))))
    (se/sendAction :pickwell cleanwell)))