package basic;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts intermediate code to executable BASIC code
 * - Adds consecutive line numbers (in steps of 10)
 * - Replaces GOTO Lx with GOTO lineNumber
 * - Replaces THEN Lx with THEN lineNumber
 */
public class BasicCodeGenerator {
    private String intermediateCode;
    private StringBuilder basicCode;
    private Map<String, Integer> labelToLineNumber;
    private int lineNumberStep;
    private int currentLineNumber;
    
    /**
     * Constructor with default line number step of 10
     * @param intermediateCode the intermediate code to convert
     */
    public BasicCodeGenerator(String intermediateCode) {
        this(intermediateCode, 10);
    }
    
    /**
     * Constructor with custom line number step
     * @param intermediateCode the intermediate code to convert
     * @param lineNumberStep the step size for line numbers (e.g., 10, 20, 1, etc.)
     */
    public BasicCodeGenerator(String intermediateCode, int lineNumberStep) {
        this.intermediateCode = intermediateCode;
        this.basicCode = new StringBuilder();
        this.labelToLineNumber = new HashMap<>();
        this.lineNumberStep = lineNumberStep;
        this.currentLineNumber = lineNumberStep;
    }
    
    /**
     * Generate BASIC code from intermediate code
     * @return executable BASIC code with line numbers
     */
    public String generateBasicCode() {
        if (intermediateCode == null || intermediateCode.trim().isEmpty()) {
            return "";
        }
        
        // First pass: assign line numbers to each line and map labels to line numbers
        String[] lines = intermediateCode.split("\n");
        String[] numberedLines = new String[lines.length];
        
        currentLineNumber = lineNumberStep;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.isEmpty()) {
                continue; // Skip empty lines
            }
            
            // Store the line with its number
            numberedLines[i] = currentLineNumber + " " + line;
            
            // If this line is a label (REM Lx), map the label to the line number
            if (line.startsWith("REM L")) {
                String label = extractLabel(line);
                if (label != null) {
                    labelToLineNumber.put(label, currentLineNumber);
                }
            }
            
            currentLineNumber += lineNumberStep;
        }
        
        // Second pass: replace GOTO Lx and THEN Lx with actual line numbers
        for (int i = 0; i < numberedLines.length; i++) {
            if (numberedLines[i] == null) {
                continue; // Skip empty lines
            }
            
            String processedLine = replaceLabelsWithLineNumbers(numberedLines[i]);
            basicCode.append(processedLine).append("\n");
        }
        
        return basicCode.toString();
    }
    
    /**
     * Extract label from a REM statement
     * @param line the line containing REM Lx
     * @return the label (e.g., "L0", "L1") or null if not found
     */
    private String extractLabel(String line) {
        // Line format: "REM Lx" or "lineNumber REM Lx"
        String[] parts = line.split("\\s+");
        
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("REM") && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        
        return null;
    }
    
    /**
     * Replace GOTO Lx and THEN Lx with GOTO lineNumber and THEN lineNumber
     * @param line the line to process
     * @return the line with labels replaced by line numbers
     */
    private String replaceLabelsWithLineNumbers(String line) {
        String result = line;
        
        // Replace GOTO Lx with GOTO lineNumber
        for (Map.Entry<String, Integer> entry : labelToLineNumber.entrySet()) {
            String label = entry.getKey();
            Integer lineNumber = entry.getValue();
            
            // Replace "GOTO Lx" with "GOTO lineNumber"
            result = result.replaceAll("GOTO\\s+" + label + "(?!\\d)", "GOTO " + lineNumber);
            
            // Replace "THEN Lx" with "THEN lineNumber"
            result = result.replaceAll("THEN\\s+" + label + "(?!\\d)", "THEN " + lineNumber);
        }
        
        return result;
    }
    
    /**
     * Get the generated BASIC code
     * @return executable BASIC code
     */
    public String getBasicCode() {
        return basicCode.toString();
    }
    
    /**
     * Print the generated BASIC code to console
     */
    public void printBasicCode() {
        System.out.println("\nGenerated BASIC Code:");
        System.out.println("=====================");
        System.out.println(basicCode.toString());
    }
    
    /**
     * Get the label to line number mapping (for debugging)
     * @return map of labels to line numbers
     */
    public Map<String, Integer> getLabelMapping() {
        return new HashMap<>(labelToLineNumber);
    }
}
