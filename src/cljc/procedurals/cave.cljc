(ns procedurals.cave)

(defn prngrid [grid]
  (doseq [row grid] (prn (apply str (map {:wall "#" :floor " "} row)))))

(defn init [w h] (vec (repeat h (vec (repeat w :wall)))))

(defn mark-floor [grid cell]
  (cond-> grid (get-in grid cell) (assoc-in cell :floor)))

(defn rand-step [from]
  (mapv + from (rand-nth [[0 1] [0 -1] [1 0] [-1 0]])))

(defn step [grid start]
  (first (filter (partial get-in grid) (repeatedly #(rand-step start)))))

(defn wander-cave [start grid iterations]
  (reduce mark-floor grid (take iterations (iterate (partial step grid) start))))

(defn cave-step [{:keys [loc grid] :as m}]
  (let [n (step grid loc)]
    (-> m (assoc :loc n) (update :grid mark-floor n))))

(defn wander-cave2 [start grid iterations]
  (:grid (nth (iterate cave-step {:loc start :grid grid}) iterations)))

(defn neighbors [[x y]]
  [[(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]])

(defn wander [{:keys [frontier grid] :as m}]
  (let [n (rand-nth (vec frontier))]
    (-> m
        (assoc-in (into [:grid] n) :floor)
        (update :frontier disj n)
        (update :frontier into (filter #(= :wall (get-in grid %)) (neighbors n))))))

(defn frontier-cave [start grid iterations]
  (:grid (nth (iterate wander {:frontier #{start} :grid grid}) iterations)))

(defn ca-grid [w h n]
  (random-sample n (for [i (range w) j (range h)] [i j])))

(defn nine-grid [[x y]]
  (map vector
       ((juxt identity inc inc identity dec dec dec identity inc) x)
       ((juxt identity identity inc inc inc identity dec dec dec) y)))

(defn ca-cave-step [grid]
  (->> grid
       (mapcat nine-grid)
       frequencies
       (filter (fn [[_ c]] (> c 4)))
       (map first)))

(def ca-cave-iterator #(iterate ca-cave-step %))

(defn ca-cave [w h pct iterations]
  (reduce mark-floor (init w h) (nth (ca-cave-iterator (ca-grid w h pct)) iterations)))

;(prngrid (ca-cave 32 32 0.45 18))
;(prngrid (wander-cave [16 16] (init 32 32) 160))
;(prngrid (wander-cave2 [16 16] (init 32 32) 160))
;(prngrid (frontier-cave [16 16] (init 32 32) 500))