# Server

## Setup

Install pre-commit

```
pip install pre-commit
```

```
pre-commit install -t commit-msg
```

## How to run?

### 1. As intellij project

- Start database container

```
docker-compose up -d db
```

- Run `ServerApplication.kt::main` file in intellij

### 2. As docker container

- Build and start the container

```
docker-compose up -d app
```

### 3. Tests in intellij

- Start database container

```
docker-compose up -d test-db
```

- Run tests in intellij

### 4. Dockerized tests

```
docker-compose up --force-recreate test
```
