(ns culture.facts-test
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [culture.facts :as facts]))

(deftest dma-has-culture-basis
  (let [sb (facts/spec-basis "DMA")]
    (is (= 7 (count sb)))
    (is (= (count sb) (count (set (map :culture/id sb)))))
    (is (every? #(str/starts-with? (:culture/url %) "https://") sb))
    (is (every? #(= "DMA" (:culture/country %)) sb))
    (is (every? #(nil? (:culture/municipality %)) sb))
    (is (every? #(seq (:culture/summary %)) sb))
    (is (every? #(string? (:culture/retrieved-at %)) sb))))

(deftest unknown-jurisdiction-has-no-basis
  ;; DOM is the Dominican Republic -- a different country, never covered here.
  (is (nil? (facts/spec-basis "DOM")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["DMA" "DOM"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["DOM"] (:missing-jurisdictions c)))))

(deftest by-kind-filters
  (is (= 3 (count (facts/by-kind "DMA" :dish))))
  (is (= ["dma.festival.world-creole-music-festival"]
         (mapv :culture/id (facts/by-kind "DMA" :festival))))
  (is (empty? (facts/by-kind "DMA" :other)))
  (is (empty? (facts/by-kind "DOM" :dish))))

(deftest tx-file-matches-catalog
  (let [tx (edn/read-string (slurp "data/culture-tx.edn"))
        flat (mapcat val (sort-by key facts/catalog))]
    (is (= (vec flat) (vec tx)))))
