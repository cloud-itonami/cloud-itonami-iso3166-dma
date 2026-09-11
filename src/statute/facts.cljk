(ns statute.facts
  "General-law compliance catalog for the Commonwealth of Dominica (DMA)
  -- extends this repo's existing `marketentry.facts` (public-
  procurement market-entry only, narrow scope) with a second, orthogonal
  catalog of statutes a company operating in this jurisdiction must
  generally track for compliance. Mirrors cloud-itonami-iso3166-jpn/-deu/
  -bgr/-aze/-alb/-arm/-atg's `statute.facts` (ADR-2607141700,
  cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL Dominica government-hosted URL -- never
  fabricated. Dominica's official consolidated-law host is
  `dominica.gov.dm/laws/...` (the same host the government's own
  `www.dominica.gov.dm/laws-of-dominica` search page links out to). All
  three entries below were fetched directly via curl (no TLS or
  JS-rendering blocker was hit for any `.gov.dm` host this iteration
  tried, unlike several other small-jurisdiction law portals prior
  iterations of this loop hit) and their text read:

  - Companies Act, No. 21 of 1994 -- the PDF at `dominica.gov.dm/laws/
    1994/act21-1994.pdf` is a SCANNED image-only PDF (no text layer;
    `pdftotext` returned empty output). This iteration rendered page 1
    to a PNG with `gs` and ran `tesseract` OCR directly on it, confirming
    the Act's own 'ARRANGEMENT OF SECTIONS' table of contents: 'PART I
    FORMATION AND OPERATION OF COMPANIES / DIVISION A: INCORPORATION OF
    COMPANIES / 4. Incorporation. ... Certificate of Incorporation / 8.
    Certificate of incorporation.' -- the same section-8-certificate
    shape this catalog's ATG sibling documents for its own (different)
    Companies Act, consistent with both being OECS-region companies
    legislation. CIPO (`cipo.gov.dm/legislation/companies`, fetched
    directly) links out to this exact PDF as the current, in-force
    Companies Act.
  - Labour Standards Act, Chapter 89:05 (originally Act 2 of 1977,
    amended by 36 of 1983, 12 of 1990 and 16 of 1991) -- the PDF at
    `dominica.gov.dm/laws/chapters/chap89-05.pdf` DOES carry a genuine
    text layer (an I.R.I.S.-OCR'd scan, `pdftotext -layout` output
    directly read): its own enacting clause reads 'AN ACT to provide for
    the fixing of wages of workers, the hours of work, their leave and
    generally for matters pertaining to the welfare of workers', with
    sections covering minimum wage proclamation, hours of work,
    overtime, weekly rest, annual vacation leave and maternity leave.
  - Protection of Employment Act, Chapter 89:02 (originally Act 1 of
    1977, amended by 30 of 1978, 4 of 1983 and 23 of 1984) -- the PDF at
    `dominica.gov.dm/laws/chapters/chap89-02.pdf` (also I.R.I.S.-OCR'd,
    text layer read directly via `pdftotext -layout`) covers termination
    of employment, probation, dismissal for serious misconduct/written
    warnings, and reduction-in-workforce (redundancy) procedure -- a
    SEPARATE, complementary statute to the Labour Standards Act rather
    than a single consolidated labour code (unlike ATG's own single
    consolidated Labour Code, CAP. 27); together the two statutes cover
    the same 'wages/hours/leave' + 'termination/redundancy' ground ATG's
    one consolidated code does.
  - This iteration also specifically searched (via the government
    portal's own `laws-of-dominica` title-search form) for a Dominica
    Data Protection Act, the third statute this catalog's ATG sibling
    carries -- 'data protection' and 'data' both returned ZERO results.
    Rather than infer or invent one, this catalog HONESTLY carries only
    two DMA statutes, not three -- the same honest-scope-narrowing
    discipline this family's siblings apply when a genuinely-searched-
    for law cannot be found.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"DMA"
   [{:statute/id "dma.companies-act"
     :statute/title "Companies Act, 1994"
     :statute/jurisdiction "DMA"
     :statute/kind :law
     :statute/law-number "Act No. 21 of 1994"
     :statute/url "https://dominica.gov.dm/laws/1994/act21-1994.pdf"
     :statute/url-provenance :official-dominica-gov-dm
     :statute/enacted-date "1994-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "dma.labour-standards-act"
     :statute/title "Labour Standards Act"
     :statute/jurisdiction "DMA"
     :statute/kind :law
     :statute/law-number "Chapter 89:05 (originally Act 2 of 1977, amended by 36 of 1983, 12 of 1990 and 16 of 1991)"
     :statute/url "https://dominica.gov.dm/laws/chapters/chap89-05.pdf"
     :statute/url-provenance :official-dominica-gov-dm
     :statute/enacted-date "1977-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment}}
    {:statute/id "dma.protection-of-employment-act"
     :statute/title "Protection of Employment Act"
     :statute/jurisdiction "DMA"
     :statute/kind :law
     :statute/law-number "Chapter 89:02 (originally Act 1 of 1977, amended by 30 of 1978, 4 of 1983 and 23 of 1984)"
     :statute/url "https://dominica.gov.dm/laws/chapters/chap89-02.pdf"
     :statute/url-provenance :official-dominica-gov-dm
     :statute/enacted-date "1977-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment :termination}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-dma statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "DMA")) " DMA statutes seeded with an "
                 "official government-hosted citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :termination)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
