(ns land-of-yure.dungeon
  (:require [lanterna.screen :as s]))

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
  [screen]
  (let [
        height (+ (rand-int 30) 10)
        width (+ (rand-int 30) 10)
        screen-size (s/get-size screen)
        x (+ 5 (rand-int (- (get screen-size 0) 20)))
        y (+ 5 (rand-int (- (get screen-size 1) 20)))]
    {:height height
     :width width
     :x x
     :y y}))

(defn print-room
  [{:keys [screen height width x y]}]
  (doseq [row (range (int height))
          col (range width)]
    (cond
      (generate-v-walls? row col width height) (s/put-string screen (+ x col) (+ y row) "║")
      (generate-h-walls? row height) (s/put-string screen (+ x col) (+ y row) "═")
      :else (s/put-string screen (+ x col) (+ y row) "."))))

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


(defn print-v-corridor
  [screen c]
  (let [max-y (max (:y-start c) (:y-end c))
        min-y (min (:y-start c) (:y-end c))]
    (doseq [y (range min-y max-y)]
      (s/put-string screen (:x-end c) y "."))))

(defn print-h-corridor
  [screen c]
  (let [max-x (max (:x-start c) (:x-end c))
        min-x (min (:x-start c) (:x-end c))]
    (doseq [x (range min-x max-x)]
      (s/put-string screen x (:y-start c) "."))))



(defn print-c
  [screen c]
  (print-h-corridor screen c)
  (print-v-corridor screen c))


(defn print-corridors
  [{:keys [corridors screen]}]
  (mapv #(print-c screen %) corridors))

(defn reduce-corridors
  [centers n]
  (let [next-iteration (next centers)
        current-room (first centers)
        next-room (second centers)]
    (if (not (nil? next-room))
      (recur next-iteration (conj n {:x-start (:x current-room)
                                     :y-start (:y current-room)
                                     :x-end (:x next-room)
                                     :y-end (:y next-room)}))
      n)))


(defn generate-dungeon
  [{:keys [screen qty rooms]}]
  (if (> qty 0)
    (let [current-room (generate-room screen)
          is-colliding? (are-rooms-colliding? rooms current-room)
          to-gen (cond is-colliding? qty
                       :else (- qty 1))
          room-list (cond is-colliding? rooms
                          :else (conj rooms current-room))]
      (when (not is-colliding?)
        (print-room {:screen screen
                     :height (:height current-room)
                     :width (:width current-room)
                     :x (:x current-room)
                     :y (:y current-room)}))
      (recur {:screen screen
              :qty to-gen
              :rooms room-list}))
    (let [rooms-center (find-centers rooms)
          corridors (reduce-corridors rooms-center [])]
      (print-corridors {:corridors corridors
                        :screen screen}))))