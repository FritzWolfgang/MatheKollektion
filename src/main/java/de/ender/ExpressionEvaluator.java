/*
 ExpressionEvaluator
 ---------------------------------
 Purpose:
  - Parse and evaluate a mathematical expression given as a String.
  - Supports numeric literals, the variable `x`, the constant `pi`, common math
    functions (sin, cos, tan, asin, acos, atan, sqrt, abs, ln, log, exp),
    binary operators (+, -, *, /, ^) and parentheses.

 Features / behavior:
  - Preprocessing: whitespace removed, `pi` normalized, and implicit multiplication
    is inserted (e.g. "2x" -> "2*x", "2(x+1)" -> "2*(x+1)").
  - Parsing: recursive-descent parser with the following precedence (high -> low):
      primary (numbers, x, pi, function calls, parentheses)
      unary (+, -)
      exponentiation (^)
      multiplication/division (*, /)
      addition/subtraction (+, -)
  - Evaluation: call `evaluate(xVal)` to evaluate the expression with `x` substituted
    by `xVal`.

 Important notes / edge cases:
  - Exponentiation is implemented as repeated application of `Math.pow` and behaves
    left-to-right in this implementation (i.e. left-associative); if right-associative
    behavior (2^(3^2)) is required, the parser needs a small change.
  - Unknown identifiers (except `x` and `pi`) are treated as function names only
    when followed by parentheses (e.g. "sin(1)"). Bare unknown names will raise
    a RuntimeException.
  - Malformed numbers or syntax errors throw RuntimeException or NumberFormatException.

 Example usages:
  new ExpressionEvaluator("2x + sin(pi/2)").evaluate(3)  // -> 7.0
  new ExpressionEvaluator("(pi/2) + 1").evaluate(0)    // -> ~2.5708
*/

package de.ender;

import java.util.Locale;

public class ExpressionEvaluator {
    private final String s;
    private int pos = -1;
    private int ch;

    public ExpressionEvaluator(String input) {
        this.s = preprocess(input);
    }


    public double evaluate(double xVal) {
        pos = -1;
        ch = -1;
        nextChar();
        double v = parseExpression(xVal);
        if (pos < s.length()) throw new RuntimeException("Unexpected: " + (char) ch);
        return v;
    }

    // --- parser ---
    private void nextChar() {
        pos++;
        ch = pos < s.length() ? s.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private double parseExpression(double xVal) {
        double x = parseTerm(xVal);
        for (;;) {
            if (eat('+')) x += parseTerm(xVal);
            else if (eat('-')) x -= parseTerm(xVal);
            else return x;
        }
    }

    private double parseTerm(double xVal) {
        double x = parseFactor(xVal);
        for (;;) {
            if (eat('*')) x *= parseFactor(xVal);
            else if (eat('/')) x /= parseFactor(xVal);
            else return x;
        }
    }

    private double parseFactor(double xVal) {
        double x = parseUnary(xVal);
        while (eat('^')) {
            double exponent = parseUnary(xVal);
            x = Math.pow(x, exponent);
        }
        return x;
    }

    private double parseUnary(double xVal) {
        if (eat('+')) return parseUnary(xVal);
        if (eat('-')) return -parseUnary(xVal);
        return parsePrimary(xVal);
    }

    private double parsePrimary(double xVal) {
        if (eat('(')) {
            double v = parseExpression(xVal);
            if (!eat(')')) throw new RuntimeException("Missing ')'");
            return v;
        }

        if (isLetter(ch)) {
            StringBuilder sb = new StringBuilder();
            while (isLetter(ch)) {
                sb.append((char) ch);
                nextChar();
            }
            String name = sb.toString().toLowerCase(Locale.ROOT);
            if ("x".equals(name)) return xVal;
            if ("pi".equals(name)) return Math.PI;

            // function call
            if (eat('(')) {
                double arg = parseExpression(xVal);
                if (!eat(')')) throw new RuntimeException("Missing ')' after function arg");
                return applyFunction(name, arg);
            } else {
                // treat bare name as variable only 'x' or 'pi' are valid; otherwise error
                throw new RuntimeException("Unknown identifier: " + name);
            }
        }

        // number
        if ((ch >= '0' && ch <= '9') || ch == '.') {
            StringBuilder sb = new StringBuilder();
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                sb.append((char) ch);
                nextChar();
            }
            return Double.parseDouble(sb.toString());
        }

        throw new RuntimeException("Unexpected: " + (char) ch);
    }

    private double applyFunction(String name, double arg) {
        return switch (name) {
            case "sin" -> Math.sin(arg);
            case "cos" -> Math.cos(arg);
            case "tan" -> Math.tan(arg);
            case "asin" -> Math.asin(arg);
            case "acos" -> Math.acos(arg);
            case "atan" -> Math.atan(arg);
            case "sqrt" -> Math.sqrt(arg);
            case "abs" -> Math.abs(arg);
            case "ln" -> Math.log(arg);
            case "log" -> Math.log10(arg);
            case "exp" -> Math.exp(arg);
            default -> throw new RuntimeException("Unknown function: " + name);
        };
    }

    // --- helpers ---
    private static boolean isLetter(int c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean isNumberChar(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

    // preprocess
    private String preprocess(String in) {
        String t = in.replaceAll("\\s+", "");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            out.append(c);
            if (i + 1 < t.length()) {
                char n = t.charAt(i + 1);
                boolean leftIsNumXOrParen = isNumberChar(c) || c == 'x' || c == 'X' || c == ')';
                boolean rightIsLetterOrXOrParenOrNum = isLetter(n) || n == 'x' || n == 'X' || n == '(' || isNumberChar(n);
                // avoid inserting between letters (function names)
                if (leftIsNumXOrParen && rightIsLetterOrXOrParenOrNum && !(isLetter(c) && isLetter(n))) {
                    out.append('*');
                }
            }
        }
        // normalize pi
        return out.toString().replaceAll("PI", "pi").replaceAll("Pi", "pi").replaceAll("pI", "pi");
    }
}
