(ns binclock.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))
(enable-console-print!)

(extend-type boolean
  ICloneable
  (-clone [b] (js/Boolean. b)))

(extend-type number
  ICloneable
  (-clone [n] (js/Number. n)))

(defn get-time
  "current time as a map"
  []
  (let [d (js/Date.)]
    {:hours (.getHours d)
     :minutes (.getMinutes d)
     :seconds (.getSeconds d)}))

(defn decimal-parts
  "split a 1 or 2 digit number into its decimal parts
   e.g. 53 => [5 3], 9 => [0 9]"
  [n]
  [(quot n 10) (mod n 10)])

(defn bit-match 
  "bit-test over a list of bit indices"
  [n bits]
  (mapv (partial bit-test n) bits))

(defn n->bits
  "number => number + bit lists 
   e.g 53 => [[5 [false true false false]] [3 [true false false false]]]
   we're keeping the original digit so that we can show them together with
   the bit patterns later on"
  [n]
  (mapv #(vector % (bit-match % [3 2 1 0])) (decimal-parts n)))

(defn time->bits
  "converts time (in the format from get-time) to bit vectors"
  [time]
  (let [->bits (fn [coll key]
                 (update-in coll [key] n->bits))]
    (-> time 
        (->bits :hours)
        (->bits :minutes)
        (->bits :seconds))))

(defn cell
  "react component for one single cell, input state is all bits for this
  column with options map containing index."
  [bit owner]
  (let [color (if (om/value bit) "light" "dark")]
    (om/component (dom/div #js {:className (str "cell" " " color)} nil))))

(defn column
  "react component containing one column of the clock"
  [[digit bits] owner]
  (om/component
   (dom/div #js {:className "col"}
            (om/build-all cell bits)
            (dom/div #js {:className "cell"} (om/value digit)))))

(defn column-pair
  "react component of two digits columns, such as hour or minutes or seconds"
  [[msd lsd] owner]
  (om/component
   (dom/div #js {:className "colpair"}
            (om/build column msd)
            (om/build column lsd))))

(defn legend-cell
  "show one digit value in the legend"
  [digit owner]
  (om/component (dom/div #js {:className "cell"} (om/value digit))))

(defn legend-column
  "column showing the digit value of each row"
  [digits owner]
  (om/component
   (dom/div #js {:className "col legend"}
            (om/build-all legend-cell digits))))

(def app-state (atom {:time (time->bits (get-time))
                      :legend [8 4 2 1]}))

(om/root
 app-state
 (fn [{:keys [legend time] :as app} owner]
   (reify
     om/IWillMount
     (will-mount [_]
       (js/setInterval
        (fn [] (om/update! time #(time->bits (get-time)))) 1000))
     om/IRender
     (render [_]
       (dom/div nil
                (om/build legend-column legend)
                (om/build column-pair (:hours time))
                (om/build column-pair (:minutes time))
                (om/build column-pair (:seconds time))))))
 (.getElementById js/document "content"))
