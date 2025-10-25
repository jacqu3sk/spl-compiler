# SPL Test Inputs

This directory contains test input files for the SPL compiler.

## File Organization

Each test case consists of two files:
- `test_name.spl` - The SPL source code to be compiled
- `test_name.txt` - The expected output when the program is executed

## Test Categories

### Base Cases (Core Functionality)
- `test_minimal.spl` - Minimal valid program
- `test_simple_vars.spl` - Variable declarations
- `test_basic_assign.spl` - Basic assignments
- `test_arithmetic.spl` - Arithmetic operations
- `test_print.spl` - Print statements
- `test_simple_proc.spl` - Simple procedure
- `test_simple_func.spl` - Simple function
- `test_if.spl` - If statement
- `test_if_else.spl` - If-else statement
- `test_while.spl` - While loop
- `test_do_until.spl` - Do-until loop

### Edge Cases (Boundary Conditions)
- `test_max_locals.spl` - Maximum local variables (3)
- `test_max_params.spl` - Maximum function parameters (3)
- `test_nested_expr.spl` - Nested expressions
- `test_comparisons.spl` - Comparison operators
- `test_logical.spl` - Logical operators (and, or, not)
- `test_negation.spl` - Unary negation
- `test_complex_control.spl` - Complex control flow
- `test_multiple_defs.spl` - Multiple procedures and functions
- `test_empty_params.spl` - Empty parameter lists
- `test_long_names.spl` - Long identifier names
- `test_sequential_algo.spl` - Sequential algorithms
- `test_func_in_assign.spl` - Function call in assignment
- `test_strings.spl` - String literals
- `test_nested_loops.spl` - Nested loops
- `test_mixed_loops.spl` - Mixed loop types

### Boundary Tests (Testing Limits)
- `test_zero_params.spl` - Zero parameters
- `test_one_param.spl` - One parameter
- `test_two_params.spl` - Two parameters
- `test_one_local.spl` - One local variable
- `test_two_locals.spl` - Two local variables
- `test_large_numbers.spl` - Large numeric literals
- `test_single_instr.spl` - Single instruction
- `test_all_instructions.spl` - All instruction types

## Usage

To run a test:
```bash
# Compile the SPL program
mvn exec:java -Dexec.args="test-inputs/test_name.spl"

# Compare output with expected
diff output.txt test-inputs/test_name.txt
```

## Notes

- Empty `.txt` files indicate programs that should compile successfully but produce no output
- Tests cover all major grammar productions and operators
- Parameter and local variable limits are enforced (0-3)
