# BaSyx AAS Query Language Example

This example provides a setup for using the AAS Query Language with the BaSyx off-the-shelf components.
It includes some preconfigured AAS with multiple submodels and example queries to get you started.

The query functionality in BaSyx leverages Elasticsearch as the underlying search engine for querying AAS, Submodels, Concept Descriptions and Descriptors.

## How to run the BaSyx + AAS Query Language Example

1. Open a terminal in this folder
2. Run the following command to start the BaSyx containers:

```bash
docker-compose up -d
```

> To run the example containers, you need to have Docker installed on your device.

## View the working Example

To see the working example, open the [BaSyx AAS Web UI](http://localhost:3000) and navigate to the `AAS Query Language` Module. There you can see a select box for choosing which component to send queries to and a text area for entering the query.

## Having a look at Elasticsearch

To explore the Elasticsearch instance used in this example, you can access the Kibana interface at [http://localhost:5601](http://localhost:5601).
This will allow you to visualize and interact with the data stored in Elasticsearch. To do so, you need to log in with the following credentials:

- Username: `elastic`
- Password: `vtzJFt1b`

## Example Queries

Here are some example queries you can use to get started:

<details>
<summary>Get all AAS with an idShort containing "Test", or a revision smaller than 11, or an globalAssetId not equal to assetKind or including a submodel with the idShort "BillOfMaterial"</summary>

```json
{
  "$condition": {
    "$or": [
      {
        "$contains": [
          {
            "$field": "$aas#idShort"
          },
          {
            "$strVal": "Test"
          }
        ]
      },
      {
        "$lt": [
          {
            "$numCast": {
              "$field": "$aas#administration.revision"
            }
          },
          {
            "$numVal": 11
          }
        ]
      },
      {
        "$ne": [
          {
            "$field": "$aas#assetInformation.globalAssetId"
          },
          {
            "$field": "$aas#assetInformation.assetKind"
          }
        ]
      },
      {
        "$contains": [
          {
            "$field": "$aas#submodels.keys[]"
          },
          {
            "$strVal": "BillOfMaterial"
          }
        ]
      }
    ]
  }
}
```

</details>

<details>
<summary>Get all Submodels with kind "Instance", revision equal to 9, version not equal to revision, and semanticId containing "SubmodelTemplates"</summary>

```json
{
  "$condition": {
    "$and": [
      {
        "$eq": [
          {
            "$field": "$sm#kind"
          },
          {
            "$strVal": "Instance"
          }
        ]
      },
      {
        "$eq": [
          {
            "$numCast": {
              "$field": "$sm#administration.revision"
            }
          },
          {
            "$numVal": 9
          }
        ]
      },
      {
        "$ne": [
          {
            "$field": "$sm#administration.version"
          },
          {
            "$field": "$sm#administration.revision"
          }
        ]
      },
      {
        "$contains": [
          {
            "$field": "$sm#semanticId.keys[]"
          },
          {
            "$strVal": "SubmodelTemplates"
          }
        ]
      }
    ]
  }
}
```

</details>

<details>
<summary>Get all Concept Descriptions with English description, revision equal to 9, and isCaseOf containing "acplt"</summary>

```json
{
  "$condition": {
    "$match": [
      {
        "$eq": [
          {
            "$field": "$cd#description[].language"
          },
          {
            "$strVal": "en-us"
          }
        ]
      },
      {
        "$eq": [
          {
            "$numCast": {
              "$field": "$cd#administration.revision"
            }
          },
          {
            "$numVal": 9
          }
        ]
      },
      {
        "$contains": [
          {
            "$field": "$cd#isCaseOf.keys[]"
          },
          {
            "$strVal": "acplt"
          }
        ]
      }
    ]
  }
}
```

</details>

<details>
<summary>Get all AAS Descriptors with localhost endpoints, assetKind "Instance", and idShort ending with "Mandatory"</summary>

```json
{
  "$condition": {
    "$match": [
      {
        "$contains": [
          {
            "$field": "$aasdesc#endpoints[].protocolInformation.href"
          },
          {
            "$strVal": "localhost"
          }
        ]
      },
      {
        "$eq": [
          {
            "$field": "$aasdesc#assetKind"
          },
          {
            "$strVal": "Instance"
          }
        ]
      },
      {
        "$ends-with": [
          {
            "$field": "$aasdesc#idShort"
          },
          {
            "$strVal": "Mandatory"
          }
        ]
      }
    ]
  }
}
```

</details>

<details>
<summary>Get all Submodel Descriptors with localhost endpoints, revision equal to 9, and idShort not equal to semanticId</summary>

```json
{
  "$condition": {
    "$match": [
      {
        "$contains": [
          {
            "$field": "$smdesc#endpoints[].protocolInformation.href"
          },
          {
            "$strVal": "localhost"
          }
        ]
      },
      {
        "$eq": [
          {
            "$field": "$smdesc#administration.revision"
          },
          {
            "$numVal": 9
          }
        ]
      },
      {
        "$ne": [
          {
            "$field": "$smdesc#idShort"
          },
          {
            "$field": "$smdesc#semanticId.keys[]"
          }
        ]
      }
    ]
  }
}
```

</details>
