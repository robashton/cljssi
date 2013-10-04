(ns cljsinvaders.game)

(def canvas-width 640)
(def canvas-height 480)
(def key-states (atom {}))

(defn is-left-pressed [] (@key-states 37))
(defn is-right-pressed [] (@key-states 39))
(defn is-fire-pressed [] (@key-states 32))

(defn log [& v]
  (.log js/console v))

(defn move-left [rect amount]
  (assoc rect :x (- (:x rect) amount)))

(defn move-right [rect amount]
  (assoc rect :x (+ (:x rect) amount)))

(defn move-up [rect amount]
  (assoc rect :y (- (:- rect) amount)))

(defn draw-rect [ctx x y w h colour]
  (set! (. ctx -fillStyle) colour) 
  (.fillRect ctx x y w h))

(defn rect-left [r] (:x r))
(defn rect-right [r] (+ (:w r) (:x r)))

(defn context [width height]
  (let [target (.getElementById js/document "target")
        context (.getContext target "2d")]
    (set! (. target -width) width)
    (set! (. target -height) height)
    context))

(defn clear-screen [ctx]
  (set! (. ctx -fillStyle) "#000")
  (.fillRect ctx 0 0 canvas-width canvas-height))

(defprotocol Entity
  (tick [this])
  (handle-event [this event])
  (render [this ctx]))

(defrecord Enemy [x y w h direction]
  Entity
  (tick [this] (move-right this direction))
  (handle-event [this event] 
    (case event
      :enemy-direction-changed (assoc this :direction (- 0 direction)
                                           :y (+ 10 y))
      :else this))
  (render [this ctx] (draw-rect ctx x y w h "#00F")))

(defrecord Player [x y w h]
  Entity
  (tick [this] 
    (cond (is-left-pressed) (move-left this 2)
          (is-right-pressed) (move-right this 2) 
          :else this))
  (handle-event [this event] this)
  (render [this ctx] (draw-rect ctx x y w h "#FF0")))

(defrecord Bullet [x y w h]
  Entity
  (tick [this] (move-up this 2))
  (handle-event [this event] this)
  (render [this ctx] (draw-rect ctx x y w h "#F00")))

(defn create-enemies []
  (for [x (range 0 480 60)
        y (range 0 240 60)]
    (Enemy. x y 20 20 1)))

(defn create-player []
  (Player. 200 430 20 20))

(defn create-bullet [player]
  (Bullet. (:x player) (:y player) 5 5))

(defn create-state [] 
  {
   :last-firing-ticks 0
   :entities (cons 
               (create-player) 
               (create-enemies)) 
   })

(defn get-enemies [entities] (filter (partial instance? Enemy) entities ))
(defn get-player [entities] (first (filter (partial instance? Player) entities )))

(defn apply-event [event {:keys [entities] :as state}]
  (assoc state :entities (map #(handle-event %1 event) entities)))

(defn check-enemy-direction [{:keys [entities] :as state}]
  (let [enemies (get-enemies entities)
        min-left (apply min (map rect-left enemies))
        max-right (apply max (map rect-right enemies)) ]
    (if (or (< 640 max-right) (> 0 min-left))
      (apply-event :enemy-direction-changed state) state)))

(defn can-fire [state]
  (= (:last-firing-ticks state) 0))

(defn try-firing [{:keys [entities last-firing-ticks] :as state}]
  (if (and (is-fire-pressed) (= last-firing-ticks 0))
    (assoc state :entities (conj entities (create-bullet (get-player entities)))
                 :last-firing-ticks 1) 
    state))


(defn get-next-state [state]
  (-> state
    (update-in [:entities] (partial map tick))  
    (check-enemy-direction)
    (try-firing)))

(defn game-tick [ctx {:keys [entities] :as state}]
  (clear-screen ctx) 
  (doseq [e entities] (render e ctx))
  (js/setTimeout (fn []
    (game-tick ctx (get-next-state state) )) 33))

(defn ^:export init []
  (hook-input-events)
  (let [ctx (context canvas-width canvas-height)] 
    (game-tick ctx (create-state))))


(defn hook-input-events []
  (.addEventListener js/document "keydown" 
   (fn [e]
    (set-key-state (. e -keyCode) true)
     false))
  (.addEventListener js/document "keyup" 
   (fn [e]
    (set-key-state (. e -keyCode) false)
     false)))

(defn set-key-state [code, value]
  (swap! key-states assoc code value))
