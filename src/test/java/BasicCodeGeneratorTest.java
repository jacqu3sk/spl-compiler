import org.junit.jupiter.api.Test;
import basic.BasicCodeGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class BasicCodeGeneratorTest {
    
    @Test
    void testBasicExample() {
        // Example from specification
        String intermediateCode = """
some_command
REM L0
IF V30=V31 THEN L1
some_other_command
GOTO L0
REM L1
yet_another_command
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode, 10);
        String result = generator.generateBasicCode();
        
        String expected = """
10 some_command
20 REM L0
30 IF V30=V31 THEN 60
40 some_other_command
50 GOTO 20
60 REM L1
70 yet_another_command
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testSimpleProgram() {
        String intermediateCode = """
t1 = 10
t2 = 5
t3 = t1+t2
v1 = t3
PRINT v1
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        String expected = """
10 t1 = 10
20 t2 = 5
30 t3 = t1+t2
40 v1 = t3
50 PRINT v1
60 END
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testIfThenProgram() {
        String intermediateCode = """
t1 = 10
v1 = t1
PRINT v1
t2 = v1
t3 = 10
IF t2=t3 THEN L1
GOTO L2
REM L1
t4 = 10
v5 = t4
STOP
t5 = v5
v2 = t5
REM L2
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        String expected = """
10 t1 = 10
20 v1 = t1
30 PRINT v1
40 t2 = v1
50 t3 = 10
60 IF t2=t3 THEN 80
70 GOTO 140
80 REM L1
90 t4 = 10
100 v5 = t4
110 END
120 t5 = v5
130 v2 = t5
140 REM L2
150 END
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testIfElseProgram() {
        String intermediateCode = """
t1 = 10
v1 = t1
PRINT v1
t2 = v1
t3 = 10
IF t2=t3 THEN L1
t4 = 20
v5 = t4
STOP
t5 = v5
v2 = t5
GOTO L2
REM L1
t6 = 10
v5 = t6
STOP
t7 = v5
v2 = t7
REM L2
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        String expected = """
10 t1 = 10
20 v1 = t1
30 PRINT v1
40 t2 = v1
50 t3 = 10
60 IF t2=t3 THEN 130
70 t4 = 20
80 v5 = t4
90 END
100 t5 = v5
110 v2 = t5
120 GOTO 190
130 REM L1
140 t6 = 10
150 v5 = t6
160 END
170 t7 = v5
180 v2 = t7
190 REM L2
200 END
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testWhileLoop() {
        String intermediateCode = """
t1 = 5
v1 = t1
PRINT v1
t2 = 10
v7 = t2
REM L1
t3 = v7
t4 = 5
IF t3>t4 THEN L2
GOTO L3
REM L2
t5 = v7
t6 = 1
t7 = t5-t6
v7 = t7
GOTO L1
REM L3
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        String expected = """
10 t1 = 5
20 v1 = t1
30 PRINT v1
40 t2 = 10
50 v7 = t2
60 REM L1
70 t3 = v7
80 t4 = 5
90 IF t3>t4 THEN 120
100 GOTO 170
110 REM L2
120 t5 = v7
130 t6 = 1
140 t7 = t5-t6
150 v7 = t7
160 GOTO 60
170 REM L3
180 END
""";
        
        // There's an issue here - REM L2 appears before its reference
        // Let me fix the expected output
        String expectedCorrected = """
10 t1 = 5
20 v1 = t1
30 PRINT v1
40 t2 = 10
50 v7 = t2
60 REM L1
70 t3 = v7
80 t4 = 5
90 IF t3>t4 THEN 110
100 GOTO 170
110 REM L2
120 t5 = v7
130 t6 = 1
140 t7 = t5-t6
150 v7 = t7
160 GOTO 60
170 REM L3
180 END
""";
        
        assertEquals(expectedCorrected, result);
    }
    
    @Test
    void testDoUntilLoop() {
        String intermediateCode = """
t1 = 5
v1 = t1
PRINT v1
t2 = 10
v7 = t2
REM L1
t3 = v7
t4 = 1
t5 = t3-t4
v7 = t5
t6 = v7
t7 = 0
IF t6=t7 THEN L2
GOTO L1
REM L2
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        String expected = """
10 t1 = 5
20 v1 = t1
30 PRINT v1
40 t2 = 10
50 v7 = t2
60 REM L1
70 t3 = v7
80 t4 = 1
90 t5 = t3-t4
100 v7 = t5
110 t6 = v7
120 t7 = 0
130 IF t6=t7 THEN 150
140 GOTO 60
150 REM L2
160 END
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testMultipleLabels() {
        String intermediateCode = """
REM L0
GOTO L1
REM L1
GOTO L2
REM L2
IF x=y THEN L0
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        String expected = """
10 REM L0
20 GOTO 30
30 REM L1
40 GOTO 50
50 REM L2
60 IF x=y THEN 10
70 END
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testCustomLineNumberStep() {
        String intermediateCode = """
REM L0
GOTO L1
REM L1
STOP
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode, 20);
        String result = generator.generateBasicCode();
        
        String expected = """
20 REM L0
40 GOTO 60
60 REM L1
80 END
""";
        
        assertEquals(expected, result);
    }
    
    @Test
    void testEmptyInput() {
        String intermediateCode = "";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        String result = generator.generateBasicCode();
        
        assertEquals("", result);
    }
    
    @Test
    void testLabelMapping() {
        String intermediateCode = """
REM L0
GOTO L1
REM L1
GOTO L0
""";
        
        BasicCodeGenerator generator = new BasicCodeGenerator(intermediateCode);
        generator.generateBasicCode();
        
        var mapping = generator.getLabelMapping();
        assertEquals(10, mapping.get("L0"));
        assertEquals(30, mapping.get("L1"));
    }
}
