curl -X POST -H "Authorization: Token eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4ODAzNGNiLTRjODQtNGYzNi1hYmNmLWZhNTExYWJhMGRlMyJ9.6-2-0gIlDN5-R64MRL7VHSaTjQb5nf7rSWJxTBllH_4" -H "Content-Type: application/json" -d '
{
    "query": "mutation CreateCourse($course: CreateCourse!) {
        course(course: $course) {id}
    }",
    "variables": "{
       \"course\": {
           \"title\":\"New Course\",
           \"description\": \"tiny description\",
           \"pitch\": \"my pitch\",
           \"created_by\": \"588034cb-4c84-4f36-abcf-fa511aba0de3\",
           \"member_limit\": 2,
           \"subjects\": [\"one\", \"two\"]
        }
     }"
}' http://localhost:3030/graphql
