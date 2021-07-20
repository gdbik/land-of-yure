(ns land-of-yure.core
  (:require [lanterna.screen :as s]
            [land-of-yure.const :refer [MAP_SIZE MAP_COL MAP_ROW]]
            [land-of-yure.dungeon :refer [generate-dungeon print-dungeon]]
            [land-of-yure.state :refer [player-pos game-map]])
  (:gen-class))


(defn get-k
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
      (= k \w) (reset! player-pos go-up)
      (= k \s) (reset! player-pos go-down)
      (= k \a) (reset! player-pos go-left)
      (= k \d) (reset! player-pos go-right))))

(defn game-loop
  [screen]
  ((s/move-cursor screen (get @player-pos 0) (get @player-pos 1))
   (print-dungeon screen)
   (s/put-string screen (get @player-pos 0) (get @player-pos 1) "@")
   (s/put-string screen 0 0 "The Land Of Yure - Dev Build")
   (s/put-string screen 0 1 "[action goes here]")
   (s/put-string screen 0 2 "[action goes here]")
   (s/redraw screen)
   (get-k screen)
   (s/clear screen)
   (game-loop screen)))

(defn main
  [screen-type]
  (let [screen (s/get-screen screen-type {:cols MAP_COL
                                          :rows MAP_ROW
                                          :font-size 16})]
    (s/in-screen screen
                 (generate-dungeon {:screen screen
                                    :rooms []
                                    :qty 5})
                 (game-loop screen))))

(defn -main
  [& args]
  (let [args (set args)
        screen-type :swing]
    (println "Land Of Yure Init")
    (main screen-type)))
