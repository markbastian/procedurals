(ns procedurals.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [procedurals.cave :as c]
            [cljs.reader :refer [read-string]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn gen-grid [{:keys [w h i p]}]
  (vec (take 64 (c/ca-cave-iterator (c/ca-grid w h (* p 0.01))))))

(defn update-grid [m]
  (assoc m :caves (gen-grid m)))

(defn render [state]
  (let [{:keys [w h i p caves]} @state
        cw 10
        grid (reduce c/mark-floor (c/init w h) (nth caves i))]
    [:div
     [:svg {:width (* w cw) :height (* h cw)}
      (doall (for [ix (range w) j (range h)]
               [:rect { :key (str ix ":" j) :x (* ix cw) :y (* j cw)
                       :width cw :height cw
                       :stroke :red :fill (case (get-in grid [ix j])
                                            :wall :blue
                                            :floor :green)}]))]
     [:div
      [:input { :type :range :min 0 :max 63 :onChange (fn [e] (swap! state assoc :i (-> e .-target .-value read-string)))} "i"]
      [:input { :type :range :min 0 :max 100 :onChange (fn [e] (swap! state assoc :p (-> e .-target .-value read-string)))} "p"]
      [:input { :type :range :min 1 :max 64 :value w :onChange #(swap! state assoc :w (-> % .-target .-value read-string))} "w"]
      [:input { :type :range :min 1 :max 64 :value h :onChange #(swap! state assoc :h (-> % .-target .-value read-string))} "h"]]
     ;[:button #(prn (with-out-str (pprint state))) "dump state"]
     ]))


(when-let [app-context (. js/document (getElementById "app"))]
  (let [state (atom (update-grid {:w 32 :h 32 :i 18 :p 45}))]
    (add-watch state :grid-watch (fn [_ _ o n]
                                   (when (not= o n)
                                     (update-grid n))))
    (reagent/render-component
      [render state]
      (do
        app-context))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
