(ns land-of-yure.player
  (:require [lanterna.screen :as s]
            [land-of-yure.state :refer [player-pos
                                        reset-game-map
                                        get-tile-object]]
            [land-of-yure.const :refer [FLOOR CORRIDOR]]
            [land-of-yure.const :refer [ROOM_QTY]]
            [land-of-yure.dungeon :refer [generate-dungeon]]))

(defn clear-and-gen
  [screen]
  (comp
    (reset-game-map)
    (generate-dungeon {:screen screen
                       :qty ROOM_QTY
                       :rooms []})))

(defn can-move?
  [[x y]]
  (let [next-tile (get-tile-object x y)
        walkable? (:walkable next-tile)]
    (println next-tile)
    walkable?))

(defn player-movement
  [screen]
  (let [k (s/get-key-blocking screen)
        pos @player-pos
        x-pos (get pos 0)
        y-pos (get pos 1)
        go-up (assoc pos 1 (- y-pos 1))
        go-down (assoc pos 1 (+ y-pos 1))
        go-left(assoc pos 0 (- x-pos 1))
        go-right(assoc pos 0 (+ x-pos 1))]
    (cond
      (= k \k) (when (can-move? go-up)
                 (reset! player-pos go-up))
      (= k \j) (when (can-move? go-down)
                 (reset! player-pos go-down))
      (= k \h) (when (can-move? go-left)
                 (reset! player-pos go-left))
      (= k \l) (when (can-move? go-right)
                 (reset! player-pos go-right))
      (= k \e) (clear-and-gen screen))))
