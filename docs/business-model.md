# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Commonwealth of Dominica

## Classification

- Repository: `cloud-itonami-iso3166-dma`
- ISO 3166: `DMA` (Commonwealth of Dominica)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Dominica public
  contract
- a foreign SME or civic-tech vendor entering the public sector in
  Dominica for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation
  and now needs public-sector market access

## Offer

- registration/submission walkthrough for public procurement under the
  Public Procurement and Disposal of Public Property Act, 2021 (Act No.
  14 of 2021), including which Second-Schedule authority (Head of
  Procuring Entity / Chief Procurement Compliance Officer / Public
  Procurement Board) a given contract value requires for confirmation
  of compliance and approval of the award recommendation
- business/tax registration checklist: Certificate of Incorporation
  from the Companies and Intellectual Property Office (CIPO, Companies
  Act 1994), with taxpayer registration confirmed automatically on the
  SAME certificate (Inland Revenue Division) -- plus a reminder that
  employee PAYE registration is a SEPARATE, non-automatic step once the
  operator has staff
- award-authority-sufficiency screening: independent verification that
  the confirming/approving authority recorded for an engagement actually
  meets what its own declared contract value requires under the Second
  Schedule, before any filing submission
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone -- it must be corrected
  against a cited official source first
- a confirming or approving authority that falls short of what the
  engagement's own declared contract value requires (Public Procurement
  and Disposal of Public Property Act, 2021 Second Schedule) is a HARD
  hold on `:filing/submit`, independently recomputed rather than trusted
  from a self-reported authority
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Dominica-licensed counsel or a registered agent
- this service is **not** a Citizenship by Investment (CBI) facilitator
  -- CBI is a separate individual citizenship regime administered by
  the Citizenship by Investment Unit, out of scope for this blueprint

## Boundary with adjacent actors (read before forking)

- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) -- a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis -- the two compose (fork a COFOG-function
  blueprint AND this one to operate in Dominica).
