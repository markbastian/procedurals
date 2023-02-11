(ns procedurals.terrain
  (:require #?(:clj [clojure.java.io :as io]))
  (:import #?(:clj (java.awt Color))
           #?(:clj (java.awt.image BufferedImage))
           #?(:clj (javax.imageio ImageIO))))

;https://danielbeard.wordpress.com/2010/08/07/terrain-generation-and-smoothing/

(defn double-indices-1d [cells]
  (zipmap (map #(* 2 %) (keys cells)) (vals cells)))

(defn double-indices-2d [{:keys [cells] :as m}]
  (-> m
      (assoc :cells (zipmap (map (fn [[x y]] [(* 2 x) (* 2 y)]) (keys cells)) (vals cells)))
      (update :width * 0.5)))

(defn diamond [{:keys [cells width dim] :as m}]
  (let [dim (+ dim (dec dim))]
    (-> m
        (assoc :dim dim)
        (update :cells into (for [i (range 1 dim 2) j (range 1 dim 2)
                                  :let
                                  [is ((juxt inc dec dec inc) i)
                                   js ((juxt inc inc dec dec) j)
                                   c (map vector is js)
                                   x (* 0.5 (reduce + (map cells c)))]]
                              [[i j] (+ x (* width (dec (* 2.0 (rand)))))])))))

(defn square [{:keys [cells width dim] :as m}]
  (update m :cells into
          (for [i (range 0 dim) j (range 0 dim)
                :when (not (cells [i j]))
                :let
                [is ((juxt inc identity dec identity) i)
                 js ((juxt identity inc identity dec) j)
                 c (map vector is js)
                 x (* 0.5 (reduce + (filter identity (map cells c))))]]
            [[i j] (+ x (* width (dec (* 2.0 (rand)))))])))

(def step (comp square diamond double-indices-2d))

#?(:clj (defn create-image-map [{:keys [cells dim]}]
          (let [img (BufferedImage. dim dim BufferedImage/TYPE_INT_RGB)
                lo (apply min (map second cells))
                hi (apply max (map second cells))]
            (doseq [[[i j] v] cells
                    :let [c (float (/ (- v lo) (- hi lo)))]]
              (.setRGB img i j (.getRGB (Color. c c c))))
            (ImageIO/write img "png" (io/file "img.png")))))

(comment
  (def a {:cells {[0 0] 0.0 [0 1] 0.0 [1 1] 0.0 [1 0] 0.0}
          :width 1.0
          :dim   2})

  (def b (double-indices-2d a))
  (prn b)
  (def c (diamond b))
  (prn c)
  (def d (square c))
  (prn d)
  (prn (count d))

  ;21? <- not right.
  (-> d step step step step step step step step create-image-map))
