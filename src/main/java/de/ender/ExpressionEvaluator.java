// src/main/java/de/ender/functions/ExpressionEvaluator.java
package de.ender;

import java.util.Locale;

public class ExpressionEvaluator {
    private final String input;
    private String s;
    private int pos = -1;
    private int ch;

    public ExpressionEvaluator(String input) {
        this.input = input;
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
        switch (name) {
            case "sin": return Math.sin(arg);
            case "cos": return Math.cos(arg);
            case "tan": return Math.tan(arg);
            case "asin": return Math.asin(arg);
            case "acos": return Math.acos(arg);
            case "atan": return Math.atan(arg);
            case "sqrt": return Math.sqrt(arg);
            case "abs": return Math.abs(arg);
            case "ln": return Math.log(arg);
            case "log": return Math.log10(arg);
            case "exp": return Math.exp(arg);
            default: throw new RuntimeException("Unknown function: " + name);
        }
    }

    // --- helpers ---
    private static boolean isLetter(int c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean isNumberChar(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

    // preprocess: remove spaces, normalize pi, insert implicit multiplication (e.g. 3x, 3sin(...), )(
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
        // normalize variable and pi to lower-case tokens
        return out.toString().replaceAll("PI", "pi").replaceAll("Pi", "pi").replaceAll("pI", "pi");
    }
}
