import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<Token> tokens;
    private int pos;
    private Map<String, Object> variables;
    private boolean trace;

    public Parser(List<Token> tokens, Map<String, Object> variables, boolean trace) {
        this.tokens = tokens;
        this.variables = variables;
        this.trace = trace;
        this.pos = 0;
    }

    private Token currentToken() {
        return tokens.get(pos);
    }

    private Token lookAhead() {
        if (pos + 1 >= tokens.size())
        {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(pos + 1);
    }

    private void eat(String expectedType) {
        if (currentToken().type.equals(expectedType))
        {
            if (trace)
            {
                System.out.println("[PARSE] Matched " + currentToken());
            }
            pos++;
        } else {
            throw new RuntimeException("Expected " + expectedType + " but found " + currentToken().value);
        }
    }

    private boolean word(String value) {
        return currentToken().type.equals("IDENTIFIER") && currentToken().value.equals(value);
    }

    public void parseProgram() {
        while (!currentToken().type.equals("EOF")) {
            if (currentToken().type.equals("SEMICOLON")) {
                eat("SEMICOLON");
            } else {
                int statementStart = pos;
                statement(false);
                if (trace) {
                    String representation = "";
                    for (int i = statementStart; i < pos; i++) {
                        representation += tokens.get(i).toString() + " ";
                    }
                    System.out.println("[PARSE] Statement representation: " + representation.trim());
                }
                if (currentToken().type.equals("SEMICOLON")) {
                    eat("SEMICOLON");
                } else if (!currentToken().type.equals("EOF")) {
                    throw new RuntimeException("Expected ';' between statements.");
                }
            }
        }
    }

    private void statement(boolean insideConditional) {
        if (word("let")) {
            if (trace)
                System.out.println("[PARSE] Variable declaration");
            declaration();
        } else if (word("display")) {
            if (trace)
                System.out.println("[PARSE] Display statement");
            displayStatement();
        } else if (word("if")) {
            if (trace)
                System.out.println("[PARSE] If/else statement");
            conditional();
        } else if (currentToken().type.equals("IDENTIFIER") && lookAhead().type.equals("ASSIGN")) {
            if (trace) System.out.println("[PARSE] Variable assignment");
            assignment();
        } else {
            if (trace) System.out.println("[PARSE] Expression statement");
            Object value = expression();
            System.out.println("Result: " + formatValue(value));
        }
    }

    private void declaration() {
        eat("IDENTIFIER"); // let
        if (!currentToken().type.equals("IDENTIFIER")) {
            throw new RuntimeException("Expected a variable name after 'let'.");
        }
        String name = currentToken().value;
        eat("IDENTIFIER");

        if (variables.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' is already declared.");
        }

        eat("ASSIGN");
        Object value = expression();
        variables.put(name, value);
        if (trace) {
            System.out.println("[EVAL] Declared " + name + " = " + value);
        }
    }

    private void assignment() {
        String name = currentToken().value;
        eat("IDENTIFIER");
        eat("ASSIGN");

        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' has not been declared.");
        }

        Object value = expression();
        variables.put(name, value);
        if (trace) {
            System.out.println("[EVAL] Updated " + name + " = " + value);
        }
    }

    private void displayStatement() {
        eat("IDENTIFIER"); // display
        eat("LPAREN");

        Object value;
        if (currentToken().type.equals("IDENTIFIER") && lookAhead().type.equals("ASSIGN")) {
            String name = currentToken().value;
            eat("IDENTIFIER");
            eat("ASSIGN");
            if (!variables.containsKey(name)) {
                throw new RuntimeException("Variable '" + name + "' has not been declared.");
            }
            value = expression();
            variables.put(name, value);
            if (trace) {
                System.out.println("[EVAL] Updated " + name + " = " + value);
            }
        } else {
            value = expression();
        }

        eat("RPAREN");
        System.out.println("Result: " + formatValue(value));
    }

    private void conditional() {
        eat("IDENTIFIER"); // if
        if (trace) {
            System.out.println("[PARSE] Conditional expression");
        }
        Object conditionValue = expression();
        if (!(conditionValue instanceof Boolean)) {
            throw new RuntimeException("The condition in 'if' must be true or false.");
        }

        if (word("then")) {
            eat("IDENTIFIER");
        }

        boolean condition = ((Boolean) conditionValue).booleanValue();
        if (trace) {
            System.out.println("[EVAL] if condition = " + condition);
        }

        if (condition) {
            conditionalBranch();
            if (word("else")) {
                eat("IDENTIFIER");
                skipBranch(false);
            }
        } else {
            skipBranch(true);
            if (!word("else")) {
                throw new RuntimeException("Expected 'else' in conditional.");
            }
            eat("IDENTIFIER");
            conditionalBranch();
        }
    }

    private void conditionalBranch() {
        if (word("display")) {
            displayStatement();
        } else if (word("if")) {
            conditional();
        } else {
            Object value = expression();
            System.out.println("Result: " + formatValue(value));
        }
    }

    // Read but do not execute the branch that was not selected.
    // The simple version supports display(...) and ordinary expressions.
    private void skipBranch(boolean stopAtElse) {
        if (word("display")) {
            eat("IDENTIFIER");
            eat("LPAREN");
            skipExpressionUntilRightParen();
            eat("RPAREN");
        } else if (word("if")) {
            // Nested conditionals are supported when their branch is selected.
            // For an unselected nested branch, skip tokens without evaluating them.
            skipNestedConditional();
        } else {
            int nestedParentheses = 0;
            while (!currentToken().type.equals("EOF")) {
                if (nestedParentheses == 0 && currentToken().type.equals("SEMICOLON")) {
                    return;
                }
                if (stopAtElse && nestedParentheses == 0 && word("else")) {
                    return;
                }
                if (currentToken().type.equals("LPAREN")) nestedParentheses++;
                if (currentToken().type.equals("RPAREN") && nestedParentheses > 0) nestedParentheses--;
                pos++;
            }
        }
    }

    private void skipNestedConditional() {
        int nestedIfs = 0;
        while (!currentToken().type.equals("EOF") && !currentToken().type.equals("SEMICOLON")) {
            if (word("if")) nestedIfs++;
            if (word("else")) {
                if (nestedIfs == 0) return;
                nestedIfs--;
            }
            pos++;
        }
    }

    private void skipExpressionUntilRightParen() {
        int nestedParentheses = 0;
        while (!currentToken().type.equals("EOF")) {
            if (currentToken().type.equals("RPAREN")) {
                if (nestedParentheses == 0) {
                    return;
                }
                nestedParentheses--;
            } else if (currentToken().type.equals("LPAREN")) {
                nestedParentheses++;
            }
            pos++;
        }
        throw new RuntimeException("Missing ')' in display statement.");
    }

    private Object expression() {
        return orExpression();
    }

    private Object orExpression() {
        Object value = andExpression();
        while (currentToken().type.equals("OR")) {
            eat("OR");
            Object right = andExpression();
            value = Boolean.valueOf(asBoolean(value) || asBoolean(right));
            traceResult("||", value);
        }
        return value;
    }

    private Object andExpression() {
        Object value = equalityExpression();
        while (currentToken().type.equals("AND")) {
            eat("AND");
            Object right = equalityExpression();
            value = Boolean.valueOf(asBoolean(value) && asBoolean(right));
            traceResult("&&", value);
        }
        return value;
    }

    private Object equalityExpression() {
        Object value = comparisonExpression();
        while (currentToken().type.equals("EQUAL_EQUAL") || currentToken().type.equals("NOT_EQUAL")) {
            String operator = currentToken().value;
            String type = currentToken().type;
            eat(type);
            Object right = comparisonExpression();
            boolean same = valuesEqual(value, right);
            if (type.equals("NOT_EQUAL")) {
                same = !same;
            }
            value = Boolean.valueOf(same);
            traceResult(operator, value);
        }
        return value;
    }

    private Object comparisonExpression() {
        Object value = additiveExpression();
        while (currentToken().type.equals("LT") || currentToken().type.equals("LTE")
                || currentToken().type.equals("GT") || currentToken().type.equals("GTE")) {
            String type = currentToken().type;
            String operator = currentToken().value;
            eat(type);
            Object right = additiveExpression();
            double leftNumber = asNumber(value);
            double rightNumber = asNumber(right);
            boolean answer;

            if (type.equals("LT")) answer = leftNumber < rightNumber;
            else if (type.equals("LTE")) answer = leftNumber <= rightNumber;
            else if (type.equals("GT")) answer = leftNumber > rightNumber;
            else answer = leftNumber >= rightNumber;

            value = Boolean.valueOf(answer);
            traceResult(operator, value);
        }
        return value;
    }

    private Object additiveExpression() {
        Object value = multiplicativeExpression();
        while (currentToken().type.equals("PLUS") || currentToken().type.equals("MINUS")) {
            String type = currentToken().type;
            String operator = currentToken().value;
            eat(type);
            Object right = multiplicativeExpression();

            if (type.equals("PLUS") && (value instanceof String || right instanceof String)) {
                value = String.valueOf(value) + String.valueOf(right);
            } else if (type.equals("PLUS")) {
                value = Double.valueOf(asNumber(value) + asNumber(right));
            } else {
                value = Double.valueOf(asNumber(value) - asNumber(right));
            }
            traceResult(operator, value);
        }
        return value;
    }

    private Object multiplicativeExpression() {
        Object value = unaryExpression();
        while (currentToken().type.equals("MULTIPLY") || currentToken().type.equals("DIVIDE")
                || currentToken().type.equals("MODULO")) {
            String type = currentToken().type;
            String operator = currentToken().value;
            eat(type);
            Object right = unaryExpression();
            double leftNumber = asNumber(value);
            double rightNumber = asNumber(right);

            if ((type.equals("DIVIDE") || type.equals("MODULO")) && rightNumber == 0) {
                throw new RuntimeException("Division or modulus by zero is not allowed.");
            }
            if (type.equals("MULTIPLY")) value = Double.valueOf(leftNumber * rightNumber);
            else if (type.equals("DIVIDE")) value = Double.valueOf(leftNumber / rightNumber);
            else value = Double.valueOf(leftNumber % rightNumber);
            traceResult(operator, value);
        }
        return value;
    }

    // Power is right-associative, so 2 ^ 3 ^ 2 means 2 ^ (3 ^ 2).
    private Object powerExpression() {
        Object value = primary();
        if (currentToken().type.equals("POWER")) {
            eat("POWER");
            Object right = unaryExpression();
            value = Double.valueOf(Math.pow(asNumber(value), asNumber(right)));
            traceResult("^", value);
        }
        return value;
    }

    private Object unaryExpression() {
        if (currentToken().type.equals("MINUS")) {
            eat("MINUS");
            Object value = Double.valueOf(-asNumber(unaryExpression()));
            traceResult("unary -", value);
            return value;
        }
        if (currentToken().type.equals("PLUS")) {
            eat("PLUS");
            return Double.valueOf(asNumber(unaryExpression()));
        }
        if (currentToken().type.equals("NOT")) {
            eat("NOT");
            Object value = Boolean.valueOf(!asBoolean(unaryExpression()));
            traceResult("!", value);
            return value;
        }
        return powerExpression();
    }

    private Object primary() {
        Token token = currentToken();

        if (token.type.equals("NUMBER")) {
            eat("NUMBER");
            return Double.valueOf(Double.parseDouble(token.value));
        }
        if (token.type.equals("STRING")) {
            eat("STRING");
            return token.value;
        }
        if (token.type.equals("LPAREN")) {
            eat("LPAREN");
            Object value = expression();
            eat("RPAREN");
            return value;
        }
        if (token.type.equals("IDENTIFIER")) {
            String name = token.value;
            eat("IDENTIFIER");

            if (name.equals("true")) return Boolean.TRUE;
            if (name.equals("false")) return Boolean.FALSE;

            if (currentToken().type.equals("LPAREN")) {
                return functionCall(name);
            }

            if (!variables.containsKey(name)) {
                throw new RuntimeException("Variable '" + name + "' has not been declared.");
            }
            return variables.get(name);
        }

        throw new RuntimeException("Expected an expression but found '" + token.value + "'.");
    }

    private Object functionCall(String name) {
        eat("LPAREN");
        Object first = expression();
        Object second = null;
        int argumentCount = 1;

        if (currentToken().type.equals("COMMA")) {
            eat("COMMA");
            second = expression();
            argumentCount = 2;
        }
        eat("RPAREN");

        double x = asNumber(first);
        double y = 0;
        if (argumentCount == 2) y = asNumber(second);

        if (name.equals("abs") && argumentCount == 1) return Double.valueOf(Math.abs(x));
        if (name.equals("sqrt") && argumentCount == 1) {
            if (x < 0) throw new RuntimeException("sqrt() cannot use a negative number.");
            return Double.valueOf(Math.sqrt(x));
        }
        if (name.equals("min") && argumentCount == 2) return Double.valueOf(Math.min(x, y));
        if (name.equals("max") && argumentCount == 2) return Double.valueOf(Math.max(x, y));
        if (name.equals("pow") && argumentCount == 2) return Double.valueOf(Math.pow(x, y));
        if (name.equals("factorial") && argumentCount == 1) {
            if (x < 0 || x != Math.floor(x) || x > 20) {
                throw new RuntimeException("factorial() requires a whole number from 0 to 20.");
            }
            long result = 1;
            for (int i = 2; i <= (int) x; i++) result *= i;
            return Double.valueOf(result);
        }

        throw new RuntimeException("Invalid function usage: " + name + "(). Check the function name and number of arguments.");
    }

    private double asNumber(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        throw new RuntimeException("A numeric value was required, but found '" + value + "'.");
    }

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean) return ((Boolean) value).booleanValue();
        throw new RuntimeException("A Boolean value (true or false) was required, but found '" + value + "'.");
    }

    private boolean valuesEqual(Object first, Object second) {
        if (first instanceof Number && second instanceof Number) {
            return ((Number) first).doubleValue() == ((Number) second).doubleValue();
        }
        return first.equals(second);
    }

    private String formatValue(Object value) {
        if (value instanceof Double) {
            double number = ((Double) value).doubleValue();
            if (number == Math.floor(number) && number <= Long.MAX_VALUE && number >= Long.MIN_VALUE) {
                return Long.toString((long) number);
            }
        }
        return String.valueOf(value);
    }

    private void traceResult(String operator, Object value) {
        if (trace) {
            System.out.println("[EVAL] " + operator + " -> " + value);
        }
    }
}
