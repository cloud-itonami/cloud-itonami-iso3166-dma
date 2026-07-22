(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `award-authority-insufficient?` (with its two components
  `confirming-authority-insufficient?` / `approving-authority-
  insufficient?`) is THIS vertical's own new ground-truth check,
  grounding DMA's flagship governor check
  (`marketentry.governor/award-authority-insufficient-violations`): the
  Public Procurement and Disposal of Public Property Act, 2021 (Act No.
  14 of 2021) Second Schedule (ss 8(2)/10(2)/16(2)/17(3)/19(1)/44(3),
  own primary text, see `marketentry.facts`) fixes, by estimated
  contract cost in EC dollars, which public official/institution must
  (a) confirm compliance of the award recommendation with the Act and
  Regulations, and (b) approve the award recommendation itself:

    Threshold A  EC$0        - 4,999.99      confirm=Head of Procuring Entity  approve=Head of Procuring Entity
    Threshold B  EC$5,000.00 - 999,999.99     confirm=Chief Procurement Compliance Officer  approve=Head of Procuring Entity
    Threshold C  EC$1,000,000.00 and above    confirm=Chief Procurement Compliance Officer  approve=Public Procurement Board

  This is a DIFFERENT check SHAPE from every prior sibling this repo
  mirrors: not a turnover-scaled formula (Bulgaria), not a flat
  statutory threshold (Albania), not a boolean registry-membership read
  (Azerbaijan/Armenia/Bolivia), not a 3-tier VENDOR-eligibility
  classification (Antigua and Barbuda), not a bid-evaluation price-
  adjustment recompute (Benin), not a struck-off company-registry
  legal-capacity boolean (Belize), not a validity-window expiry-date
  recompute (Barbados), not a negative-sanction prescription-lapse
  (Comoros), not a per-contract-kind fee-exemption threshold/band
  classification (Cabo Verde) -- it is a DUAL, INDEPENDENTLY-escalating
  AUTHORITY ladder over the SAME estimated-cost figure: the confirming-
  authority ladder steps up at the Threshold A/B boundary (EC$5,000),
  the approving-authority ladder steps up at the DIFFERENT Threshold
  B/C boundary (EC$1,000,000), and the object being checked is the
  procuring entity's OWN internal governance/sign-off hierarchy for the
  award -- never the bidder's own eligibility, a fee obligation, or a
  time-bound sanction on the bidder. (This iteration also investigated
  s.80's suspension-and-debarment ineligibility list as a candidate
  flagship check, but its bar duration is set case-by-case by the
  Debarment Committee rather than a fixed statutory window, making its
  general MECHANIC -- a time-bound negative sanction on the bidder --
  too close to Comoros's ARMP-exclusion-lapse shape to count as
  genuinely different; and Regulations reg.10(2)'s per-procurement-kind
  international-publication threshold is, likewise, mechanically too
  close to Cabo Verde's per-kind fee-exemption threshold/band shape.
  The Second Schedule's dual authority-escalation ladder was chosen
  instead because its checked OBJECT -- internal award-approval
  governance, not the bidder at all -- is genuinely different, even
  though the general 'N-band value threshold' MECHANIC recurs across
  this family; see `marketentry.governor` for the full reasoning.)

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real Central Procurement Unit, Public Procurement Board,
  or government Notices system. It builds the RECORD an operator would
  keep, not the act of submitting a portal registration itself (that is
  `marketentry.operation`'s `:filing/submit`, always human-gated -- see
  README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(def authority-thresholds-ecd
  "Public Procurement and Disposal of Public Property Act, 2021 (Act No.
  14 of 2021), Second Schedule -- the upper EC$ bound of Threshold A
  (confirming-authority escalation boundary) and of Threshold B
  (approving-authority escalation boundary). All amounts are Eastern
  Caribbean Dollars (EC$), the currency the Schedule's own table is
  published in."
  {:confirm-boundary 4999.99
   :approve-boundary 999999.99})

(def authority-rank
  "Ordinal reading of the Second Schedule's OWN monotonic escalation
  direction (never stated as a general seniority hierarchy in the Act
  itself, but the Schedule never de-escalates as value rises, so this
  is a faithful, minimal encoding of it): Head of Procuring Entity <
  Chief Procurement Compliance Officer < Public Procurement Board."
  {:head-of-procuring-entity 0
   :chief-procurement-compliance-officer 1
   :public-procurement-board 2})

(defn required-confirming-authority
  "Which official the Second Schedule requires to confirm compliance
  for `contract-value` (EC$). Missing/nil/zero contract value requires
  only the Threshold A baseline (Head of Procuring Entity)."
  [contract-value]
  (let [v (double (or contract-value 0))]
    (if (<= v (:confirm-boundary authority-thresholds-ecd))
      :head-of-procuring-entity
      :chief-procurement-compliance-officer)))

(defn required-approving-authority
  "Which official/institution the Second Schedule requires to approve
  the award recommendation for `contract-value` (EC$). Missing/nil/zero
  contract value requires only the Threshold A baseline (Head of
  Procuring Entity)."
  [contract-value]
  (let [v (double (or contract-value 0))]
    (if (<= v (:approve-boundary authority-thresholds-ecd))
      :head-of-procuring-entity
      :public-procurement-board)))

(defn- rank [authority]
  (get authority-rank authority -1))

(defn confirming-authority-insufficient?
  "Does `engagement`'s own declared `:confirming-authority` fall SHORT
  of what its own declared `:contract-value` requires under the Second
  Schedule? A missing/unrecognized declared authority ranks below every
  valid authority, so it is always insufficient -- never assume
  compliance from the absence of a declared confirming authority."
  [{:keys [contract-value confirming-authority]}]
  (< (rank confirming-authority) (rank (required-confirming-authority contract-value))))

(defn approving-authority-insufficient?
  "Does `engagement`'s own declared `:approving-authority` fall SHORT of
  what its own declared `:contract-value` requires under the Second
  Schedule? Same missing-is-always-insufficient discipline as
  `confirming-authority-insufficient?`."
  [{:keys [contract-value approving-authority]}]
  (< (rank approving-authority) (rank (required-approving-authority contract-value))))

(defn award-authority-insufficient?
  "Does EITHER of `engagement`'s own declared confirming/approving
  authorities fall short of what its own declared `:contract-value`
  requires? The flagship check this vertical adds -- a DUAL,
  independently-escalating authority ladder, not a single 3-tier
  classification."
  [engagement]
  (boolean (or (confirming-authority-insufficient? engagement)
               (approving-authority-insufficient? engagement))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
