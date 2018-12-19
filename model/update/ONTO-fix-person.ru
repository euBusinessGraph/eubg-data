prefix org:    <http://www.w3.org/ns/org#>
prefix person: <http://www.w3.org/ns/person#>

insert {?y a person:Person}
where {?x org:member ?y}