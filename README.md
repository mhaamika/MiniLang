# MiniLang Interpreter

A small interpreter written in Java using a hand-written lexer and recursive-descent parser. It is intentionally kept in a few readable files so the implementation is easier to learn and explain.

## Requirements

- Java 11 or newer
- A terminal, or an IDE such as IntelliJ IDEA

## Compile and run

From the `MiniLangBeginner` folder:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main examples/sample.txt
```

Trace mode:

```bash
java -cp out Main --trace examples/sample.txt
```

The source file is supplied as a command-line argument. You do not need to hardcode its path in `Main.java`.

## MiniLang features

- Read source code from a text file
- Integer and decimal numbers
- Strings in single or double quotes
- Variables with `let` and assignment to existing variables
- `+`, `-`, `*`, `/`, `%`, and `^`
- Unary `+` and `-`
- Parentheses and operator precedence
- `abs(x)`, `sqrt(x)`, `min(x,y)`, `max(x,y)`, and `pow(x,y)`
- Custom function `factorial(x)`
- Comparisons: `<`, `<=`, `>`, `>=`, `==`, `!=`
- Boolean operators: `&&`, `||`, `!`
- `if condition then statement else statement` (the word `then` is optional)
- Single-line comments beginning with `//`
- Error messages for invalid characters, missing expressions, unknown variables, redeclarations, bad function calls, and division by zero
- Trace output for tokens, parser matches, variable changes, and evaluation steps

## Examples

```text
let x = 5;
let y = x + 3;
display(y);

if y > 5 then display("large") else display("small");
```

Output:

```text
Result: 8.0
Result: large
```

## How the parser works

The parser uses separate methods for different precedence levels:

1. `orExpression()` handles `||`.
2. `andExpression()` handles `&&`.
3. `equalityExpression()` handles `==` and `!=`.
4. `comparisonExpression()` handles `<`, `<=`, `>`, and `>=`.
5. `additiveExpression()` handles `+` and `-`.
6. `multiplicativeExpression()` handles `*`, `/`, and `%`.
7. `powerExpression()` handles `^`.
8. `unaryExpression()` handles unary `+`, unary `-`, and `!`.
9. `primary()` handles values, variables, function calls, and parentheses.

This is recursive-descent parsing. No parser generator is used.

## Tests

Run the basic test suite:

```bash
./tests/run_tests.sh
```

## DFA diagrams

### Numbers

```text
START --digit--> INTEGER --digit--> INTEGER
                         --dot--> DECIMAL --digit--> DECIMAL
INTEGER or DECIMAL --other character--> ACCEPT
```

### Identifiers

```text
START --letter or _--> IDENTIFIER
IDENTIFIER --letter, digit, or _--> IDENTIFIER
IDENTIFIER --other character--> ACCEPT
```

### Multi-character operators

```text
'<' --> accept '<' or, if next char is '=', accept '<='
'>' --> accept '>' or, if next char is '=', accept '>='
'=' --> accept '=' or, if next char is '=', accept '=='
'!' --> accept '!' or, if next char is '=', accept '!='
'&' --> require '&' to accept '&&'
'|' --> require '|' to accept '||'
```

## Important implementation notes

- The implementation evaluates expressions as it parses them. This keeps the code smaller and easier to follow, though a larger language might use a separate abstract syntax tree.
- `factorial(x)` accepts whole numbers from 0 through 20.
- Arithmetic results are displayed as Java numeric values, so whole-number results may appear with `.0`.
- This is a student project starter. Read and test the code with your teammate, and adjust any design decisions to match your instructor's exact expectations.
