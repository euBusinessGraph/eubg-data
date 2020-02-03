base <http://data.businessgraph.io/>
prefix void: <http://rdfs.org/ns/void#>
prefix rov: <http://www.w3.org/ns/regorg#>

insert {
  graph ?x {?x void:inDataset ?d}
} where {
  values (?g ?d) {
    (<provider/ocorp/uk> <dataset/OCORP/EBG>)
    (<provider/ocorp/de> <dataset/OCORP/EBG>)
    (<provider/bgtr>     <dataset/ONTO>)
    (<provider/brc>      <dataset/BRC>)
    (<provider/sdati/it> <dataset/SDATI/EBG>)
    (<provider/sdati/uk> <dataset/SDATI/EBG>)
  }
  graph ?g {?x a rov:RegisteredOrganization}
}
