(ns cljsinvaders.game)

(def canvas-width 640)
(def canvas-height 480)

(defn log [& v]
  (.log js/console v))

(defprotocol Entity
  (tick [this])
  (render [this ctx]))

(defrecord Enemy [x y]
  Entity
  (tick [this] this)
  (render [this ctx]
    (log x y 20 20)
    (set! (. ctx -fillStyle) "#FF0")
    (.fillRect ctx x y 20 20)))

(defn create-enemies []
  (for [x (range 0 480 60)
        y (range 0 240 60)]
    (Enemy. x y)))

(defn create-state [] 
  {
   :entities (create-enemies)                     
  })

(defn context [width height]
  (let [target (.getElementById js/document "target")
        context (.getContext target "2d")]
    (set! (. target -width) width)
    (set! (. target -height) height)
    context))

(defn clear-screen [ctx]
  (set! (. ctx -fillStyle) "#000")
  (.fillRect ctx 0 0 canvas-width canvas-height))

(defn tick [ctx {:keys [entities] :as state}]
  (log entities)
  (clear-screen ctx) 
  (doseq [e entities] (render e ctx))
  (js/setTimeout (fn []
    (tick ctx state )) 1000))

(defn ^:export init []
  (let [ctx (context canvas-width canvas-height)] 
    (tick ctx (create-state))))
