PREFIX org:    <http://www.w3.org/ns/org#>
PREFIX person: <http://www.w3.org/ns/person#>
PREFIX rov: <http://www.w3.org/ns/regorg#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX doo: <http://data.ontotext.com/ontology#>

# add type Person to officers who are not Companies
insert {?y a person:Person}
where {
  ?x org:member ?y.
  filter not exists {?x a rov:RegisteredOrganization}
};

# Remove erroneously inserted type Person
delete {?x a person:Person}
where {?x a rov:RegisteredOrganization};

# Move person name to person:birthName to conform to model
delete {?x skos:prefLabel ?y}
insert {?x person:birthName ?y}
where  {?x a person:Person; skos:prefLabel ?y};

# Create BG Roles concept scheme
base <http://data.businessgraph.io>
insert data {
  <role/BG> a skos:ConceptScheme;
    rdfs:label "BG embership Roles"
};

# Create concepts for BG roles (there are just 3)
insert {
  ?role a skos:Concept; 
    skos:inScheme <role/BG>;
    skos:prefLabel ?roleName
} where {
  values (?role ?roleName) {
    (doo:DirectorRole "Director")
    (doo:SoleCapitalOwnerRole "Sole Capital Owner")
    (doo:ManagerRole "Manager")
  }
}