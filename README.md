# group03-user-registration

To start postgres database:
```
docker pull postgres
docker run --name postgres -e POSTGRES_PASSWORD=<password> -d -p <port>:5432 -v <path_to_project>/database:/var/lib/postgresql/data postgres
```

In `application.properties` the property `spring.datasource.url` should look like:
```
jdbc:postgresql://localhost:<port>/postgres
```
while `spring.datasource.password` should be `<password>`.
