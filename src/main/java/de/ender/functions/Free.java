package de.ender.functions;

import javax.swing.*;

public class Free extends Function{

    String input;

    public Free(String input) {
        this.input = input;
    }

    @Override
    public double getY(double x) {
        try {
            de.ender.ExpressionEvaluator eval = new de.ender.ExpressionEvaluator(input);
            return eval.evaluate(x);
        } catch (RuntimeException e) {
            // Return NaN for invalid expressions so they don't render
            // and don't crash the UI. The error is already logged elsewhere if needed.
            return Double.NaN;
        }
    }

    @Override
    public Function derivative() {
        return null;
    }

    @Override
    public String toString() {
        return input;
    }

    @Override
    double[] calculateRoots() {
        return new double[0];
    }
}
