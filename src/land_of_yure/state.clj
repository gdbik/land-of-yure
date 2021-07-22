(ns land-of-yure.state
  (:require [land-of-yure.const :refer [MAP_SIZE MAP_COL]]))

(def game-map (atom (vec (repeat MAP_SIZE ""))))
(def player-pos (atom (vec [0,0])))

(defn calculate-coord
  [x y]
  (+ x (* y MAP_COL)))


(defn put-tile-on-map!
  [x y tile]
  (let [array-coord (calculate-coord x y)
        new-map-atom (assoc @game-map array-coord tile)]
    (reset! game-map new-map-atom)))

(defn get-tile
  [x y]
  (let [array-coord (calculate-coord x y)]
    (:tile (get @game-map array-coord))))

(defn get-tile-object
  [x y]
  (let [array-coord (calculate-coord x y)]
    (get @game-map array-coord)))

(defn reset-game-map
  []
  (reset! game-map (vec (repeat MAP_SIZE "")))
  (reset! player-pos (vec [0, 0])))

