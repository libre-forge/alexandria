curl -X POST -H "Content-Type: application/json" -d '
{
    "query": "mutation CreateNewSubject($subject: CreateSubject!) {
        subject(subject: $subject) {
          id
          title
          created_by {
            email
          }
        }
    }",
    "variables": {
       "subject": {
           "course": "a4d145c8-df92-4f13-aca0-12fb0a257df2",
           "title":"Introduction",
           "entry_order": 0,
           "description": "This chapter will introduce you in the basics of...",
           "created_by": "588034cb-4c84-4f36-abcf-fa511aba0de3"
        }
     }
}' http://localhost:3030/graphql
