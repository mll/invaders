(ns invaders.core-test
  (:require [clojure.test :refer :all]
            [invaders.parser :as parser]
            [invaders.core :refer [run]]
            [clojure.pprint :refer [pprint]]
            [invaders.preprocess :as preprocess]))

(def pattern-1 [[0 0 1 0 0 0 0 0 1 0 0]
                [0 0 0 1 0 0 0 1 0 0 0]
                [0 0 1 1 1 1 1 1 1 0 0]
                [0 1 1 0 1 1 1 0 1 1 0]
                [1 1 1 1 1 1 1 1 1 1 1]
                [1 0 1 1 1 1 1 1 1 0 1]
                [1 0 1 0 0 0 0 0 1 0 1]
                [0 0 0 1 1 0 1 1 0 0 0]])

(def pattern-2 [[0 0 0 1 1 0 0 0]
                [0 0 1 1 1 1 0 0]
                [0 1 1 1 1 1 1 0]
                [1 1 0 1 1 0 1 1]
                [1 1 1 1 1 1 1 1]
                [0 0 1 0 0 1 0 0]
                [0 1 0 1 1 0 1 0]
                [1 0 1 0 0 1 0 1]])

(deftest test-patterns-parsing
  (testing "Pattern 1"
    (is (= pattern-1 (parser/parse-radar "resources/invader-1.txt")))
    (is (not= pattern-2 (parser/parse-radar "resources/invader-1.txt"))))
  (testing "Pattern 2"
    (is (= pattern-2 (parser/parse-radar "resources/invader-2.txt")))
    (is (not= pattern-1 (parser/parse-radar "resources/invader-2.txt"))))  
  (testing "Sample 1"
    (is (= 0 (ffirst (parser/parse-radar "resources/sample-1.txt"))))
    (is (= 50 (count (parser/parse-radar "resources/sample-1.txt"))))
    (is (= 100 (count (first (parser/parse-radar "resources/sample-1.txt")))))
    (is (= 1 (-> (parser/parse-radar "resources/sample-1.txt")
                 (nth 5)
                 (nth 10))))    
    (is (not= pattern-1 (parser/parse-radar "resources/sample-1.txt")))))

(deftest test-preprocessing
  (testing "Idempotency 1"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          preprocessed (preprocess/prepare-radar-for-pattern pattern-1 pattern-1)]
      (is (= (:prepared preprocessed) 
             (:prepared (preprocess/prepare-radar-for-pattern (:prepared preprocessed) pattern-1))))))
  (testing "Idempotency 2"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          preprocessed (preprocess/prepare-radar-for-pattern radar pattern-1 {:x -2 :y 3})]
      (is (= (:prepared preprocessed) 
             (:prepared (preprocess/prepare-radar-for-pattern (:prepared preprocessed) pattern-1))))))
  
  (testing "Match"
    (let [manual [[0 0 0 0 0 0 0 0 0 1 0]
                  [0 0 0 0 0 0 0 0 1 0 0]
                  [0 0 0 1 0 0 1 0 0 0 0]
                  [0 0 1 0 0 0 0 0 0 0 0]
                  [0 0 0 0 1 0 0 0 0 0 0]
                  [0 0 0 0 0 0 0 0 0 0 0]
                  [0 0 0 0 0 0 0 0 0 0 0]
                  [0 0 0 0 0 0 0 0 1 0 0]]
          pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          {preprocessed :prepared} (preprocess/prepare-radar-for-pattern radar pattern-1 {:x -2 :y 3})]
      (is (= preprocessed manual))))

  (testing "Ratio"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          {ratio :filled-ratio external :external-tiles} 
          (preprocess/prepare-radar-for-pattern radar pattern-1 {:x -2 :y 3})]
      (is (= (/ 2 11) ratio))
      (is (= #{[0 0] [0 1] [0 2] [0 3] [0 4] [0 5] [0 6] [0 7]
               [1 0][1 1] [1 2] [1 3] [1 4] [1 5] [1 6] [1 7]}))))
  
  (testing "Ratio"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          {ratio :filled-ratio external :external-tiles} 
          (preprocess/prepare-radar-for-pattern radar pattern-1 {:x 95 :y 3})]
      (is (= (/ 6 11) ratio))
      (is (= #{[5 0] [5 1] [5 2] [5 3] [5 4] [5 5] [5 6] [5 7] 
               [6 0] [6 1] [6 2] [6 3] [6 4] [6 5] [6 6] [6 7]               
               [7 0] [7 1] [7 2] [7 3] [7 4] [7 5] [7 6] [7 7]
               [8 0] [8 1] [8 2] [8 3] [8 4] [8 5] [8 6] [8 7]
               [9 0] [9 1] [9 2] [9 3] [9 4] [9 5] [9 6] [9 7]
               [10 0] [10 1] [10 2] [10 3] [10 4] [10 5] [10 6] 
               [10 7]}))))
  
  (testing "Ratio"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          {ratio :filled-ratio external :external-tiles}  
          (preprocess/prepare-radar-for-pattern radar pattern-1 {:x 90 :y 43})]
      (is (= (/ 9 44) ratio))
      (is (= #{[0 7] [1 7] [2 7] [3 7] [4 7] [5 7] [6 7] [7 7] [8 7] [9 7] [10 7]
               [10 0] [10 1] [10 2] [10 3] [10 4] [10 5] [10 6]}
             external))))

  (testing "Ratio"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          {ratio :filled-ratio external :external-tiles}  
          (preprocess/prepare-radar-for-pattern radar pattern-1 {:x -11 :y 47})]
      (is (= 1 ratio))
      (is (= 88 (count external))))))

(deftest matching
  (testing "Matching"
    (is (= {:pattern-1
            [[10 -1 129/176]
             [13 27 59/88]
             [15 -2 29/44]
             [15 27 29/44]
             [16 27 15/22]
             [60 13 10/11]
             [74 1 7/8]
             [79 40 15/22]
             [82 40 65/88]
             [85 12 19/22]
             [86 12 61/88]],
            :pattern-2
            [[16 28 55/64]
             [17 45 49/64]
             [18 -1 49/64]
             [18 45 21/32]
             [35 15 27/32]
             [37 13 21/32]
             [42 0 7/8]
             [60 14 23/32]
             [63 14 45/64]
             [74 2 21/32]
             [77 2 23/32]
             [82 41 55/64]
             [83 40 45/64]
             [87 12 43/64]
             [88 13 21/32]]} 
           (run "resources/sample-1.txt" 0.65)))))
