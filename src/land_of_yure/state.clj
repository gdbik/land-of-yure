(ns land-of-yure.state
  (:require [land-of-yure.const :refer [MAP_SIZE MAP_COL]]))

(def game-map (atom (vec (repeat MAP_SIZE ""))))
(def player-pos (atom (vec [0,0])))


(defn put-tile-on-map!
  [x y tile]
  (let [array-coord (+ x (* y MAP_COL))
        new-map-atom (assoc @game-map array-coord tile)]
    (reset! game-map new-map-atom)))

