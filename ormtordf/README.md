This Scala script converts Object-Role Modeling (ORM) models in XML format (written in NORMA https://www.ormfoundation.org/files/folders/norma_the_software/default.aspx) into RDF schemas in Turtle format. Because ORM models often use different naming conventions than RDFs, the ORM model should be annotated with key value pairs of the form "key -> value" as notes in the ORM model. These annotations are used to convert the names of types and facts in ORM to names of classes and properties in RDFS, and to add additional properties to the generated RDFS file. Annotations whose key is "rdfs" are always used to convert the name of ORM types and facts into RDFS names. All other annotations are used in order to generate additional properties.

To test the script, run the Scala object OrmXmlToRDFS with input arguments "example/ebg-ontology.orm example/ebg-ontology.ttl". When executed, the script will read the example/ebg-ontology.orm ORM model file, convert it to RDFS, and write the output to the file example/ebg-ontology.ttl (in Turtle format).