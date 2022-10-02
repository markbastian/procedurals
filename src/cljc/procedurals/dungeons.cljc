(ns procedurals.dungeons)

;http://www.gamasutra.com/blogs/AAdonaac/20150903/252889/Procedural_Dungeon_Generation_Algorithm.php

(defn random-point [radius]
  (let [t (* 2.0 Math/PI (rand))
        u (+ (rand) (rand))
        r (if (> u 1) (- 2 u) u)]
    [(* radius r (Math/cos t)) (* radius r (Math/sin t))]))

(defn roundm [n m]
  (int (* (Math/floor (/ (dec (+ n m)) m)) m)))

(defn random-int-point [radius tile-size]
  (let [t (* 2.0 Math/PI (rand))
        u (+ (rand) (rand))
        r (if (> u 1) (- 2 u) u)]
    [(roundm (* radius r (Math/cos t)) tile-size)
     (roundm (* radius r (Math/sin t)) tile-size)]))