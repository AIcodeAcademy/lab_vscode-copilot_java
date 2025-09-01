# Java programming with VSCode and GitHub Copilot

An archetype for building Java command-line interfaces using Spring Boot and Spring Shell. It provides a runnable scaffold, a sample weather command, and docs/tests to kick-start new CLI projects.

## Install & Run

Prerequisites:

- Java 21 (JDK) in PATH
- Maven 3.9+

Build and run tests:

```bash
# Run all tests (unit tests only)
mvn -q clean test

# Run only unit tests (fast, no Spring context)
mvn test -Dtest=*UnitTest

# Use unit tests for regular development and CI/CD pipelines (fast feedback)
# Always run mvn clean when encountering failures to clear stale compiled classes

```

Run the application (non-web CLI):

```bash
mvn -q spring-boot:run
```

Or package and run the JAR:

```bash
mvn -q -DskipTests package
java -jar target/archetype-java-cli-0.1.0-SNAPSHOT.jar
```

## Logging

- Structured JSON logs to STDOUT using Logback + `logstash-logback-encoder`.
- Custom fields `app` and `version` are included in each entry.
- Root log level is resolved in this order: `logging.level.root` (Spring property) → `LOGGING_LEVEL_ROOT` (env var) → `INFO`.

Examples:

```bash
# Run with INFO root level (default)
mvn -q spring-boot:run

# Override to ERROR via Spring property
mvn -q spring-boot:run -Dspring-boot.run.arguments="--logging.level.root=ERROR"

# Or via environment variable
LOGGING_LEVEL_ROOT=ERROR mvn -q spring-boot:run
```

Environment hints:

- To set the app name or version when running locally:

```bash
SPRING_APPLICATION_NAME=archetype-java-cli APP_VERSION=dev mvn -q spring-boot:run
```

- Configuration via environment variables (F1.3):
  - Timeouts
    - `CLI_NETWORK_CONNECT_TIMEOUT_MS` → `cli.network.connect-timeout-ms` (default 2000)
    - `CLI_NETWORK_READ_TIMEOUT_MS` → `cli.network.read-timeout-ms` (default 2000)
  - External endpoints
    - `CLI_ENDPOINTS_IP_GEO_BASE_URL` → `cli.endpoints.ip-geo-base-url` (default http://ip-api.com/json)
    - `CLI_ENDPOINTS_OPEN_METEO_BASE_URL` → `cli.endpoints.open-meteo-base-url` (default https://api.open-meteo.com/v1/forecast)

Example run with overrides:

```bash
CLI_NETWORK_CONNECT_TIMEOUT_MS=1500 CLI_NETWORK_READ_TIMEOUT_MS=2500 \
CLI_ENDPOINTS_IP_GEO_BASE_URL=http://ip-api.com/json \
CLI_ENDPOINTS_OPEN_METEO_BASE_URL=https://api.open-meteo.com/v1/forecast \
mvn -q spring-boot:run
```


## About

- Author: [Alberto Basalo](https://albertobasalo.dev)
- Academy: [AIcodeAcademy](https://aicode.academy)
- Archetype:  [AIDDbot / ArchetypeJavaCLI](https://github.com/AIDDbot/ArchetypeJavaCLI)
- Laboratory: [AIcodeAcademy / lab_vscode-copilot_java](https://github.com/AIcodeAcademy/lab_vscode-copilot_java)