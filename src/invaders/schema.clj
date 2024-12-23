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

(spec/def ::natural-number (spec/and int? #(>= % 0)))

(spec/def ::cell-index (spec/and (spec/coll-of ::natural-number)
                                 #(= (count %) 2)))

(spec/def ::cell-indices (spec/coll-of ::cell-index))

(spec/def ::external-tiles ::cell-indices)
(spec/def ::prepared ::radar-pattern)

(spec/def ::preprocessed-radar (spec/keys :req-un [::external-tiles ::filled-ratio ::prepared]))

