package de.ender.functions;

import javax.swing.*;

public class Free extends Function{

    String input;

    public Free(String input) {
        this.input = input;
    }

    @Override
    public double getY(double x) {
        de.ender.ExpressionEvaluator eval = new de.ender.ExpressionEvaluator(input);
        return eval.evaluate(x);
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
