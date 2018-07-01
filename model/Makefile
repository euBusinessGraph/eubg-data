IGNORE = prefixes.ttl ebg-ontology.ttl
TTL    = $(filter-out $(IGNORE), $(wildcard *.ttl))
PNG    = $(patsubst %.ttl, %.png, $(TTL))

all : $(PNG) 

%.puml : %.ttl
	rdfpuml.bat $<

%.png : %.puml
	puml.bat $<

#ebg-ontology.ttl : prefixes.ttl ebg-ontology-part.ttl ebg-ontology-gen.ttl
#	cat prefixes.ttl ebg-ontology-part.ttl ebg-ontology-gen.ttl > ebg-ontology.ttl
