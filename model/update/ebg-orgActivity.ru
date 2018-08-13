# Compute ebg:orgActivityTransitive as transive closure of rov:orgActivity along the skos:broader hierarchy"

prefix ebg:     <http://data.businessgraph.io/ontology#>
prefix skos:    <http://www.w3.org/2004/02/skos/core#> 
prefix rov:     <http://www.w3.org/ns/regorg#>

insert {?x ebg:orgActivityTransitive ?y}
where {?x rov:orgActivity|(rov:orgActivity/skos:broader+) ?y}