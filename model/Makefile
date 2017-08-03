TTL    = identifiers.ttl identifiers2.ttl company.ttl
PUML   = $(patsubst %.ttl, %.puml, $(TTL))
PNG    = $(patsubst %.ttl, %.png, $(TTL))

all : $(PNG) $(R2RML)

%.puml : %.ttl
	rdfpuml.bat $<

%.png : %.puml
	puml.bat $<

