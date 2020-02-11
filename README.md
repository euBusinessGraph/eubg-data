# Semantic Data Model (Ontology) for Company Data
The [euBusinessGraph project](http://www.eubusinessgraph.eu/) aims at simplifying cross-border and cross-lingual collection, reconciliation, aggregation and analysis of company-related information from several authoritative and non-authoritative sources.

The euBusinessGraph has drawn on the experience of its data providers and technology providers to tackle the complex task of combining company data from multiple sources. We have defined a common semantic model (ontology) to represent companies and their attributes in a consistent way.

* Based on project needs and provider datasets
* Rooted in and reuses existing ontologies and datasets
* Expressed in comprehensive EBG Semantic Model doc
* Formalized as ebg: ontology using schema:(domain|range)Includes
* Also defines URL patterns and authorities/lookup lists to use
* Will be validated with RDF Shapes

## Common Semantic Model (ontology)
We created an initial company data model considering related works, data available from the partners, and the needs of their business cases. The model covers the following requirements:

* Capture the concept of a company and represent different types of companies.
* Represent company jurisdictions and registration information.
* Capture company contact information, such as the address and other locations.
* Capture social data of companies, such as their websites (together with Web languages), RSS/Atom feeds and Wikipedia URLs.
* Answer if a company is publicly traded or not, if it is state owned or not, and if it is registered in a startup register.
* Support languages: EN, IT, NO.

In developing the company data model we have reused from appropriate ontologies such as:

* EU Core Vocabs: W3C Org, RegOrg, Location, Person (not W3C)
* schema.org: widely used, some relevant properties (e.g., dates)
* ADMS: datasets and identifiers

![Figure 1: Towards a common semantic model for company data](http://www.eubusinessgraph.eu/wp-content/uploads/2018/06/ontology-approach.png)

Since none of the existing ontologies covers the complete scope we need, we reuse where possible and extend and compose by:

* Add some classes and properties of our own (ebg: ontology)
* Use schema:(domain|range)Includes instead of rdfs:(domain|range) for easier composition (polymorphic vs monomorphic)

In addition we define RDF Shapes (SHACL and ShEx) to validate incoming data.

### Company data
In its first release, the model focuses on capturing key company information present in official registers such as legal name, registered address and economic classification, and also information coming from online resources related to the company such as company websites, blogs and social media accounts. These aspects are explicitly incorporated into the model and describe company information that is shared across data providers and directly accessible through the graph. Additionally, the model supports advertising other company related information available from data providers directly.

![Figure 2: Company data attributes that are covered by the model](http://www.eubusinessgraph.eu/wp-content/uploads/2018/06/ontology-company.png)

### Identifier System
We have performed a thorough analysis of identifiers in the context of euBusinessGraph. From the analysis of the different identifier systems and the requirements of the business cases of the project, we singled out key aspects about identifiers and addressed them in the common semantic model.

Achieving matching and reconciliation across jurisdictions and registers requires careful modelling of identifier use. This release models the different cases through properties that describe the lifecycle of each identifier issued and by encoding a series of characteristics of the identifier system to which the identifier belongs. We follow a pragmatic approach when describing identifier systems in terms of these characteristics.

We model expectations of a particular system that should help determine to which extent an indicator can be used for matching and reconciliation. Additionally, we model web resources that are frequently found for identifier systems such as search endpoints, templates for building identifier URLs through which company information can be reached and other resources that describe the system’s rules. Finally, the model supports the representation of the different agents that are in charge of setting and maintaining rules, issuing identifiers and publishing identifier databases.

![Figure 3: Identifier System attributes that is covered by the modelFurther information](http://www.eubusinessgraph.eu/wp-content/uploads/2018/06/ontology-identifier-system.png)

# GitHub Repository
This repository contains the sources for the euBusinessGraph Semantic Model for representing company-related data. Here we will keep:

* Prefixes file
* Instance model file in Turtle format
* Instance model files for diagrams
* Generated ontology file in RDF format
* Generated online documentation using LODE
* RDF shapes for validation
* RDF data (e.g. NACE csv sheet), conversion scripts and resulting RDF
* Diagrams for the master document (links to full-size diagrams and source files here)

# References
For further details about the euBusinessGraph ontology:

* [Semantic Data Model master document on Google Docs](https://docs.google.com/document/d/1dhMOTlIOC6dOK_jksJRX0CB-GIRoiYY6fWtCnZArUhU/edit)
* [Ontology documentation generated using pyLODE](https://rawcdn.githack.com/euBusinessGraph/eubg-data/master/ontology/doc.html)
* [Ontology introduction on the euBusinessGraph project website](http://www.eubusinessgraph.eu/eubusinessgraph-ontology-for-company-data)
