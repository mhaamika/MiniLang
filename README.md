# MiniLang Interpreter

A custom programming language interpreter built from scratch in Java, featuring lexical analysis, recursive descent parsing, expression evaluation, variables, boolean logic, conditional execution, and mathematical functions.

## Overview

MiniLang is a lightweight interpreted language designed to explore the fundamentals of programming language implementation. It reads source code from a text file, tokenizes and parses statements, evaluates expressions, and executes instructions with runtime error handling.

The interpreter uses a hand-written lexer and recursive descent parser, without parser generators.

## Features

### Language Processing
- **Lexical Analysis:** Recognizes identifiers, keywords, operators, literals, and other language tokens.
- **Recursive Descent Parsing:** Parses expressions and statements using a structured grammar.
- **Finite-State Token Recognition:** Uses state-based logic to recognize valid tokens and detect invalid characters.
- **Trace Mode:** Provides insight into tokenization, parsing, variable updates, and expression evaluation.

### Variables and Statements
- Variable declaration using `let`
- Variable reassignment and reuse in expressions
- Variable lookup and undefined-variable detection
- Output statements using `display`
- File-based source code execution
- Single-line comments using `//`

### Expressions and Operators
- Arithmetic: `+`, `-`, `*`, `/`, `%`, `^`
- Unary operators: `+` and `-`
- Parenthesized expressions
- Operator precedence and associativity
- Comparison operators: `<`, `<=`, `>`, `>=`, `==`, `!=`
- Logical operators: `&&`, `||`, `!`
- Boolean expression evaluation

### Control Flow and Functions
- Conditional execution using `if`, `then`, and `else`
- Built-in mathematical functions: `abs()`, `sqrt()`, `min()`, `max()`, and `pow()`
- Custom factorial function: `factorial()`
- String literals and string output

### Error Handling
- Syntax errors and malformed expressions
- Undefined variables and invalid identifiers
- Duplicate variable declarations
- Missing or mismatched parentheses
- Invalid function usage or arguments
- Division-by-zero errors
- Invalid character detection

## How It Works

```text
        Source File
             |
             v
           Lexer
             |
             v
       Token Sequence
             |
             v
 Recursive Descent Parser
             |
             v
  Expression Evaluation
             |
             v
     Runtime Results
             |
             v
      Console Output
```

1. **Lexing:** The lexer scans the source file and converts its contents into tokens.
2. **Parsing:** The parser processes tokens according to the language's grammar and validates expressions and statements.
3. **Evaluation:** Expressions are evaluated, variables are managed, and functions and boolean operations are processed.
4. **Execution:** Conditional statements determine which instructions run.
5. **Diagnostics:** Errors identify invalid input, while trace mode helps inspect the interpreter's internal processing.

## Example

**MiniLang source code**

```text
// Variables and arithmetic
let x = 10
let y = 5

display(x + y)
display(-x)
display(pow(2, 3))

// Boolean logic and conditional execution
if x > y then
    display("x is greater")
else
    display("y is greater")
```

**Expected output**

```text
15.0
-10.0
8.0
x is greater
```

## Tech Stack

- **Language:** Java
- **Parsing:** Hand-written recursive descent parser
- **Lexical Analysis:** State-based token recognition
- **Core Concepts:** Programming language design, expression evaluation, operator precedence, variables, boolean logic, control flow, and error handling
- **Tools:** IntelliJ IDEA, Git, GitHub

## Running the Interpreter

Clone the repository:

```bash
git clone YOUR_REPOSITORY_URL
cd MiniLang-Interpreter
```

Compile the Java source files from the directory containing them:

```bash
javac *.java
```

Run a source file:

```bash
java Main program.txt
```

Enable trace mode to inspect the interpreter's internal processing:

```bash
java Main --trace program.txt
```

## Key Learning Outcomes

- Designed a lexer and parser from scratch.
- Applied recursive descent parsing to interpret a custom language.
- Implemented variable storage, reassignment, and expression evaluation.
- Handled operator precedence, unary operations, and boolean logic.
- Implemented conditional execution and mathematical functions.
- Added error detection and trace output for debugging and transparency.

---


