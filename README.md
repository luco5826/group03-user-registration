# group03-user-registration

To start postgres database:
```
docker pull postgres
docker run --name postgres -e POSTGRES_PASSWORD=<password> -d -p 54320:5432 -v <path_to_project>/group03-user-registration/database:/var/lib/postgresql/data postgres
```
