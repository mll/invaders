(ns invaders.parser
  (:require [invaders.schema :as schema]
            [clojure.spec.alpha :as spec]
            [clojure.string :refer [split-lines]]))

(defn parse-radar [filename]
  {:pre [(spec/valid? string? filename)]
   :post [(spec/valid? :invaders.schema/radar-pattern %)]}
  (let [lines (-> filename slurp split-lines)
        char->digit (fn [ch] (if (= ch \-) 0 1))]
    (mapv
     (fn [line]
       (mapv char->digit line))
     lines)))



