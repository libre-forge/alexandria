curl -X POST -H "Content-Type: application/json" -d '
{
    "query": "mutation CreateCourse($course: CreateCourse) {
        course(course: $course) {id}
    }",
    "variables": {
       "course": {
           "title":"New Course",
           "description": "tiny description",
           "pitch": "my pitch",
           "created_by": "588034cb-4c84-4f36-abcf-fa511aba0de3",
           "member_limit": 2,
           "subjects": ["one", "two"]
        }
     }
}' http://localhost:3030/graphql
