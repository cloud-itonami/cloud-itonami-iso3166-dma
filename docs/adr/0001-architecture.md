# ADR-0001: Architecture — Commonwealth of Dominica market-entry compliance actor (`marketentry`)

**Status**: accepted
**Date**: 2026-07-22

## Context

`cloud-itonami-iso3166-dma` was published as a `:blueprint` (docs +
`blueprint.edn` only, then a country-level `culture.facts` catalog in a
separate Wave 1 batch) but carried ZERO `src/marketentry` or
`src/statute` content -- its `:public-sector/market-entry-compliance`
domain, declared in `blueprint.edn`, was unimplemented. This ADR closes
that gap, following the pattern established by `cloud-itonami-iso3166-jpn`
(origin) and the wider iso3166 family, most recently
`cloud-itonami-iso3166-atg` / `-brb` / `-blz` / `-com` / `-cpv` (the
simpler, no-`goyoukiki` shape this blueprint also uses --
`blueprint.edn`'s `:required-technologies` does not list `:ontology`, so
this fork skips the `marketentry.goyoukiki` real-tender-fact bridge JPN
carries).

## Decision

Build the full governed-actor architecture for `marketentry`, mirroring
the family's harness verbatim (StateGraph node names, governor
hard/escalate contract, phase 0-3 rollout, `Store` protocol with
MemStore + DatomicStore parity) and researching Dominica's own real
market-entry rules from scratch for the country-specific content.

- **Store**: `marketentry.store`, MemStore + DatomicStore, proven parity
  via contract test.
- **Registry**: `marketentry.registry`, pure DRAFT-certificate
  construction via `unsigned-certificate`, jurisdiction-scoped sequence
  numbering (`DMA-DFT-000000`, `DMA-SUB-000000`), plus the flagship
  dual authority-escalation recompute (see below).
- **Governor**: `:market-entry-compliance-governor` (family keyword from
  `blueprint.edn`).
- **Entity shape**: `engagement`, sequential draft -> submit on the same
  record. `high-stakes` = `#{:actuation/draft-filing
  :actuation/submit-filing}`.
- **Phase**: 0->3; `:filing/draft` and `:filing/submit` NEVER auto-
  commit at any phase.

### Which body administers procurement -- Central Tenders Board and e-procurement portal both checked, neither found

The task named two open questions to investigate rather than assume:
whether Dominica has a "Central Tenders Board" or similar, and whether
an e-procurement portal exists or the process is paper/gazette-based.
NEITHER was found in the form asked. `finance.gov.dm`'s own "Divisions,
Units and Departments" listing (fetched directly) names a "Central
Procurement Unit" -- its own page states: "The Central Procurement Unit
was established in 2014, following the enactment of the Public
Procurement and Contract Administration Act of 2012 ... The Central
Procurement Unit (CPU) is now governed by the New Public Procurement
and Disposal of Public Property Act of 2021." The current Act (Act No.
14 of 2021, downloaded directly from `dominica.gov.dm` and read via
`pdftotext`, a genuine text-layer PDF -- assented by President Charles
A. Savarin 8 December 2021, gazetted 9 December 2021) establishes a
Chief Procurement Compliance Officer (s.8) and a Public Procurement
Board (s.9), and repeals "THE PUBLIC PROCUREMENT AND CONTRACT
ADMINISTRATION ACT, CHAP. 63:06" (the Act's own long title). No
`procurement.gov.dm` / `tenders.gov.dm` / `ppb.gov.dm` /
`publicprocurement.gov.dm` host resolves (DNS failure on every guess,
curl-verified). The Act's own s.2 Interpretation defines a "public
procurement website" as "the dedicated website to be established and
maintained by the Ministry of Finance in accordance with section
7(1)(j)" -- future/aspirational wording. Real, current (2025-2026-dated)
contract-award and general-procurement notices are instead published on
the general government portal's own Notices section
(`www.dominica.gov.dm/notices`, fetched directly, live entries confirmed
as recent as Jul 2026) -- cited as this catalog's `:provenance` in place
of a nonexistent dedicated portal.

### Flagship HARD check: `award-authority-insufficient` -- a check OBJECT genuinely different from every prior sibling

