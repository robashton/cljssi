(ns game)

(defn context [width height]
  (let [target (.getElementById js/document "target")]
    [(.getContext target "2d") 
      (set! (. target -width) width)
      (set! (. target -height) height)]))

(defn clear-screen [[ctx width height]]
  (set! (. ctx -fillStyle) "#000")
  (.fillRect ctx 0 0 width height))

(defn tick [ctx state]
  (clear-screen ctx) 
  (js/setTimeout (fn []
    (tick ctx {} )) 1000))

(defn ^:export init []
  (let [ctx (context 640 480)] 
    (tick ctx {})))
