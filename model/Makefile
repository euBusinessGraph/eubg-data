TTL    = identifiers.ttl identifiers2.ttl company.ttl
PUML   = $(patsubst %.ttl, %.puml, $(TTL))
PNG    = $(patsubst %.ttl, %.png, $(TTL))

all : $(PNG) ebg-ontology.ttl

%.puml : %.ttl
	rdfpuml.bat $<

%.png : %.puml
	puml.bat $<

ebg-ontology.ttl : prefixes.ttl ebg-ontology-part.ttl ebg-ontology-gen.ttl
	cat prefixes.ttl ebg-ontology-part.ttl ebg-ontology-gen.ttl > ebg-ontology.ttl
