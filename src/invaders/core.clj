(ns invaders.core
  (:require [invaders.preprocess :refer [prepare-radar-for-pattern]]
            [invaders.parser :refer [parse-radar]]
            [clojure.pprint :refer [pprint]]))

(defn fuzzy-matcher-builder 
  "Returns a function that given the needle and external? set of points computes 
   detection probability for difference between the needle and external?

  The matcher is prebuilt and separated out in case a more involved algorithm 
  were to be used (neural nets?). Prebuilding is needed once only for the NN,
  there is not point in re-doing it each time. 

  Some detectors that can detect both patterns in a single go could be considered, but because
  patterns have different dimensions it is not straightforward. 
  
  Therefore we arrive at a per-pattern matcher scheme."
  [pattern]
  (let [norm (* (count pattern) (count (first pattern)))]
    (fn [needle external?]
      (let [internal-norm (- norm (count external?))]
        (if (zero? internal-norm) 
          0
          (let [;; row-triples contains a sequence of [pattern-row needle-row row-index ] 
                row-triples (map-indexed #(conj %2 %1) (map vector pattern needle))
                partial-sums (map (fn [[row1 row2 y]]  
                                    (let [;; column-triples contains a sequence of 
                                          ;; [pattern-value needle-value column-index] 
                                          column-triples (map-indexed 
                                                          #(conj %2 %1) 
                                                          (map vector row1 row2))]
                                      (->> column-triples
                                           (map (fn [[v1 v2 x]]
                                                  (if (and (= v1 v2)
                                                           (not (external? [x y]))) 
                                                    1 0)))
                                           (reduce +)))) 
                                  row-triples)]
            (/ (reduce + partial-sums)
               internal-norm)))))))

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
                 (let [{:keys [prepared filled-ratio external-tiles]} (prepare-radar-for-pattern radar pattern {:x dx :y dy})
                       final-probability (+ (* (matcher prepared external-tiles) (- 1 filled-ratio)) 
                                            (* (/ 1 2) filled-ratio))]
                   ;; when matching something out of radar image, we assign 0.5 probability to every
                   ;; field out of sight - 0.5 means no knowledge at all.
                   (when (> final-probability threshold)                     
                     [dx dy final-probability]))))
         (remove nil?)
         (doall))))

(defn run 
  "For each pattern returns a sequence of triples: [dx, dy, p] where (dx, dy) is a detection shift 
  vs top left corner of the radar pattern (positive means right and down), and p is detection 
  probability. Easily generalisable to more patterns. Uses sort to stabilise output for test purposes.
  
  Note that no state management is needed for our programme as our problem 
  matches beautifully to the functional 'in -> computation -> out' paradigm.
  
  In production version some caching could be introduced as a state, depending on usage patterns.
  Matchers could be cached, but here it is enough to construct them first, since the programme
  runs on a single radar sample. "
  [filename threshold]
  (let [pattern-1 (-> "resources/invader-1.txt"
                      (parse-radar))
        pattern-2 (-> "resources/invader-2.txt"
                      (parse-radar))
        matcher-1 (fuzzy-matcher-builder pattern-1)
        matcher-2 (fuzzy-matcher-builder pattern-2)
        radar (parse-radar filename)]
    {:pattern-1 (sort (matches radar pattern-1 matcher-1 threshold))
     :pattern-2 (sort (matches radar pattern-2 matcher-2 threshold))}))

(defn -main [filename threshold]
  (pprint (run filename (parse-double threshold)))
  (shutdown-agents)
  0)
