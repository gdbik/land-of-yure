(ns land-of-yure.core
  (:require [lanterna.screen :as s]
            [land-of-yure.const :refer [MAP_SIZE
                                        MAP_COL
                                        MAP_ROW
                                        ROOM_QTY]]
            [land-of-yure.dungeon :refer [generate-dungeon print-dungeon]]
            [land-of-yure.state :refer [player-pos
                                        game-map
                                        reset-game-map]]
            [land-of-yure.player :refer [player-movement]])
  (:gen-class))


(defn game-loop
  [screen]
  ((s/move-cursor screen (get @player-pos 0) (get @player-pos 1))
   (print-dungeon screen)
   (s/put-string screen (get @player-pos 0) (get @player-pos 1) "@")
   (s/put-string screen 0 0 "The Land Of Yure - Dev Build")
   (s/put-string screen 0 1 "[action goes here]")
   (s/put-string screen 0 2 "[action goes here]")
   (s/redraw screen)
   (player-movement screen)
   (s/clear screen)
   (game-loop screen)))

(defn main
  [screen-type]
  (let [screen (s/get-screen screen-type {:cols MAP_COL
                                          :rows MAP_ROW
                                          :font-size 18})]
    (s/in-screen screen
                 (generate-dungeon {:screen screen
                                    :rooms []
                                    :qty ROOM_QTY})
                 (game-loop screen))))

(defn -main
  [& args]
  (let [args (set args)
        screen-type :swing]
    (println "Land Of Yure Init")
    (main screen-type)))
