(ns wg-dashboard-v1.db)

;;---------highcharts--------------------

(def default-chart-config
  "The base default options we apply to HighChart in our application.
   We apply this to normalize the style of highcharts throughout our application.
   Any of the options can be overriden by the chart-config parameter passed to the
   HighChart component."
  {
   :credits {:enabled false}
   :legend  {:layout        "horizontal"
             :align         "center"
             :verticalAlign "bottom"
             :borderWidth   0}
   :yAxis {:labels {:format "{value}"}}
   :xAxis {:gridLineWidth 1
           :labels {:format "{value}"}}})

(def chart-data
  {
   :title {:text "Depth vs. Temperature/Pressure"}
   :subtitle {:text "Source: AppSmiths.com"}
   :xAxis {:title {:text "Pressure"}}
   :yAxis {:min 0
           :title {:text "Depth"
                   :align "middle"}
           :labels {:overflow "justify"}}
   :tooltip {:valueSuffix " psi"}
   :plotOptions {:series {:dataLabels {:enabled true}}}
   :legend {:layout "vertical"
            :align "center"
            :verticalAlign "bottom"
            :floating false
            :borderWidth 1
            :shadow true}
   :credits {:enabled false}
   :series [{:name "Installation"
             :data [43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175]}
            {:name "Manufacturing"
             :data [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]}
            {:name "Project Development"
             :data ["null", "null", 7988, 12169, 15112, 22452, 34400, 34227]}
            {:name "Other"
             :data [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]}]})

;;-----------Handsontable------------------------------------------------------
(def table-data
  {
   :colHeaders ["Well" "Diagnostic Date" "GL Status" "GL Valves Status" "GLIR Calculated"]
   :columnSorting true
   :columnHeaderHeight 35
   :rowHeights 30
   :data
          ;[["Well 1" "11/03/2015 8:00AM" 2 "Valves 1, 2 of 3 Inj" 0.402]
          ; ["Well 3" "11/03/2015 8:00AM" 2 "Valves 2, 3 of 3 Inj" 0.503]
          ; ["Well 5" "11/03/2015 8:00AM" 1 "Valves 1, 2 of 4 Inj" 0.908]
          ; ["Well 2" "11/03/2015 8:00AM" 1 "Valves 3, 4 of 4 Inj" 0.749]
          ; ["Well 9" "11/03/2015 8:00AM" 1 "Valve 2 of 3 Inj"     0.555]
          ; ["Well 4" "11/03/2015 8:00AM" 0 "Valve 2 of 4 Inj"     0.666]
          ; ["Well 7" "11/03/2015 8:00AM" 0 "Valves 1, 2 of 5 Inj" 0.777]
          ; ["Well 6" "11/03/2015 8:00AM" 0 "Valves 1, 2 of 4 Inj" 0.666]
          ; ["Well 8" "11/03/2015 8:00AM" 0 "Valves 1, 2 of 3 Inj" 0.888]
          ; ["Well 10" "11/03/2015 8:00AM" 0 "Valves 1, 2 of 2 Inj" 0.444]
          ; ["Well 11" "11/03/2015 8:00AM" 0 "Valves 1, 2 of 3 Inj" 0.666]]
       [{:name "Well 1" :date "11/03/2015 8:00AM" :status 2 :desciption "Valves 1, 2 of 3 Inj" :rate 0.402}
        {:name "Well 2" :date "11/03/2015 8:00AM" :status 1 :desciption "Valve 2 of 3 Inj" :rate 0.400}
        {:name "Well 3" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1, 2 of 3 Inj" :rate 0.555}
        {:name "Well 4" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 3,4 of 4 Inj" :rate 0.444}
        {:name "Well 5" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1 of 3 Inj" :rate 0.666}
        {:name "Well 6" :date "11/03/2015 8:00AM" :status 1 :desciption "Valves 2,3 of 4 Inj" :rate 0.333}
        {:name "Well 7" :date "11/03/2015 8:00AM" :status 2 :desciption "Valves 1 of 4 Inj" :rate 0.404}
        {:name "Well 8" :date "11/03/2015 8:00AM" :status 1 :desciption "Valves 1, 2 of 3 Inj" :rate 0.606}
        {:name "Well 9" :date "11/03/2015 8:00AM" :status 1 :desciption "Valves 2, 3 of 3 Inj" :rate 0.777}
        {:name "Well 10" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 2,3  of 4 Inj" :rate 0.555}
        {:name "Well 11" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1, 2 of 3 Inj" :rate 0.788}
        {:name "Well 12" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1, 2 of 3 Inj" :rate 0.345}]

   :columns
         [
          {:data "name", :renderer "html"}
          {:data "date", :renderer "html"}
          {:data "status", :renderer (js/imageRenderer)}
          {:data "desciption", :renderer "html"}
          {:data "rate", :renderer "html"}]


   :rowHeaders  false
   :contextMenu false
   :autoWrapRow true})


(def well-data
  [{:name "Well 1" :date "11/03/2015 8:00AM" :status 2 :desciption "Valves 1, 2 of 3 Inj" :rate 0.402}
   {:name "Well 2" :date "11/03/2015 8:00AM" :status 1 :desciption "Valve 2 of 3 Inj" :rate 0.400}
   {:name "Well 3" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1, 2 of 3 Inj" :rate 0.555}
   {:name "Well 4" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 3,4 of 4 Inj" :rate 0.444}
   {:name "Well 5" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1 of 3 Inj" :rate 0.666}
   {:name "Well 6" :date "11/03/2015 8:00AM" :status 1 :desciption "Valves 2,3 of 4 Inj" :rate 0.333}
   {:name "Well 7" :date "11/03/2015 8:00AM" :status 2 :desciption "Valves 1 of 4 Inj" :rate 0.404}
   {:name "Well 8" :date "11/03/2015 8:00AM" :status 1 :desciption "Valves 1, 2 of 3 Inj" :rate 0.606}
   {:name "Well 9" :date "11/03/2015 8:00AM" :status 1 :desciption "Valves 2, 3 of 3 Inj" :rate 0.777}
   {:name "Well 10" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 2,3  of 4 Inj" :rate 0.555}
   {:name "Well 11" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1, 2 of 3 Inj" :rate 0.788}
   {:name "Well 12" :date "11/03/2015 8:00AM" :status 0 :desciption "Valves 1, 2 of 3 Inj" :rate 0.345}])



