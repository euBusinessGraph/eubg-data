# NUTS
I downloaded the data in RDF from [here|http://nuts.geovocab.org/]. 
The dump includes data for 34 countries. According to [wikipedia|https://en.wikipedia.org/wiki/Local_administrative_unit]
we should have data for 35 countries (28 members, 4 candidates, 3 EFTA). Montenegro is missing.
```
@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix nuts: <http://nuts.geovocab.org/id/> .
@prefix ramon: <http://rdfdata.eionet.europa.eu/ramon/ontology/> .
@prefix ngeo: <http://geovocab.org/geometry#> .
@prefix spatial: <http://geovocab.org/spatial#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix dc: <http://purl.org/dc/elements/1.1/> .

nuts:FI rdf:type ramon:NUTSRegion .
nuts:FI rdfs:label "FI - SUOMI / FINLAND" .
nuts:FI ramon:name "SUOMI / FINLAND" .
nuts:FI ramon:level "0"^^<http://www.w3.org/2001/XMLSchema#integer> .
nuts:FI ramon:code "FI" .
nuts:FI ngeo:geometry nuts:FI_geometry .

```
# Run scripts
```bash
bash loop.sh convert-lau1.tarql ../codes.tsv ../data/LAU1/ ../rdf/ lau4
bash loop.sh convert.tarql ../codes.tsv ../data/LAU2/ ../rdf/ lau5 ","
```
# LAU 1 
LAU 1 is the same as NUTS3 for these countries:
* AT
* BE
* HR
* ES
* IT
* NL
* RO
* SE
For all other countries the LAU1 codes is different from NUTS3 and should be extracted.
## Extraction based on LAU2 with largest population
There's no data published for LAU1 holding both the name of the region and it's code.
We decided to use the name of the largest LAU2 region in terms of population as name for the LAU1 name. 
Examples with BG: 
* BG341	BGS01	00151	no	Айтос	Aytos	19462	79033000 - is the largest LAU2 and indeed Aytos should be the name of the LAU1 region
* BG341	BGS04	07079	30/06/2015	Бургас	Burgas	203017	284442000 - is the largest LAU2 and indeed Burgas should be the name of the LAU1 region

Evaluation:
* AT - not needed 
* BE - not needed
* BG - done
* CY - no population (seek different source)
* CZ - done
* DE - 1348 / 1457 ; extracted / actual
* DK - done
* EE - done
* EL - 326 / 1034
* ES - not needed
* FI - 70 / 77 
* FR - no population (seek different source)
* HR - not needed
* HU - 175 / 174 
* IE - no population (seek different source)
* IT - not needed
* LT - 13 / 33 (Lots of n.a. - seek different source)
* LU - 12 / 13
* LV - not needed (no LAU1 code in the LAU2 file)
* MT - 6 / 68 (not lau2 according to wiki ??)
* NL - not needed 
* PL - 380 / 379
* PT - done
* RO - not needed
* SE - done
* SI - done
* SK - done
* UK - 406 / 443
## Seeking of different sources
* CY - Match by hand (DONE)
* FR - Use of wikidata. 3515 Results of 3785
```sparql
prefix ramon:  <http://rdfdata.eionet.europa.eu/ramon/ontology/>
construct
{
  ?lau1 ramon:name ?label;
        rdfs:label ?newLabel.
}
where{
      ?s wdt:P2506 ?o;
         rdfs:label ?label.
       BIND(uri(concat("http://data.businessgraph.io/lau/fr/", ?o)) as ?lau1)
       BIND(concat(?o, " ", IF(lang(?label) = 'en', ?label, ?undef)) as ?newLabel)
       filter(lang(?label) = 'en' || lang(?label) = 'fr')
}
```
* IE - Match by hand. According to [wikipedia|https://en.wikipedia.org/wiki/Local_administrative_unit]
there should be 34 LAU1 codes. In the eurostat data there are only 6 LAU1 codes. 
There are 8 NUTS3 codes. This leads to the conclusion that LAU1 isn't subregion of NUTS3.
Probably should bypass it.
[IE lau|https://en.wikipedia.org/wiki/Local_government_in_the_Republic_of_Ireland].
* LT - We'll skip these
* IS - [LAU1](https://en.wikipedia.org/wiki/Regions_of_Iceland), [LAU2](https://en.wikipedia.org/wiki/Municipalities_of_Iceland)
* ME - [NUTS](https://en.wikipedia.org/wiki/NUTS_of_Montenegro), [LAU1](https://en.wikipedia.org/wiki/Municipalities_of_Montenegro)
* TR - We weren't able to find structured LAU data for Turkey. 
Thus we crawled the [Municipalities TR](https://en.wikipedia.org/wiki/List_of_municipalities_in_Turkey) and it's subpages. 
From them we extracted the hierarchy. At the government site there's a [list of IDs](https://biruni.tuik.gov.tr/DIESS/FileDownload/Yayinlar/Siniflamalar/IBBS_2010.xls) including NUTS + LAU1.
The Districts(LAU1) extracted from wiki were mapped to the excel above and the LAU1 ID was used. 737 Districts were mapped to the LAU code. 
The others were given an id starting from 9901 to 9995.
As we couldn't find LAU2 IDs we applied incremental ids from 99901 to 102644.
* LI - [LAU1+LAU2](https://en.wikipedia.org/wiki/Municipalities_of_Liechtenstein)
* NO - [LAU2](https://en.wikipedia.org/wiki/List_of_municipalities_of_Norway); [cleanded data](https://docs.google.com/spreadsheets/d/14_dAPjSz1Rarwi1JZDDZ2Q9dSIHSXd2ede5wwqKOPlE/edit#gid=0)
* CH -  We used the official [register](https://www.bfs.admin.ch/bfs/fr/home/bases-statistiques/repertoire-officiel-communes-suisse.assetdetail.2245009.html)
# LAU 2 
Countries:
* AT
* BE
* BG
* CY
* CZ
* DE
* DK
* EE
* EL
* ES
* FI
* FR
* HR
* HU
* IE
* IT
* LT
* LU
* LV
* MT
* NL
* PL
* PT
* RO
* SE
* SI
* SK
* UK

## Column name differences
* DK - adds new column "LAU1_NAME"
* IT - instead of "POP" column name is "POP 2015"
* UK
  * LAU1_NAT_CODE_NEW - new column - is blank for a lot of cases. Will use the old one
  * LAU2_NAT_CODE_NEW - new column - is blank for a lot of cases. Will use the old one
  * instead of NUTS_3 there are 2 new columns "NUTS3_13" and "NUTS3_10".
This is due to changes in the NUTS classification. The NUTS classification was introduced in 2006.
In 2010 and 2013 changes were made to the UK regions - [reference](https://en.wikipedia.org/wiki/NUTS_statistical_regions_of_the_United_Kingdom#NUTS_2013).
We'll use NUTS3_13 as latest present identificator.

## Column eval
* NUTS_3 - blanks: "0", ""
* LAU1_NAT_CODE - blanks are: "n.a.", "", "no" - not unique between countries
* LAU2_NAT_CODE - has duplicates for DK - not unique between countries.
-----------------------
| LAU2_NAT_CODE | num |
-----------------------
| "7964"        | 2   |
| "8190"        | 2   |
| "8981"        | 2   |
| "8895"        | 2   |
| "8499"        | 2   |
| "8801"        | 2   |
| "9251"        | 3   |
| "7427"        | 2   |
| "9999"        | 94  |
| "7422"        | 2   |
| "8448"        | 2   |
| "8444"        | 2   |
| "8005"        | 2   |
| "9028"        | 2   |
| "9023"        | 2   |
| "7898"        | 2   |
| "7899"        | 2   |
| "8523"        | 2   |
| "7544"        | 2   |
| "8524"        | 2   |
| "8521"        | 3   |
| "7541"        | 2   |
| "8217"        | 2   |
| "8036"        | 2   |
| "8912"        | 2   |
| "8739"        | 2   |
| "7449"        | 2   |
| "8113"        | 2   |
| "8111"        | 2   |
| "8025"        | 2   |
-----------------------

For DK LAU1+LAU2 seem to make uniq ID,  so we use the following URLs for DK LAU2: lau:DK-[lau1]-[lau2]
* NAME_1 - strings
* NAME_2_LAT - strings
* POP - numeric, blanks: "n.a.", "", make regex filter only digits
* AREA - same as POP



