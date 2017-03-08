(ns procedurals.terrain)

;https://danielbeard.wordpress.com/2010/08/07/terrain-generation-and-smoothing/

(defn double-indices-1d [cells]
  (zipmap (map #(* 2 %) (keys cells)) (vals cells)))

(defn double-indices-2d [cells]
  (zipmap (map (fn [[x y]][(* 2 x) (* 2 y)]) (keys cells)) (vals cells)))

(defn diamond [cells width]
  (into cells (let [dim (dec (* 2 (Math/sqrt (count cells))))]
                (for [i (range 1 dim 2) j (range 1 dim 2) :let
                      [is ((juxt inc dec dec inc) i)
                       js ((juxt inc inc dec dec) j)
                       c (map vector is js)
                       x (* 0.5 (reduce + (map cells c)))]]
                  [[i j] (+ x (* width (dec (* 2.0 (rand)))))]))))

(defn square [cells width]
  (into cells (let [dim (Math/sqrt (count cells))]
                (for [i (range 0 dim) j (range 0 dim) :when (not (cells [i j])) :let
                      [is ((juxt inc identity dec identity) i)
                       js ((juxt identity inc identity dec) j)
                       c (map vector is js)
                       x (* 0.5 (reduce + (filter identity (map cells c))))]]
                  [[i j] (+ x (* width (dec (* 2.0 (rand)))))]))))

(defn gap-fill [cells width]
  (for [i (range 1 (dec (* 2 (count cells))) 2)
        :let [x (* 0.5 (reduce + (map cells ((juxt inc dec) i))))]]
    [i (+ x (* width (dec (* 2.0 (rand)))))]))

(defn divide [{ :keys [width cells]}]
  (let [c (double-indices-1d cells)
        gaps (gap-fill c width)]
    { :width (* 0.5 width) :cells (into c gaps)}))

(defn generate [ic steps] (nth (iterate divide ic) steps))

;(def a { [0 0] 0.0 [0 1] 0.0 [1 1] 0.0 [1 0] 0.0 })
;(def b (double-indices-2d a))
;(prn b)
;(def c (diamond b 1.0))
;(prn c)
;(def d (square c 1.0))
;(prn d)
;(prn (count d))
;
;;21? <- not right.
;(-> d double-indices-2d (diamond 1.0) (square 1.0) count prn)
