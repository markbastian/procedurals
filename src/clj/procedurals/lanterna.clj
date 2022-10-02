(ns procedurals.lanterna
  (:require [clojure.core.async :as ca]
            [lanterna.screen :as s]
            [procedurals.cave :as cave]))

(defonce scr (s/get-screen))

(s/start scr)

;; TODO - Connect before other steps
(def cave-data (cave/ca-cave 64 32 0.45 50))
(def floor-cells (cave/floor-coords cave-data))
(def start (rand-nth (vec floor-cells)))
(def cave (cave/connect-cavern cave-data))

(let [[row col] start]
  (s/clear scr)
  (doseq [row-index (range (count cave))]
    (s/put-string scr 0 row-index (cave row-index)))
  (s/put-string scr col row "@")
  (s/redraw scr)
  (ca/go-loop [k (s/get-key scr) [row col :as c] start]
    (if k
      (let [[row' col' :as c'] (case k
                                 :up [(dec row) col]
                                 :down [(inc row) col]
                                 :right [row (inc col)]
                                 :left [row (dec col)]
                                 (println k))]
        (println k)
        (println start)
        (println c)
        (println c')
        (if (floor-cells c')
          (do
            (s/put-string scr col row " ")
            (s/put-string scr col' row' "@")
            (s/redraw scr)
            (recur (s/get-key scr) c'))
          (recur (s/get-key scr) c)))
      (recur (s/get-key scr) c))))

;(s/stop scr)