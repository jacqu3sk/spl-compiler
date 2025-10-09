# SPL Compiler

## Setup
- Place your grammar in `src/main/antlr4/SPL.g4` (already provided).
- Ensure Java 21 and Maven 3.9+ are installed.

## Build and Run
- Compile the project: `mvn clean compile`
- Execute the demo driver: `mvn exec:java`

## Testing
- Run the unit and integration tests: `mvn test`
- CI runs `mvn -B verify` on pushes and pull requests via GitHub Actions (`.github/workflows/ci.yml`).

## Project Layout
- `src/main/antlr4`: SPL grammar (`SPL.g4`)
- `src/main/java`: Compiler implementation
- `src/test/java`: JUnit tests for semantic analysis

## Generated Artifacts
- Maven places build outputs in `target/`, which is ignored by Git (`.gitignore`). If `target/` was previously committed, remove it once with `git rm -r target` so the ignore takes effect.
