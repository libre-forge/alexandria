curl -i -X POST -H "Content-Type: application/json" -d '
{
  "query": "query CourseMembers($course: String){courseMembers(course: $course){id\nname}}",
  "variables": {
    "course": "a4d145c8-df92-4f13-aca0-12fb0a257df2"
  }
}
' http://localhost:3030/graphql
