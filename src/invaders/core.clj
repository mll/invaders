(ns invaders.core
  (:require [invaders.preprocess :refer [prepare-radar-for-pattern]]
            [invaders.parser :refer [parse-radar]]))


(defn fuzzy-matcher-builder [pattern]
  (let [norm (* (count pattern) (count (first pattern)))]
    (fn [needle]
      (let [row-pairs (map vector pattern needle)
            probability 
            (/ (reduce + 
                       (map (fn [[row1 row2]]  
                              (reduce + (map (fn [[v1 v2]]
                                               (if (= v1 v2) 1 0))
                                             (map vector row1 row2))))
                            row-pairs))
               norm)]
        probability))))

(defn matches [radar pattern matcher threshold]
  (let [pattern-width (count (first pattern))
        pattern-height (count pattern)
        radar-width (count (first radar))
        radar-height (count radar)
        min-x (- pattern-width)
        max-x radar-width
        min-y (- pattern-height)
        max-y radar-height
        shifts (for [dx (range min-x max-x)
                     dy (range min-y max-y)]
                 [dx dy])]
    (->> shifts
         (pmap (fn [[dx dy]]
                 (let [{:keys [prepared filled-ratio]} (prepare-radar-for-pattern radar pattern {:x dx :y dy})
                       final-probability (+ (* (matcher prepared) (- 1 filled-ratio)) 
                                            (* (/ 1 2) filled-ratio))]
                   ;; when matching something out of radar image, we assign 0.5 probability to every
                   ;; field out of sight
                   (when (> final-probability threshold)                     
                     [dx dy final-probability]))))
         (remove nil?)
         (doall))))

