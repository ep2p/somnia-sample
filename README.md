# somnia-sample

Small sample application made on top of Somnia to show an example.

## How to run

You need to run two or more nodes of this application, then store data or retrieve data from it.

Requirements:
- Java 8+
- maven
- Running MongoDB instance

### Get the project

Clone the project to your system and go to the main directory of project (where POM file is)

### Make and run node 1 (ID: 2)
```sh
 mvn spring-boot:run -Dspring-boot.run.arguments="--nodeId=2 --server.port=8002 --server.host=localhost"
```
This builds project and runs the server on port `8002`.
We are not making any changes to mongodb, so make sure mongo is accessible on localhost and default port or pass the arguments that you need.
A database with name `somnia-sample-1` will be created in your mongoDB instance

### Make and run node 2 (ID: 3)
```sh
mvn spring-boot:run -Dspring-boot.run.arguments="--nodeId=3 --server.port=8003 --server.host=localhost --spring.data.mongodb.database=somnia-sample-2"
```
We run the second node with ID of `3` on port `8003`.
Note that we are passing different mongodb database name to this one.

### Bootstrap nodes

Lets bootstrap node 2 with 3:

```shell
curl --request POST \
  --url http://localhost:8002/api/bootstrap/3 \
  --header 'Content-Type: application/json' \
  --data '{
	"address": "http://localhost:8003"
}'
```

### Store

Lets store data using node 2:

```shell
curl --request POST \
  --url http://localhost:8002/storage/store/3 \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "Michel",
	"lastname": "Scott"
}'
```

The **key** we chose for the data is `3` as you see in the url.
Id of `3` is closer to node `3` rather than node `2`, and thats where we are expecting data to be persisted.

Check the mongodb database of node 3 to make sure data is persisted.

### Get (Retrieve)

Now lets get data of ID `3` from node `2`

```shell
curl --request GET \
  --url http://localhost:8002/storage/get/3 \
  --header 'Content-Type: application/json'
```

Output:

```json
{
  "name": "Michel",
  "lastname": "Scott",
  "nickname": null
}
```

Stop the node 2 and try the same request. This time lets ask node 3 itself to make sure it has same output.

```shell
curl --request GET \
  --url http://localhost:8002/storage/get/3 \
  --header 'Content-Type: application/json'
```


Done :)