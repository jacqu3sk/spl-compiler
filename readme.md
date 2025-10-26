# SPL Compiler

## Setup
- Place your grammar in `src/main/antlr4/SPL.g4` (already provided).
- Ensure Java 21 and Maven 3.9+ are installed.

## Build and Run
- Compile the project: `mvn clean compile`
- Execute the demo driver: `mvn exec:java`

## Testing
- Run all tests: `mvn test`
- Run only integration tests: `mvn test -Dtest=IntegrationTest`
- See [TESTING.md](TESTING.md) for detailed testing documentation
- CI runs `mvn -B verify` on pushes and pull requests via GitHub Actions (`.github/workflows/ci.yml`)

### Integration Tests
The integration test suite automatically:
1. Compiles all `.spl` files in `test-inputs/`
2. Generates BASIC code for each
3. Executes the BASIC code with `bwbasic`
4. Verifies output matches expected results in corresponding `.txt` files

**Note**: Install `bwbasic` to run integration tests: `sudo apt-get install bwbasic`

## Project Layout
- `src/main/antlr4`: SPL grammar (`SPL.g4`)
- `src/main/java`: Compiler implementation
- `src/test/java`: JUnit tests for semantic analysis

## Generated Artifacts
- Maven places build outputs in `target/`, which is ignored by Git (`.gitignore`). If `target/` was previously committed, remove it once with `git rm -r target` so the ignore takes effect.
