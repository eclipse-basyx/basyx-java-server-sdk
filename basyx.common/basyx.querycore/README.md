# Implementation of the AASQL and Atribute Based Access Control in BaSyx Java

## Summary

* The standardized components of the AAS need to be secured as defined in the [AAS specification Part 4](https://industrialdigitaltwin.io/aas-specifications/IDTA-01004/v3.0/index.html)
* The choice of the security model is based on the AASQL and Attribute Based Access Control (ABAC)
* The currently implemented Role Based Access Control (RBAC) will be handled as a subset of the ABAC
* The authentication flow is not defined by the specificatiion and can be chosen by the implementer (in our case the OAuth2.0 protocol is used)
* The exchange of access rules should be possible (the [JSON schema serialization](https://industrialdigitaltwin.io/aas-specifications/IDTA-01004/v3.0/access-rule-model.html#json-serialization) of rules is reccommended by the specification)
* An identity provider is used to manage the users and their attributes (in our case Keycloak is used)
* The acces rule model applies to all types of repositories and registries in the context of the AAS (AAS Discovery Service is not mentioned here!)
* The AAS Query Language (AASQL) and the AAS Access Rules share the same BNF (Backus-Naur-Form) grammar for formula expressions
* Examples for Access Rules in JSON serialization can be found [here](https://industrialdigitaltwin.io/aas-specifications/IDTA-01004/v3.0/annex/json-access-rule-examples.html)
* The `/query` endpoint of the respective component is used to query the Access Rule(s) by using the AASQL
* AAS queries are sent via a POST request to the `/query` endpoint including the AASQL query in the body of the request
* For the AAS Repository, the endpoint looks like this: `.../query/shells`
* The query language is specified as part of the [AAS specification Part 2](https://industrialdigitaltwin.io/aas-specifications/IDTA-01002/v3.2/query-language.html)
* The Rest API for querying is defined in the [HTTP/REST API](https://industrialdigitaltwin.io/aas-specifications/IDTA-01002/v3.2/http-rest-api/http-rest-api.html#_querying)
* From a technical point of view, ElasticSearch (ES) will be used to index AAS/Submodels/ConceptDescriptions and Descriptors
* ES will constantly be synchronized with the respective BaSyx components
* The JSON serialized query (send via the `/query` endpoint) is translated into an ES query
* The ES query is executed and the results are returned to the client

## Implementation of the AASQL

* [ ] Integrate ES component into Docker Compose
* [ ] Define test cases first
* [ ] Spring configuration has to be adapted -> AASQL and ES have to be active at the same component (see gateway for reference)
* [ ] Implement mechanism to synchronize AAS/Submodels/ConceptDescriptions and Descriptors with ES
* [ ] Implement AASQL query parser
* [ ] Implement `/query` endpoint for AASQL queries in the respective components

## Implementation of ABAC

## Challenges

* ES and the respective backend have to be kept in sync at all times (maybe cron job each day to correct if faults happen)
* Synchronization will happen in java (decorator pattern)
* Testing is key!

## Assumptions

* All BaSyx components need ABAC and AASQL (even Discovery)
* ABAC needs active AASQL and ES
* The AAS Repo will be the first component to implement AASQL and ABAC (as a proof of concept)

## Risks

* Errors can be catched in the decorated functions (transactional)
* Security needs to be considered -> Just query id of elements and then go to the MongoDB


## TODOS

* [x] Extract Query Package from basyx.core to own module (e.g. basyx.querycore)
* [x] Comparison of two fields is not possible yet
* [x] Fix Error: java.lang.ClassNotFoundException: co.elastic.clients.elasticsearch._types.ScriptSource$Builder in ValueConverter during Runtime
* [x] Fix configuration: ES Feature starts for AAS Repo even if it is not configured
* [x] In Registries, add ES feature to release POMs (only for the variants with MongoDB!)
* [x] Fix error: When comparing fields -> first check with a bool.must if the fields are there. (See ChatGPT Chat -> Ask Claude)
* [x] Pagination
* [x] Empty elements should not be included (empty arrays)
* [x] Configurable Index Names
* [x] Implement Casting Operators
* [x] Unit Test
* [x] !Priority! Duplicate search module for the missing components
* [ ] Integration Tests
* [ ] Validate Expected Queries (unformatted ones) -> Depends on other components to utilize the AASQL
* [x] Make the following SME Props queryable: value, valueType, semanticId, idShort
* [x] Make other Components (AAS,CD) also fetch Data from MongoDB not ES
* [ ] Note down that comparison operators don't work for SME filtering in SM Repo without idShortPath
* [x] Handle Search Queries with SML (Indices)
* [ ] Handle error case when Query is not valid (e.g.  "$field: $sme", "$field: $sme.smc.smc.propa")
* [x] License Header hinzufügen
* When querying for (supplemental-)semanticId Keys append with .value 