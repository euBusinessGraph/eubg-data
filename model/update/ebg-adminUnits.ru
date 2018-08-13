prefix ebg:     <http://data.businessgraph.io/ontology#>
prefix locn:    <http://www.w3.org/ns/locn#>
prefix spatial: <http://geovocab.org/spatial#>

# Transitively add higher adminUnitLm based on filled lower levels adminUnitLn (where m<n)
# We need these queries executed in order:
# L5 <- L6
# L4 <- L5
# L3 <- L4
# L2 <- L3
# L1 <- L2
# That's why we can't do it with VALUES that lists all pairs

insert {?adr ?level1 ?parent}
where {bind(ebg:adminUnitL5 as ?level1) bind(ebg:adminUnitL6 as ?level2)
  ?adr ?level2 ?child. ?child spatial:PP ?parent. filter not exists {?adr ?level1 []}};

insert {?adr ?level1 ?parent}
where {bind(ebg:adminUnitL4 as ?level1)  bind(ebg:adminUnitL5 as ?level2)
  ?adr ?level2 ?child.
  ?child spatial:PP ?parent. filter not exists {?adr ?level1 []}};

insert {?adr ?level1 ?parent}
where {bind(ebg:adminUnitL3 as ?level1)  bind(ebg:adminUnitL4 as ?level2)
  ?adr ?level2 ?child.
  ?child spatial:PP ?parent. filter not exists {?adr ?level1 []}};

insert {?adr ?level1 ?parent}
where {bind(locn:adminUnitL2 as ?level1)  bind(ebg:adminUnitL3 as ?level2)
  ?adr ?level2 ?child.
  ?child spatial:PP ?parent. filter not exists {?adr ?level1 []}};

insert {?adr ?level1 ?parent}
where {bind(locn:adminUnitL1 as ?level1)  bind(locn:adminUnitL2 as ?level2)
  ?adr ?level2 ?child.
  ?child spatial:PP ?parent. filter not exists {?adr ?level1 []}};

# Put the most specific adminUnitLn (max n) in ebg:adminUnit, used for faceting

insert {?adr ebg:adminUnit ?adm} where  {?adr ebg:adminUnitL6  ?adm filter not exists {?adr ebg:adminUnit []}}; 
insert {?adr ebg:adminUnit ?adm} where  {?adr ebg:adminUnitL5  ?adm filter not exists {?adr ebg:adminUnit []}}; 
insert {?adr ebg:adminUnit ?adm} where  {?adr ebg:adminUnitL4  ?adm filter not exists {?adr ebg:adminUnit []}}; 
insert {?adr ebg:adminUnit ?adm} where  {?adr ebg:adminUnitL3  ?adm filter not exists {?adr ebg:adminUnit []}}; 
insert {?adr ebg:adminUnit ?adm} where  {?adr locn:adminUnitL2 ?adm filter not exists {?adr ebg:adminUnit []}};
insert {?adr ebg:adminUnit ?adm} where  {?adr locn:adminUnitL1 ?adm filter not exists {?adr ebg:adminUnit []}};
