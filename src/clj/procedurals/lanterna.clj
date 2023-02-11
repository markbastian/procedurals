(ns procedurals.lanterna
  (:require [procedurals.cave :as cave]
            [clojure.core.async :as ca]
            [lanterna.screen :as s]))

(defonce scr (s/get-screen))

(s/start scr)

;; TODO - Connect before other steps
(def cave-data (cave/connect (cave/ca-caverns 64 32 0.45 50)))
(def floor-cells (cave/floor-coords cave-data))
(def start (rand-nth (vec floor-cells)))
(def cave (cave/grid-data->ascii-lines cave-data))

(let [[row col] start]
  (s/clear scr)
  (doseq [row-index (range (count cave))]
    (s/put-string scr 0 row-index (cave row-index)))
  (s/move-cursor scr col row)
  (s/redraw scr)
  (ca/go-loop [k (s/get-key scr) [row col :as c] start]
    (ca/timeout 100)
    (if k
      (let [[row' col' :as c'] (case k
                                 :up [(dec row) col]
                                 :down [(inc row) col]
                                 :right [row (inc col)]
                                 :left [row (dec col)]
                                 :escape nil
                                 (do (println k) c))]
        (cond
          (floor-cells c')
          (do
            (s/move-cursor scr col' row')
            (s/redraw scr)
            (recur (s/get-key scr) c'))
          (nil? c') (do
                      (println "End input")
                      (s/stop scr))
          :else (recur (s/get-key scr) c)))
      (recur (s/get-key scr) c))))

;(s/stop scr)
