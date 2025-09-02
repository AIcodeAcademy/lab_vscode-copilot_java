# User Manual — Archetype Java CLI

This document explains how to install, run and use the `archetype-java-cli` tool built with Spring Boot and Spring Shell.

## Installation / Preparation

1. Ensure Java 21 is installed.
2. Build the project from the repository root:

```bash  
mvn -DskipITs=true clean package
```

3. Locate the produced artifact under `target/`: `archetype-java-cli-<version>.jar`.

## Run

Start the interactive shell:

```bash
# Compile and package
mvn -q -DskipTests package
# Run
java -jar target/archetype-java-cli-0.1.0-SNAPSHOT.jar
```

## Common commands

- version — prints build and runtime metadata
- weather — sample flow: resolves location (by IP if not provided) and fetches current weather


```bash
# Run the weather command on your ip based location
java -jar target/archetype-java-cli-0.1.0-SNAPSHOT.jar weather 
# Run the weather command with coordinates:
java -jar target/archetype-java-cli-0.1.0-SNAPSHOT.jar weather --lat 40.4168 --lon -3.7038
```

## Where to look next

- If you're a developer, make sure to read the documentation.
- [Briefing](/docs/archetype-java_cli.briefing.md)
- [Project Requirements Document (PRD)](/docs/PRD.md)
- [Domain Model](/docs/DOMAIN.md)
- [Systems Architecture](/docs/SYSTEMS.md)
- [Backlog](/docs/BACKLOG.md)
- [Project Structure](/docs/STRUCTURE.md)

---

> End of MANUAL for Archetype Java CLI, last updated on 2025-09-01.
