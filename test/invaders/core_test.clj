(ns invaders.core-test
  (:require [clojure.test :refer :all]
            [invaders.parser :as parser]
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
      (is (= preprocessed (preprocess/prepare-radar-for-pattern preprocessed pattern-1)))))
  (testing "Idempotency 2"
    (let [pattern-1 (parser/parse-radar "resources/invader-1.txt")
          radar (parser/parse-radar "resources/sample-1.txt")
          preprocessed (preprocess/prepare-radar-for-pattern radar pattern-1 {:x -2 :y 3})]
      (is (= preprocessed (preprocess/prepare-radar-for-pattern preprocessed pattern-1)))))

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
          preprocessed (preprocess/prepare-radar-for-pattern radar pattern-1 {:x -2 :y 3})]
      (is (= preprocessed manual)))))

