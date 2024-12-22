(ns invaders.schema
  (:require [clojure.spec.alpha :as spec]))


(spec/def ::cell (spec/or :zero #(= 0 %) 
                          :one #(= 1 %)))

(spec/def ::radar-row (spec/coll-of ::cell))


(spec/def ::equal-rows (fn [rows]
                         (let [height (count rows)
                               width (-> rows first count)]
                           (or (<= height 1)
                               (every? #(= (count %) width) rows)))))

(spec/def ::radar-pattern (spec/and (spec/coll-of ::radar-row)
                                    ::equal-rows))



