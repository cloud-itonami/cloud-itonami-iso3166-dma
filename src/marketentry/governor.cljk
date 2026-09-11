(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Commonwealth of Dominica procurement law, whether a claimed
  engagement fee actually equals base + months x rate, whether the
  engagement's own declared confirming/approving authority actually
  meets what the Public Procurement and Disposal of Public Property
  Act, 2021's own Second Schedule requires for its declared contract
  value, whether employee PAYE registration has been verified for an
  engagement whose company has taken on staff, or when a draft stops
  being a draft and becomes a real-world portal registration/filing
  submission, so this MUST be a separate system able to *reject* a
  proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Award authority insufficient
       for contract value            -- for `:filing/submit`,
                                       INDEPENDENTLY recompute which
                                       official the Public Procurement
                                       and Disposal of Public Property
                                       Act, 2021's own Second Schedule
                                       requires to (a) confirm
                                       compliance and (b) approve the
                                       award recommendation for the
                                       engagement's own declared
                                       `:contract-value`, and HARD-hold
                                       if either of the engagement's own
                                       declared `:confirming-authority`/
                                       `:approving-authority` falls
                                       short. FLAGSHIP genuinely new
                                       check for the iso3166 family
                                       (grep-verified absent as a
                                       governor check function name
                                       fleet-wide at build time) -- a
                                       DUAL, independently-escalating
                                       AUTHORITY-LADDER recompute over
                                       the procuring entity's OWN
                                       internal governance hierarchy
                                       (never the bidder's own
                                       eligibility, a fee obligation, or
                                       a time-bound sanction), a
                                       genuinely different check OBJECT
                                       from Antigua and Barbuda's
                                       vendor-eligibility 3-tier class,
                                       Cabo Verde's per-kind fee-
                                       exemption threshold/band, and
                                       Comoros's bidder-side negative-
                                       sanction prescription-lapse, even
                                       though all four share the general
                                       'N-band value threshold' MECHANIC
                                       -- see `marketentry.registry`.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. PAYE unregistered            -- for `:filing/submit`, when the
                                       engagement declares
                                       `:has-employees? true`,
                                       INDEPENDENTLY check
                                       `:paye-registered?`. CONDITIONAL
                                       on the engagement's own ground
                                       truth. Grounded in CIPO's own
                                       published administrative practice
                                       ('As and when the company takes
                                       on employees, it will still [be]
                                       necessary for them to be
                                       registered separately for PAYE
                                       purposes') -- unlike ATG's TIN
                                       check (a separate application for
                                       the COMPANY's own tax identifier),
                                       Dominica's company-level tax
                                       registration is AUTOMATIC
                                       alongside incorporation, so the
                                       genuinely analogous NOT-automatic
                                       gate here is employee PAYE
                                       registration, not company TIN
                                       issuance.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(CIPO登録/税務登録確認/Second Schedule確認・承認記録等)が充足していない状態での提案"}]))))

(defn- award-authority-insufficient-violations
  "For `:filing/submit`, INDEPENDENTLY recompute which official the
  Second Schedule requires to confirm compliance and approve the award
  recommendation for the engagement's own declared `:contract-value`,
  and HARD-hold if either of the engagement's own declared
  `:confirming-authority`/`:approving-authority` falls short -- the
  flagship genuinely new check this vertical adds. Evaluated for EVERY
  `:filing/submit`, unconditionally."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/award-authority-insufficient? e)
        [{:rule :award-authority-insufficient
          :detail (str subject " の申告契約額(EC$" (:contract-value e)
                      ")が要求する確認者(" (registry/required-confirming-authority (:contract-value e))
                      ")/承認者(" (registry/required-approving-authority (:contract-value e))
                      ")に対し、申告された確認者(" (:confirming-authority e)
                      ")/承認者(" (:approving-authority e)
                      ")が不足している -- Public Procurement and Disposal of Public Property Act, 2021 Second Schedule")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- paye-unregistered-violations
  "For `:filing/submit`, when the engagement declares `:has-employees?
  true`, INDEPENDENTLY check `:paye-registered?` -- CONDITIONAL on the
  engagement's own ground truth. Grounded in CIPO's own published
  administrative practice: company-level tax registration is automatic
  alongside incorporation, but employee PAYE registration is a
  SEPARATE, NOT-automatic act owed to the Inland Revenue Division once
  the company has staff."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:has-employees? e))
                 (not (true? (:paye-registered? e))))
        [{:rule :paye-unregistered
          :detail (str subject " は従業員を有するためPAYE(Pay As You Earn)登録(Inland Revenue Division)を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (award-authority-insufficient-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (paye-unregistered-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
