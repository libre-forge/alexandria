curl -X POST -H "Content-Type: application/json" -d '
{
    "query": "mutation CreateNewResource($resource: CreateResource!) {
        resource(resource: $resource) {
          id
          title
          created_by {
            email
          }
        }
    }",
    "variables": {
       "resource": {
           "subject": "ae04b5e9-fd7f-4775-8e8b-96835ba08a87",
           "title":"Introduction Resource",
           "type": "image",
           "uri": "https://www.graphqlhub.com/images/graphqlhub-logo.png",
           "description": "This chapter will introduce you in the basics of...",
           "created_by": "588034cb-4c84-4f36-abcf-fa511aba0de3"
        }
     }
}' http://localhost:3030/graphql
