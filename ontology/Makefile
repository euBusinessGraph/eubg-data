index.html: ebg-ontology.rdf extraction.xsl
	transform -xsl:extraction.xsl -s:ebg-ontology.rdf -o:index.html

ebg-ontology.rdf: ../model/ebg-ontology.ttl
	riot --output=rdfxml ../model/ebg-ontology.ttl > ebg-ontology.rdf

