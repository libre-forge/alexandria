curl -X GET -H "Content-Type: application/json" -H "Authorization: Token eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4ODAzNGNiLTRjODQtNGYzNi1hYmNmLWZhNTExYWJhMGRlMyJ9.6-2-0gIlDN5-R64MRL7VHSaTjQb5nf7rSWJxTBllH_4" -d '{"query": "query={users{name email}}"}' http://localhost:3030/api/graphql
