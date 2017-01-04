curl -X POST -H "Content-Type: application/json" -d '
{
    "query": "query { __schema { types {name kind} }}"
}' http://localhost:3030/graphql
