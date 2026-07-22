(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 450000 :monthly-rate 28000 :monitoring-months 12 :claimed-fee 786000.0}]
    (is (== 786000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 450000 :monthly-rate 28000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "DMA" 0)
        s (registry/register-submit "eng-1" "DMA" 0)]
    (is (= "DMA-DFT-000000" (get d "draft_number")))
    (is (= "DMA-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "DMA" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest required-confirming-authority-tiers
  (testing "Threshold A (EC$0-4,999.99) -> Head of Procuring Entity"
    (is (= :head-of-procuring-entity (registry/required-confirming-authority 0)))
    (is (= :head-of-procuring-entity (registry/required-confirming-authority 4999.99))))
  (testing "Threshold B/C (EC$5,000.00 and above) -> Chief Procurement Compliance Officer"
    (is (= :chief-procurement-compliance-officer (registry/required-confirming-authority 5000.00)))
    (is (= :chief-procurement-compliance-officer (registry/required-confirming-authority 750000)))
    (is (= :chief-procurement-compliance-officer (registry/required-confirming-authority 1000000.00))))
  (testing "missing/nil contract value -> Threshold A baseline"
    (is (= :head-of-procuring-entity (registry/required-confirming-authority nil)))))

(deftest required-approving-authority-tiers
  (testing "Threshold A/B (EC$0-999,999.99) -> Head of Procuring Entity"
    (is (= :head-of-procuring-entity (registry/required-approving-authority 0)))
    (is (= :head-of-procuring-entity (registry/required-approving-authority 5000.00)))
    (is (= :head-of-procuring-entity (registry/required-approving-authority 999999.99))))
  (testing "Threshold C (EC$1,000,000.00 and above) -> Public Procurement Board"
    (is (= :public-procurement-board (registry/required-approving-authority 1000000.00)))
    (is (= :public-procurement-board (registry/required-approving-authority 1500000))))
  (testing "missing/nil contract value -> Threshold A baseline"
    (is (= :head-of-procuring-entity (registry/required-approving-authority nil)))))

(deftest award-authority-insufficient
  (testing "declared authorities meet the required tiers -> not insufficient"
    (is (false? (registry/award-authority-insufficient?
                 {:contract-value 750000 :confirming-authority :chief-procurement-compliance-officer
                  :approving-authority :head-of-procuring-entity})))
    (is (false? (registry/award-authority-insufficient?
                 {:contract-value 3000 :confirming-authority :head-of-procuring-entity
                  :approving-authority :head-of-procuring-entity}))))
  (testing "a HIGHER-ranked declared authority than required -> still not insufficient"
    (is (false? (registry/award-authority-insufficient?
                 {:contract-value 3000 :confirming-authority :public-procurement-board
                  :approving-authority :public-procurement-board}))))
  (testing "approving-authority falls short (EC$1.5M requires Public Procurement Board) -> insufficient"
    (is (true? (registry/award-authority-insufficient?
                {:contract-value 1500000 :confirming-authority :chief-procurement-compliance-officer
                 :approving-authority :head-of-procuring-entity}))))
  (testing "confirming-authority falls short (EC$750,000 requires Chief Procurement Compliance Officer) -> insufficient"
    (is (true? (registry/award-authority-insufficient?
                {:contract-value 750000 :confirming-authority :head-of-procuring-entity
                 :approving-authority :head-of-procuring-entity}))))
  (testing "no declared authority -> insufficient even at Threshold A baseline"
    (is (true? (registry/award-authority-insufficient? {:contract-value 3000})))))
