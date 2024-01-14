## Docker Compose


Check the configuration of docker-compose:
```bash
docker-compose -f docker/docker-compose.dev.yml config
```

Run the postgres service:

```bash
docker-compose --project-name english-course-app -f docker/docker-compose.dev.yml up -d postgres
```

Stop and remove docker services:

```bash
docker-compose -f docker/docker-compose.dev.yml down
```

You can configure the environment variables in the <em>./docker/.env</em> file.
Possible variables to configure:

| Environment Variable  | Description                                                 |
|-----------------------|-------------------------------------------------------------|
| POSTGRES_PASSWORD_DEV | Password for postgres database during development stage     |
| POSTGRES_USER_DEV     | User for postgres database during development stage         |
| POSTGRES_DB_DEV       | Default database name for postgres during development stage |