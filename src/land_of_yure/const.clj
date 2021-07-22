(ns land-of-yure.const)


(def MAP_ROW 25)
(def MAP_COL 85)
(def MAP_SIZE (* MAP_COL MAP_ROW))
(def ROOM_QTY 5)

(def WALL_V {:tile "│"
             :walkable false})
(def WALL_H {:tile "─"
             :walkable false})
(def DOOR {:tile "+"
           :walkable true})
(def FLOOR {:tile "."
            :walkable true
            :type :floor})
(def CORRIDOR {:tile "#"
               :walkable true
               :type :floor})
(def DEBUG {:tile "à"
            :walkable true})
