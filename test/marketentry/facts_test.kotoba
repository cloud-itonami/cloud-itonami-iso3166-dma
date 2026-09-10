(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest dma-has-spec-basis
  (let [sb (facts/spec-basis "DMA")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "DMA")))
    (is (some? (facts/authority-escalation-spec-basis "DMA")))))

(deftest dma-rep-spec-basis-is-genuinely-present
  (testing "s.80(6) of the Public Procurement and Disposal of Public Property Act, 2021 extends debarment consequences to a bidder's directors/affiliates -- unlike ATG, this is a genuine, verified finding, not honestly-nil"
    (let [rb (facts/rep-spec-basis "DMA")]
      (is (some? rb))
      (is (string? (:rep-owner-authority rb)))
      (is (string? (:rep-legal-basis rb))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "DMA")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "DMA" all)))
    (is (not (facts/required-evidence-satisfied? "DMA" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["DMA" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))
