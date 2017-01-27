curl -X POST -H "Content-Type: application/json" -d '
{
    "query": "query QueryByTopic($filter: CourseFilter, first: Int, after: String){
                    courses(filter: $filter, first: $first, after: $after){
                       title
                    }
               }",
     "variables": {
         "filter": {
            "byTopic": "data"
         }
     }
}' http://localhost:3030/graphql
