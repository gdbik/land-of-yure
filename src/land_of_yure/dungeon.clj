(ns land-of-yure.dungeon
  (:require [lanterna.screen :as s]
            [land-of-yure.const :refer [MAP_SIZE
                                        MAP_ROW
                                        MAP_COL
                                        WALL_H
                                        WALL_V
                                        CORRIDOR
                                        DOOR
                                        FLOOR]]
            [land-of-yure.state :refer [put-tile-on-map!
                                        player-pos
                                        game-map
                                        get-tile
                                        get-tile-object]]))


(defn collision?
  [{:keys [current-room previous-room]}]
  (if (not (nil? current-room))
    (let [left (< (+ (:x current-room) (:width current-room)) (:x previous-room))
          right (> (:x current-room) (+ (:x previous-room) (:width previous-room)))
          top (< (+ (:y current-room) (:height current-room)) (:y previous-room))
          bottom (> (:y current-room) (+ (:y previous-room) (:height previous-room)))]
      (not (or left right top bottom)))
    false))

(defn are-rooms-colliding?
  [rooms current-room]
  (some true? (map #(collision? {:current-room current-room :previous-room %}) rooms)))

(defn generate-v-walls?
  [row col width height]
  (and (and (> row 0) (< row (- height 1)))
       (or (= col 0) (= col (- width 1)))))

(defn generate-h-walls?
  [row height]
  (or (= row 0)
      (= row (- height 1))))

(defn generate-room
  []
  (let [
        height (+ (rand-int 5) 5)
        width (+ (rand-int 10) 5)
        x (rand-int (- MAP_COL width))
        y (+ 3 (rand-int (- MAP_ROW (* height 2))))]        ;; just to be sure we don't go
                                                            ;; out of bound on the Y axis
    {:height height
     :width width
     :x x
     :y y}))

(defn add-element!
  [{:keys [tile x y]}]
   (put-tile-on-map! x y tile))

(defn add-room
  [{:keys [height width x y]}]
  (doseq [row (range (int height))
          col (range width)]
    (let [x-cord (+ x col)
          y-cord (+ y row)
          put-tile #(add-element! {:tile %
                                   :x x-cord
                                   :y y-cord})]
      (cond
        (generate-v-walls? row col width height) (put-tile WALL_V)
        (generate-h-walls? row height) (put-tile WALL_H)
        :else (put-tile FLOOR)))))

(defn find-center
  [room]
  (let [x-center (int (+ (/ (:width room) 2) (:x room)))
        y-center (int (+ (/ (:height room) 2 ) (:y room))) ]
    (when (and (not (nil? x-center))
               (not (nil? y-center)))
      {:x x-center
       :y y-center})))

(defn find-centers
  [rooms]
  (mapv #(find-center %) rooms))

(defn add-v-corridor
  [c]
  (let [max-y (max (:y-start c) (:y-end c))
        min-y (min (:y-start c) (:y-end c))
        x (:x-end c)]
    (doseq [y (range min-y max-y)]
      (let [current-tile (get-tile x y)
            next-tile (get-tile x (+ y 1))
            next-two-tile (get-tile x (+ y 2))]
        (when (not (= current-tile (:tile FLOOR)))
          (put-tile-on-map! x y CORRIDOR))))))

(defn add-h-corridor
  [c]
  (let [max-x (max (:x-start c) (:x-end c))
        min-x (min (:x-start c) (:x-end c))
        y (:y-start c)
        y-cord (atom y)]
    (doseq [x (range min-x (+ 1 max-x) )]
      (let [current-tile (get-tile x @y-cord)
            next-tile-x (get-tile (+ 1 x) @y-cord)]
        (when (= next-tile-x (:tile WALL_H))
          (comp
            (put-tile-on-map! x @y-cord CORRIDOR)
            (reset! y-cord (- y 1))))
        (when (not (= current-tile (:tile FLOOR)))
          (if (= current-tile (:tile WALL_V))
            (put-tile-on-map! x @y-cord DOOR)
            (comp
              (put-tile-on-map! x @y-cord CORRIDOR))))))))


(defn add-c!
  [c]
  (add-h-corridor c)
  (add-v-corridor c))

(defn add-corridors
  [corridors]
  (mapv add-c! corridors))

(defn reduce-corridors
  [centers n]
  (let [next-iteration (next centers)
        current-room (first centers)
        next-room (second centers)]
    (if (not (nil? next-room))
      (recur next-iteration (conj n {:x-start (:x current-room)
                                     :y-start (:y current-room)
                                     :x-end (- (:x next-room) 1)
                                     :y-end (+ 1 (:y next-room))}))
      n)))

(defn print-dungeon
  [screen]
  (doseq [y (range MAP_ROW)
          x (range MAP_COL)]
    (let [tile (:tile (get @game-map (+ (* MAP_COL y) x)))]
      (s/put-string screen x y (str tile)))))

(defn position-player!
  [rooms]
  (let [rng (rand-int (- (count rooms) 1))
        random-room (get rooms rng)
        x (+ (:x random-room) (rand-int (:width random-room)))
        y (+ (:y random-room) (rand-int (:height random-room)))
        random-tile (get-tile-object x y)
        is-floor? (= (:type random-tile) :floor)]
      (if is-floor?
        (reset! player-pos (vec [x, y]))
        (recur rooms))))

(defn generate-dungeon
  [{:keys [screen qty rooms]}]
  (if (> qty 0)
    (let [current-room (generate-room)
          is-colliding? (are-rooms-colliding? rooms current-room)
          to-gen (cond is-colliding? qty
                       :else (- qty 1))
          room-list (cond is-colliding? rooms
                          :else (conj rooms current-room))]
      (when (not is-colliding?)
        (add-room {:height (:height current-room)
                   :width (:width current-room)
                   :x (:x current-room)
                   :y (:y current-room)}))
      (recur {:screen screen
              :qty to-gen
              :rooms room-list}))
    ;; else
    (let [rooms-center (find-centers rooms)
          corridors (reduce-corridors rooms-center [])]
      (add-corridors corridors)
      (print-dungeon screen)
      (position-player! rooms))))