index.html: ebg-ontology.rdf extraction.xsl
	transform -xsl:extraction.xsl -s:ebg-ontology.rdf -o:index.html

# TODO: currently broken because riot emits <rdf:Description> rather than specific <Class> elements.
# Converted usign the page http://www.easyrdf.org/converter, but can't call it from the command line since the output is in some box, and syntax-highlighted.
# And it mangles or omits many namespaces, eg: vann->ns0; ebg->ns1; dbo,rov,xsd->(none), 
# So I need to find a more sustainable variant
ebg-ontology.rdf: ../model/ebg-ontology.ttl
	riot --output=rdfxml ../model/ebg-ontology.ttl > ebg-ontology.rdf

