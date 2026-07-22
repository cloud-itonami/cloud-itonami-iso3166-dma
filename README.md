# cloud-itonami-iso3166-dma

Open ISO 3166 Blueprint for **DMA**: Commonwealth of Dominica --
**`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a Compliance Advisor +
independent **Market-Entry Compliance Governor** to navigate public-
procurement registration, local business/tax registration, and
regulatory-compliance rules in Dominica, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

## Official surface (curl-verified 2026-07-22 -- every `*.gov.dm` / `dominica.gov.dm` host attempted resolved and served content directly, no TLS/JS blocker hit)

- Procurement: Central Procurement Unit (established 2014), Chief
  Procurement Compliance Officer and Public Procurement Board, Ministry
  of Finance, Economic Development, Climate Resilience and Social
  Security, established/governed by the Public Procurement and
  Disposal of Public Property Act, 2021 (Act No. 14 of 2021), which
  repealed the Public Procurement and Contract Administration Act,
  Chap. 63:06 (originally Act 11 of 2012). No dedicated e-procurement
  portal domain resolves; the Act's own s.2 defines a "public
  procurement website" as one "to be established" by the Ministry of
  Finance -- current practice publishes procurement notices on the
  general government portal (`www.dominica.gov.dm/notices`).
- Business registration: the Companies and Intellectual Property
  Office (CIPO, `cipo.gov.dm`), established under Part II of the
  Patent Act, Act 8 of 1999, a Department of the Ministry of National
  Security and Home Affairs -- administers the Companies Act, No. 21
  of 1994 (s.4 Incorporation, s.8 Certificate of Incorporation).
- Tax: the Inland Revenue Division (IRD), Ministry of Finance, Economic
  Development, Climate Resilience and Social Security. Company-level
  taxpayer registration is AUTOMATIC alongside incorporation -- CIPO
  shares the incorporation application with the IRD and confirms tax
  registration on the SAME incorporation certificate (per CIPO's own
  published administrative practice). Employee PAYE registration
  remains a separate, NOT-automatic act once the company has staff.

## Implementation (R0)

| Piece | Location |
|---|---|
| Actor namespaces | `src/marketentry/*` |
| Governor | `:market-entry-compliance-governor` |
| Ops | `:engagement/intake` · `:jurisdiction/assess` · `:filing/draft` · `:filing/submit` |
| Flagship HARD check | `award-authority-insufficient` (Public Procurement and Disposal of Public Property Act, 2021 Second Schedule's DUAL, independently-escalating confirming-authority/approving-authority ladder, recomputed against the engagement's own declared contract value -- see `docs/adr/0001-architecture.md`) |
| Compliance catalog | `src/statute/facts.cljc` -- Companies Act 1994, Labour Standards Act (Chap. 89:05), Protection of Employment Act (Chap. 89:02) |
| Tests | `clojure -M:dev:test` |
| Demo | `clojure -M:dev:run` |
| Architecture ADR | [`docs/adr/0001-architecture.md`](docs/adr/0001-architecture.md) |

`:filing/submit` is never in any phase's `:auto` set -- human sign-off
is structural, not a rollout milestone.

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) -- the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set -- it always requires human sign-off.

## What this is NOT

- **Not the government of Dominica.** This blueprint is an
  independent operator the government contracts with or that bids into
  its procurement -- never the government itself, and never an official
  channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Dominica-licensed counsel
  or a registered agent where the law requires licensed representation.
- **Not the Citizenship by Investment (CBI) programme.** Dominica's
  CBI programme (administered by the Citizenship by Investment Unit,
  Ministry of Finance) is an individual citizenship/passport regime,
  investigated and deliberately excluded -- a different regulatory
  domain from business/company registration or public-procurement
  market entry.

## Capability layer

Required capabilities (`blueprint.edn`):

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Dominica:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
