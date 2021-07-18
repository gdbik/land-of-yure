(ns land-of-yure.core
  (:require [lanterna.screen :as s]
            [land-of-yure.dungeon :refer [generate-dungeon]])
  (:gen-class))


(defn game-loop
  "Hellos"
  [screen]
  ((s/clear screen)
   (generate-dungeon {:screen screen
                      :rooms []
                      :qty 10})
   (s/put-string screen 0 0 "Land Of Yure 0.1")
   (s/put-string screen 0 1 "Press any key to generate a new dungeon")
   (s/put-string screen 0 2 "Press ALT-F4/⌘-Q to quit")
   (s/redraw screen)
   (s/get-key-blocking screen)
   (game-loop screen)))

(defn main
  [screen-type]
  (let [screen (s/get-screen screen-type)]
    (s/in-screen screen
                 (game-loop screen))))

(defn -main
  [& args]
  (let [args (set args)
        screen-type (cond
                       (args :swing) :swing
                       (args :text) :text
                       :else :auto)]
    (println "Land Of Yure Init")
    (main screen-type)))
