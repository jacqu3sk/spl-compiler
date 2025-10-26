import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import generated.SPLLexer;
import generated.SPLParser;
import symbolTable.*;
import translator.Translator;
import typeChecker.*;
import basic.BasicCodeGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Integration tests for the SPL compiler
 * Compiles all .spl files in test-inputs/ and verifies output against expected .txt files
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTest {
    
    private static final String TEST_DIR = "test-inputs";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    
    @BeforeAll
    public void checkBwbasic() {
        try {
            Process process = Runtime.getRuntime().exec("which bwbasic");
            process.waitFor();
            if (process.exitValue() != 0) {
                System.err.println(ANSI_YELLOW + 
                    "Warning: bwbasic not found. Install it with: sudo apt-get install bwbasic" + 
                    ANSI_RESET);
            }
        } catch (Exception e) {
            System.err.println(ANSI_YELLOW + 
                "Warning: Could not check for bwbasic: " + e.getMessage() + 
                ANSI_RESET);
        }
    }
    
    /**
     * Factory method to create dynamic tests for each .spl file
     */
    @TestFactory
    public Stream<DynamicTest> testAllSplFiles() {
        File testDir = new File(TEST_DIR);
        
        if (!testDir.exists() || !testDir.isDirectory()) {
            return Stream.empty();
        }
        
        File[] splFiles = testDir.listFiles((dir, name) -> name.endsWith(".spl"));
        
        if (splFiles == null || splFiles.length == 0) {
            return Stream.empty();
        }
        
        // Sort for consistent test order
        Arrays.sort(splFiles, Comparator.comparing(File::getName));
        
        return Arrays.stream(splFiles)
            .map(file -> DynamicTest.dynamicTest(
                "Testing: " + file.getName(),
                () -> testSplFile(file)
            ));
    }
    
    /**
     * Test a single SPL file
     */
    private void testSplFile(File splFile) throws Exception {
        String baseName = splFile.getName().replace(".spl", "");
        File expectedFile = new File(TEST_DIR, baseName + ".txt");
        
        // Step 1: Compile SPL to BASIC
        String basicCode = compileSplToBasic(splFile);
        assertNotNull(basicCode, "Compilation should produce BASIC code");
        assertFalse(basicCode.trim().isEmpty(), "BASIC code should not be empty");
        
        // Step 2: Run BASIC code
        String output = runBasicCode(basicCode);
        assertNotNull(output, "BASIC execution should produce output");
        
        // Step 3: Verify output
        verifyOutput(output, expectedFile, baseName);
    }
    
    /**
     * Compile an SPL file to BASIC code
     */
    private String compileSplToBasic(File splFile) throws Exception {
        CharStream input = CharStreams.fromFileName(splFile.getAbsolutePath());
        
        // Lexical Analysis
        SPLLexer lexer = new SPLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Syntax Analysis
        SPLParser parser = new SPLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                  Object offendingSymbol,
                                  int line, int charPositionInLine,
                                  String msg,
                                  RecognitionException e) {
                throw new RuntimeException("Syntax Error at line " + line + ":" + 
                                         charPositionInLine + " - " + msg);
            }
        });
        
        ParseTree tree = parser.spl_prog();
        
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new RuntimeException("Parsing failed with syntax errors");
        }
        
        // Semantic Analysis
        ParseTreeWalker walker = new ParseTreeWalker();
        SPLSemanticAnalyzer analyzer = new SPLSemanticAnalyzer();
        walker.walk(analyzer, tree);
        
        SymbolTable symbolTable = analyzer.getSymbolTable();
        
        if (symbolTable.hasErrors()) {
            StringBuilder errors = new StringBuilder("Semantic errors:\n");
            for (String error : symbolTable.getErrors()) {
                errors.append("  - ").append(error).append("\n");
            }
            throw new RuntimeException(errors.toString());
        }
        
        // Type Checking
        TypeErrorListener typeErrorListener = new TestTypeErrorListener();
        TypeChecker typeChecker = new TypeChecker(symbolTable, typeErrorListener);
        Boolean tc_ok = typeChecker.visit((SPLParser.Spl_progContext) tree);
        
        if (!tc_ok) {
            throw new RuntimeException("Type checking failed");
        }
        
        // Code Generation
        Translator translator = new Translator(symbolTable, tree);
        translator.generateIntermediateCode();
        
        BasicCodeGenerator basicGenerator = new BasicCodeGenerator(translator.getIntermediateCode());
        return basicGenerator.generateBasicCode();
    }
    
    /**
     * Run BASIC code using bwbasic
     */
    private String runBasicCode(String basicCode) throws Exception {
        // Create temporary file for BASIC code
        File tempFile = File.createTempFile("spl_test_", ".bas");
        tempFile.deleteOnExit();
        
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.print(basicCode);
        }
        
        // Run bwbasic
        ProcessBuilder pb = new ProcessBuilder("bwbasic", tempFile.getAbsolutePath());
        pb.redirectErrorStream(true);
        pb.redirectInput(ProcessBuilder.Redirect.PIPE); // Prevent interactive mode
        
        Process process = pb.start();
        
        // Close stdin immediately to prevent bwbasic from waiting for input
        process.getOutputStream().close();
        
        // Read output with timeout to prevent hanging
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // Wait for completion with timeout
        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("BASIC execution timed out");
        }
        
        return output.toString();
    }
    
    /**
     * Verify that actual output matches expected output
     */
    private void verifyOutput(String actualOutput, File expectedFile, String testName) throws Exception {
        if (!expectedFile.exists()) {
            System.err.println(ANSI_YELLOW + 
                "Warning: No expected output file for " + testName + 
                ANSI_RESET);
            return;
        }
        
        // Read expected lines
        List<String> expectedLines = Files.readAllLines(expectedFile.toPath())
            .stream()
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .toList();
        
        // Parse actual output, filtering out bwbasic messages
        List<String> actualLines = Arrays.stream(actualOutput.split("\n"))
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .filter(line -> !line.startsWith("Program interrupted"))
            .toList();
        
        // Normalize for numeric comparison
        List<String> normalizedExpected = expectedLines.stream()
            .map(this::normalizeNumber)
            .toList();
        
        List<String> normalizedActual = actualLines.stream()
            .map(this::normalizeNumber)
            .toList();
        
        // Check that all expected lines are present in actual output
        List<String> missingLines = new ArrayList<>();
        for (int i = 0; i < expectedLines.size(); i++) {
            String expectedLine = expectedLines.get(i);
            String normalizedExpectedLine = normalizedExpected.get(i);
            
            // Check both original and normalized
            if (!actualLines.contains(expectedLine) && 
                !normalizedActual.contains(normalizedExpectedLine)) {
                missingLines.add(expectedLine);
            }
        }
        
        if (!missingLines.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("\nMissing expected output lines:\n");
            for (String line : missingLines) {
                errorMsg.append("  - ").append(line).append("\n");
            }
            errorMsg.append("\nActual output:\n");
            for (String line : actualLines) {
                errorMsg.append("  ").append(line).append("\n");
            }
            
            fail(errorMsg.toString());
        }
    }
    
    /**
     * Normalize a number string for comparison
     * Handles scientific notation (e.g., 1.E+9 -> 1000000000)
     */
    private String normalizeNumber(String s) {
        try {
            double num = Double.parseDouble(s);
            // If it's a whole number, return as integer
            if (num == Math.floor(num) && !Double.isInfinite(num)) {
                return String.valueOf((long) num);
            }
            return String.valueOf(num);
        } catch (NumberFormatException e) {
            // Not a number, return as-is
            return s;
        }
    }
    
    /**
     * Type error listener for tests
     */
    private static class TestTypeErrorListener implements TypeErrorListener {
        private final List<String> errors = new ArrayList<>();
        
        @Override
        public void onTypeError(ParserRuleContext ctx, String message) {
            errors.add(message);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
}
