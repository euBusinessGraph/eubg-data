base        <http://data.businessgraph.io/>
prefix dct: <http://purl.org/dc/terms/> 

delete {
  ?x dct:creator <register/BG>
}
insert {
  ?x dct:creator <https://www.registryagency.bg>; dct:isPartOf <identifier/BG>
}
where {
  ?x dct:creator <register/BG>
};

base        <http://data.businessgraph.io/>
delete {
  ?x dct:creator <register/BG/GUID>
}
insert {
  ?x dct:creator <https://www.registryagency.bg>; dct:isPartOf <identifier/BG/GUID>
}
where {
  ?x dct:creator <register/BG/GUID>
};
