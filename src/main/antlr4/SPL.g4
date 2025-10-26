grammar SPL;

@header {
package generated;
}

// Parser Rules
spl_prog
    : 'glob' '{' variables '}' 
      'proc' '{' procdefs '}' 
      'func' '{' funcdefs '}' 
      'main' '{' mainprog '}'
    ;

variables
    : // empty
    | var variables
    ;

var
    : NAME
    ;

procdefs
    : // empty
    | pdef procdefs
    ;

pdef
    : NAME '(' param ')' '{' body '}'
    ;

funcdefs
    : // empty
    | fdef funcdefs
    ;

fdef
    : NAME '(' param ')' '{' body ';' 'return' atom '}'
    ;

body
    : 'local' '{' maxthree '}' algo
    ;

param
    : maxthree
    ;

maxthree
    : // empty
    | var
    | var var
    | var var var
    ;

mainprog
    : 'var' '{' variables '}' algo
    ;

atom
    : var
    | NUMBER
    ;

algo
    : instr
    | instr ';' algo
    ;

instr
    : 'halt'
    | 'print' output
    | NAME '(' input ')'
    | assign
    | loop
    | branch
    ;

assign
    : var '=' NAME '(' input ')'
    | var '=' term
    ;

loop
    : 'while' term '{' algo '}'
    | 'do' '{' algo '}' 'until' term
    ;

branch
    : 'if' term '{' algo '}'
    | 'if' term '{' algo '}' 'else' '{' algo '}'
    ;

output
    : atom
    | STRING
    ;

input
    : // empty
    | atom
    | atom atom
    | atom atom atom
    ;

term
    : atom
    | '(' unop term ')'
    | '(' term binop term ')'
    ;

unop
    : 'neg'
    | 'not'
    ;

binop
    : 'eq'
    | '>'
    | 'or'
    | 'and'
    | 'plus'
    | 'minus'
    | 'mult'
    | 'div'
    ;

// Lexer Rules
NAME
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

NUMBER
    : [0-9]+
    ;

STRING
    : '"' (~["\r\n])* '"'
    ;

// Whitespace and Comments
WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;
