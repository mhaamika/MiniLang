import java.util.ArrayList;
import java.util.List;

// Converts the input program into tokens for the parser.
public class Lexer {
    private String text;
    private int pos;

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
    }

    private char currentChar() {
        if (pos >= text.length()) {
            return '\0';
        }
        return text.charAt(pos);
    }

    private char nextChar() {
        if (pos + 1 >= text.length()) {
            return '\0';
        }
        return text.charAt(pos + 1);
    }

    private void advance() {
        pos++;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<Token>();

        while (currentChar() != '\0') {
            char ch = currentChar();

            if (Character.isWhitespace(ch)) {
                advance();
            } else if (ch == '/' && nextChar() == '/') {
                // Ignore everything from // to the end of the line.
                while (currentChar() != '\0' && currentChar() != '\n') {
                    advance();
                }
            } else if (Character.isLetter(ch) || ch == '_') {
                tokens.add(readIdentifier());
            } else if (Character.isDigit(ch)) {
                tokens.add(readNumber());
            } else if (ch == '"' || ch == '\'') {
                tokens.add(readString());
            } else {
                tokens.add(readOperator());
            }
        }

        tokens.add(new Token("EOF", ""));
        return tokens;
    }

    private Token readIdentifier() {
        String name = "";
        while (Character.isLetterOrDigit(currentChar()) || currentChar() == '_') {
            name += currentChar();
            advance();
        }
        return new Token("IDENTIFIER", name);
    }

    private Token readNumber() {
        String number = "";
        boolean decimalPointSeen = false;

        while (Character.isDigit(currentChar()) || currentChar() == '.') {
            if (currentChar() == '.') {
                if (decimalPointSeen) {
                    throw new RuntimeException("Invalid number: " + number + ".");
                }
                decimalPointSeen = true;
            }
            number += currentChar();
            advance();
        }

        if (number.endsWith(".")) {
            throw new RuntimeException("Invalid number: " + number);
        }
        return new Token("NUMBER", number);
    }

    private Token readString() {
        char quote = currentChar();
        advance();
        String value = "";

        while (currentChar() != quote && currentChar() != '\0') {
            if (currentChar() == '\\' && nextChar() == quote) {
                advance();
            }
            value += currentChar();
            advance();
        }

        if (currentChar() == '\0') {
            throw new RuntimeException("String is missing its closing quote.");
        }

        advance();
        return new Token("STRING", value);
    }

    private Token readOperator() {
        char ch = currentChar();
        char next = nextChar();
        advance();

        if (ch == '<' && next == '=') { advance(); return new Token("LTE", "<="); }
        if (ch == '>' && next == '=') { advance(); return new Token("GTE", ">="); }
        if (ch == '=' && next == '=') { advance(); return new Token("EQUAL_EQUAL", "=="); }
        if (ch == '!' && next == '=') { advance(); return new Token("NOT_EQUAL", "!="); }
        if (ch == '&' && next == '&') { advance(); return new Token("AND", "&&"); }
        if (ch == '|' && next == '|') { advance(); return new Token("OR", "||"); }

        switch (ch) {
            case '+': return new Token("PLUS", "+");
            case '-': return new Token("MINUS", "-");
            case '*': return new Token("MULTIPLY", "*");
            case '/': return new Token("DIVIDE", "/");
            case '%': return new Token("MODULO", "%");
            case '^': return new Token("POWER", "^");
            case '(': return new Token("LPAREN", "(");
            case ')': return new Token("RPAREN", ")");
            case ',': return new Token("COMMA", ",");
            case ';': return new Token("SEMICOLON", ";");
            case '=': return new Token("ASSIGN", "=");
            case '<': return new Token("LT", "<");
            case '>': return new Token("GT", ">");
            case '!': return new Token("NOT", "!");
            default: throw new RuntimeException("Invalid character: '" + ch + "'");
        }
    }
}
