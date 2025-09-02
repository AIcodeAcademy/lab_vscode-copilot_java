# Java programming with VSCode and GitHub Copilot

An archetype for building Java command-line interfaces using Spring Boot and Spring Shell. It provides a runnable scaffold, a sample weather command, and docs/tests to kick-start new CLI projects.

## Install & Run

- [User Manual](./docs/MANUAL.md)

Prerequisites:

- Java 21 (JDK) in PATH
- Maven 3.9+

Build and run tests:

```bash
# Run tests
mvn -q clean test

# Run the application:
mvn -q spring-boot:run

# Or package and run the JAR:
mvn -q -DskipTests package
java -jar target/archetype-java-cli-0.1.0-SNAPSHOT.jar
```
---

## About

- Author: [Alberto Basalo](https://albertobasalo.dev)
- Academy: [AIcodeAcademy](https://aicode.academy)
- Archetype:  [AIDDbot / ArchetypeJavaCLI](https://github.com/AIDDbot/ArchetypeJavaCLI)
- Laboratory: [AIcodeAcademy / lab_vscode-copilot_java](https://github.com/AIcodeAcademy/lab_vscode-copilot_java)