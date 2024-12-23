(ns invaders.preprocess
  (:require [invaders.schema :as schema]
            [clojure.spec.alpha :as spec]
            [clojure.string :refer [split-lines]]))

(defn prepare-radar-for-pattern 
  "Prepares a section of radar input from [x...pattern.width] and [y...pattern.height], filling
   all the missing entries with 'fill'. The section is then ready to be matched."
  ([radar pattern]
   (prepare-radar-for-pattern radar pattern {}))
  ([radar 
    pattern 
    {:keys [x y fill] :or {x 0 y 0 fill 0}}]  
   {:pre [(spec/valid? :invaders.schema/radar-pattern pattern)
          (spec/valid? :invaders.schema/radar-pattern radar)
          (spec/valid? :invaders.schema/cell fill)]
    :post [(spec/valid? :invaders.schema/radar-pattern (:prepared %))
           (= (count pattern) (count (:prepared %)))
           (= (count (first pattern)) (count (first (:prepared %))))]}
   (let [height (count pattern)        
         width (count (first pattern))
         radar-height (count radar)
         radar-width (count (first radar))

         fillers (repeat fill)
         filler-row (vec (take width fillers))
         filler-rows (repeat filler-row)
         pre-x (max (- x) 0)
         pre-y (max (- y) 0)
         to-take-width (- width pre-x)
         to-take-height (- height pre-y)
         post-y (min (max 0 (- to-take-height (- radar-height y))) height)
         post-x (min (max 0 (- to-take-width (- radar-width x))) width)]
     {:filled-ratio (/
                     (+ (* (- width pre-x) pre-y) ; top
                        (* height pre-x) ; left + common with top
                        (* (- width post-x) post-y) ; bottom
                        (* height post-x)) ; right + common with bottom
                     (* height width))                  
      :prepared
      (vec 
       (concat
        (vec (take pre-y filler-rows))
        (map #(vec (concat (take pre-x fillers)
                           (take to-take-width (drop x %))
                           (take post-x fillers)))
             
             (vec (concat (take to-take-height (drop y radar)))))
        (take post-y filler-rows)))})))
