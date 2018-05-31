TTL    = identifiers.ttl identifiers2.ttl identifier-GUID.ttl company.ttl
PUML   = $(patsubst %.ttl, %.puml, $(TTL))
PNG    = $(patsubst %.ttl, %.png, $(TTL))

all : $(PUML) $(PNG) ebg-ontology.ttl

%.puml : %.ttl
	rdfpuml.bat $<

%.png : %.puml
	puml.bat $<

#ebg-ontology.ttl : prefixes.ttl ebg-ontology-part.ttl ebg-ontology-gen.ttl
#	cat prefixes.ttl ebg-ontology-part.ttl ebg-ontology-gen.ttl > ebg-ontology.ttl
