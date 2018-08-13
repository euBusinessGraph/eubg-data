insert {?x ebg:foundingYear ?year}
where {
  ?x schema:foundingDate ?date
  bind(xsd:integer(substr(str(?date),1,4)) as ?year)
};

insert {?x ebg:dissolutionYear ?year}
where {
  ?x schema:dissolutionDate ?date
  bind(xsd:integer(substr(str(?date),1,4)) as ?year)
};

