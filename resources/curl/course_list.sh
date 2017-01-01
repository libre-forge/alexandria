curl -X POST -H "Content-Type: application/json" -d '
{
    "query": "query QueryByTopic($filter: CourseFilter){courses(filter: $filter){title}}",
     "variables": {
         "filter": {
            "byTopic": "data"
         }
     }
}' http://localhost:3030/graphql
