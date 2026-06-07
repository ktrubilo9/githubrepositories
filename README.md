# GitHub Repositories

Spring Boot application that lists a GitHub user's public repositories that are not forks. For every repository, the response contains its name, owner login, and branches with their latest commit SHA.

## Requirements

- Java 25

## Run

```shell
./gradlew bootRun
```

The application starts on port `8080` by default.

## API

```http
GET /api/github/users/{username}/repositories
```

Example successful response:

```json
[
  {
    "repositoryName": "example",
    "ownerLogin": "username",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "abc123"
      }
    ]
  }
]
```

For a user that does not exist, the endpoint returns HTTP `404`:

```json
{
  "status": 404,
  "message": "Github user 'missing' was not found"
}
```

The application uses the GitHub REST API at `https://api.github.com`. Pagination is intentionally not supported.

## Tests

```shell
./gradlew test
```

The integration tests use WireMock to emulate the GitHub API.
