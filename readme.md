Setup: Place grammar in src/main/antlr4/SPL.g4


Compile: mvn clean compile
Run: mvn compile exec:java
Run with input file: mvn compile exec:java -Dexec.args="file.spl"