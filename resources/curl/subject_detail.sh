curl 'http://localhost:3030/api/graphql' -H 'Origin: http://localhost:8080' -H 'content-type: application/json'  --data-binary '{"query":"query SubjectById($id: String) {\n  subject(id: $id) {resources\n {id}}\n}\n","variables":{"id":"588034cb-4c84-4f36-abcf-fa511aba0de3"}}' --compressed
