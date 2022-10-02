(ns procedurals.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [procedurals.dungeon-viewer :as pdv]
            [procedurals.dungeon-generator :as pdg]
            [procedurals.cave :as c]
            [cljs.reader :refer [read-string]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn gen-grid [{:keys [w h i p]}]
  (vec (take 64 (c/ca-cave-iterator (c/ca-grid w h (* p 0.01))))))

(defn update-grid [m]
  (assoc m :caves (gen-grid m)))

(when-let [app-context (. js/document (getElementById "app"))]
  (let [state (atom (update-grid {:w 32 :h 32 :i 18 :p 45}))]
    (add-watch state :grid-watch (fn [_ _ o n]
                                   (when (not= o n)
                                     (update-grid n))))
    (reagent/render-component
      [pdg/render state]
      (do
        app-context))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