The Act's own Second Schedule (ss 8(2)/10(2)/16(2)/17(3)/19(1)/44(3),
image-verified directly by rendering the Act PDF's page 189 to a PNG
with `gs` and viewing it, confirming the OCR'd `pdftotext` text was
accurate) fixes, by estimated contract cost in EC dollars, which public
official/institution must (a) confirm compliance of the award
recommendation with the Act and Regulations, and (b) approve the award
recommendation itself:

| | Estimated cost (EC$) | Confirmation of compliance | Approval of award recommendation |
|---|---|---|---|
| Threshold A | 0 - 4,999.99 | Head of Procuring Entity | Head of Procuring Entity |
| Threshold B | 5,000.00 - 999,999.99 | Chief Procurement Compliance Officer | Head of Procuring Entity |
| Threshold C | 1,000,000.00 and above | Chief Procurement Compliance Officer | Public Procurement Board |

`marketentry.registry/required-confirming-authority` /
`required-approving-authority` independently recompute which official
each of the engagement's own declared `:contract-value` requires, and
`award-authority-insufficient?` HARD-holds `:filing/submit` if EITHER of
the engagement's own declared `:confirming-authority`/`:approving-
authority` falls short.

This is a check OBJECT genuinely different from every prior iso3166
sibling this repo mirrors, even though several share the general "N-band
value threshold" MECHANIC: Antigua and Barbuda's `vendor-class-
insufficient` recomputes the BIDDER's own vendor-registration
eligibility class; Cabo Verde's `registration-fee-threshold-mismatch`
recomputes a per-contract-kind FEE-EXEMPTION classification; Comoros's
`armp-exclusion-active` recomputes whether a time-bound NEGATIVE
SANCTION on the bidder has lapsed. Dominica's Second Schedule check is
about NEITHER the bidder's eligibility, a fee obligation, NOR a sanction
on the bidder -- it is the PROCURING ENTITY's OWN internal governance/
sign-off hierarchy for the award, and it is DUAL (two independent
escalation boundaries for two different roles: confirming-authority
escalates at the EC$5,000 A/B boundary, approving-authority escalates
at the DIFFERENT EC$1,000,000 B/C boundary), not a single monotonic
N-tier classification.

This iteration also investigated two other candidates before choosing
this one, and documents why each was set aside:

- **s.80 Suspension and debarment** (+ Fifth Schedule Debarment
  Procedure): a bidder under debarment proceedings is automatically
  suspended and, after determination, "declared ineligible for the
  period prescribed in the debarment proceedings" (s.80(6)) -- a real,
  verified, TIME-BOUND negative sanction on the bidder. Its general
  MECHANIC (a time-bound bar that lapses) is too close to Comoros's own
  ARMP-exclusion-lapse shape to count as genuinely different, and unlike
  Comoros's fixed five-year statutory prescription window, Dominica's
  debarment period is set case-by-case by the Debarment Committee, not a
  fixed constant this repo could independently recompute from the Act's
  own text alone.
- **Regulations reg.10(2)**: a per-procurement-kind (works/goods-
  services/consulting) EC$ threshold gating whether international
  publication is required. Mechanically too close to Cabo Verde's own
  per-contract-kind fee-exemption threshold/band shape.

### Other HARD checks (all unoverridable)

1. **spec-basis** -- never invent a jurisdiction's market-entry
   requirements (`marketentry.facts` G2 catalog: Central Procurement
   Unit / CIPO / IRD for DMA).
2. **evidence-incomplete** -- draft/submit require a full assessment
   checklist on file.
3. **award-authority-insufficient** -- see above (FLAGSHIP).
4. **engagement-fee-mismatch** -- recompute `base-fee + monthly-rate ×
   monitoring-months` (ground-truth-recompute discipline).
5. **paye-unregistered** -- conditional on `:has-employees?`. Grounded
   in CIPO's own published administrative practice: company-level tax
   registration is automatic alongside incorporation, but employee PAYE
   registration is a separate, not-automatic act once the company has
   staff (see the one-act-vs-two-acts finding below) -- a genuinely
   different conditional gate from ATG's TIN-verification check (a
   separate application for the company's OWN tax identifier).
6. **already-drafted / already-submitted** -- dedicated booleans, never
   a `:status` value.

### `rep-spec-basis`: genuinely FOUND, unlike ATG's honestly-nil entry

Unlike ATG (which explicitly could not locate a gazetted personal-
exclusion-grounds text and left this nil), Dominica's own Act text DOES
extend debarment consequences to a bidder's representatives: s.80(6)
(read directly from the Act PDF) provides that after a Fifth-Schedule
debarment-proceedings determination that a bidder "has engaged in a
prohibited practice, THE BIDDER, ITS DIRECTORS OR AFFILIATES may: (a) be
required to reimburse ... (b) be declared ineligible for the period
prescribed in the debarment proceedings". `marketentry.facts/rep-spec-
basis` returns this real citation for DMA.

### The one-act-vs-two-acts business-registration/tax question -- a THIRD variant

The task asked every iteration to investigate, rather than assume,
whether business registration and tax-ID issuance happen in one act or
two. Dominica is NEITHER a clean one-act model (Albania/Armenia/
Azerbaijan) NOR ATG's clean two-act model (a separate, subsequent IRD
application with its own forms, CB001+F16) -- it is a THIRD, more
tightly-integrated variant. CIPO's own text (`cipo.gov.dm/businesses-
and-companies/companies`, "TAX AND SOCIAL SECURITY" section, fetched
directly): "The information provided to CIPO for the incorporation of a
company will be shared with the Inland Revenue Division in order that
the company can automatically be registered as a taxpayer. Confirmation
of the tax registration will be provided with the incorporation
certificate." One incorporation act AUTOMATICALLY triggers a downstream
tax-registration act on the SAME transaction, confirmed on the SAME
certificate -- genuinely different from both prior shapes this family
has documented. CIPO's own text also identifies the genuinely separate,
NOT-automatic conditional this vertical's PAYE check is grounded in: "As
and when the company takes on employees, it will still [be] necessary
for them to be registered separately for PAYE purposes."

### Citizenship by Investment (CBI): investigated, deliberately excluded

The task named Dominica's CBI programme as a candidate to investigate
if genuinely relevant. `finance.gov.dm`'s own Citizenship by Investment
Unit (CBIU) page (fetched directly) confirms the CBIU "administers the
Citizenship by Investment (CBI) Programme ... works closely with
agents, due diligence firms, a marketing firm and developers" -- an
individual citizenship/passport-by-investment regime, not a business/
company-registration or public-procurement regime. Deliberately NOT
folded into this catalog's scope.

### `statute.facts` (second, orthogonal catalog) -- two statutes, not three, honestly

Two Dominica statutes, both confirmed by downloading the PDF directly
from `dominica.gov.dm` and reading the extracted text: the Companies
Act, No. 21 of 1994 (a SCANNED image-only PDF -- `pdftotext` returned
empty, so this iteration rendered page 1 to a PNG with `gs` and OCR'd it
directly with `tesseract`, confirming s.4 Incorporation / s.8
Certificate of incorporation), and TWO separate labour statutes rather
than ATG's single consolidated code: the Labour Standards Act (Chapter
89:05, originally Act 2 of 1977, wages/hours/leave) and the Protection
of Employment Act (Chapter 89:02, originally Act 1 of 1977, termination/
redundancy). This iteration also specifically searched the government
portal's own law-title search for a Dominica Data Protection Act (the
third statute ATG's own catalog carries) and got ZERO results for both
"data protection" and "data" -- rather than infer or invent one, this
catalog honestly carries two DMA statutes, not three.

## Consequences

- `src/` now genuinely exists with real, tested, curl/pdftotext/OCR-
  cited content for this blueprint's declared domain (`:public-sector/
  market-entry-compliance`) -- moves this repo's
  `manifest/itonami-fleet-audit.edn` `:prod-ready?` signal from `:stub`
  to `:active`.
- The existing `culture.facts` catalog (Wave 1, unrelated batch) is
  untouched.
- s.80's suspension-and-debarment ineligibility list, and Regulations
  reg.10(2)'s per-kind international-publication threshold, are both
  genuine, verified, NOT-implemented extension points for a future
  iteration that wants a second/third governor check for this
  jurisdiction.
- Sibling country blueprints can continue forking this family and
  swapping in their own genuinely-researched `marketentry.facts` /
  `statute.facts` content and whichever flagship check their own law
  actually supports -- this ADR is itself further evidence that the
  flagship check should be chosen from real, currency-checked research,
  not copied by rote, and that a shared general MECHANIC (an N-band
  value threshold) can still ground a genuinely different check OBJECT.
