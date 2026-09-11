(ns culture.facts
  "Country-level regional-culture catalog for Dominica (DMA) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). First facts namespace
  in this blueprint-stage repo; the marketentry/statute catalogs land with
  :implemented (ADR-2607141700). City-level counterparts live in the
  cloud-itonami-municipality-* repos. (Dominica, the island nation -- not
  the Dominican Republic, which is DOM.)

  Catalog is keyed by UPPERCASE ISO3 (mirrors the fleet's `statute.facts`
  convention); entries carry no :culture/municipality (that attribute is
  city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"DMA"
   [{:culture/id "dma.dish.callaloo"
     :culture/name "Callaloo"
     :culture/country "DMA"
     :culture/kind :dish
     :culture/summary "Soup of leafy greens (in Dominica primarily taro/dasheen bush), coconut milk and sometimes salted meat or seafood; one of the national dishes of Dominica (and of Trinidad and Tobago), found all around the Caribbean."
     :culture/url "https://en.wikipedia.org/wiki/Callaloo"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dma.dish.mountain-chicken"
     :culture/name "Mountain chicken"
     :culture/name-local "Crapaud"
     :culture/country "DMA"
     :culture/kind :dish
     :culture/summary "Meaty legs of the crapaud frog of Dominica and Montserrat, cooked in traditional Dominican dishes and until recently the country's national dish; the critically endangered species is now protected and near-extinct in the wild."
     :culture/url "https://en.wikipedia.org/wiki/Mountain_chicken"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dma.dish.green-fig-and-saltfish"
     :culture/name "Green fig and saltfish"
     :culture/country "DMA"
     :culture/kind :dish
     :culture/summary "Dish of green bananas and salted codfish seasoned with onions, peppers and garlic, part of Dominica cuisine."
     :culture/url "https://en.wikipedia.org/wiki/Dominica_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dma.beverage.kubuli"
     :culture/name "Kubuli beer"
     :culture/country "DMA"
     :culture/kind :beverage
     :culture/summary "Dominica brews its beer under the Kubuli label."
     :culture/url "https://en.wikipedia.org/wiki/Dominica_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dma.beverage.cacao-tea"
     :culture/name "Cacao tea"
     :culture/country "DMA"
     :culture/kind :beverage
     :culture/summary "Drink commonly served with breakfasts in Dominica, made by boiling cocoa sticks with cinnamon and bay leaves."
     :culture/url "https://en.wikipedia.org/wiki/Dominica_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dma.festival.world-creole-music-festival"
     :culture/name "World Creole Music Festival"
     :culture/country "DMA"
     :culture/kind :festival
     :culture/summary "Annual three-day music festival hosted on the island of Dominica during the final weekend in October, begun in 1997 during International Creole Month around the island's Independence celebrations."
     :culture/url "https://en.wikipedia.org/wiki/World_Creole_Music_Festival"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "dma.heritage.morne-trois-pitons"
     :culture/name "Morne Trois Pitons National Park"
     :culture/country "DMA"
     :culture/kind :heritage
     :culture/summary "National park in Dominica established in July 1975, the first legally established in the country, featuring the Boiling Lake and the Valley of Desolation; a UNESCO World Heritage Site since 1997."
     :culture/url "https://en.wikipedia.org/wiki/Morne_Trois_Pitons_National_Park"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-dma culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "DMA"))
                 " DMA entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
