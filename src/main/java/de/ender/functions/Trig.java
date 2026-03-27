package de.ender.functions;

public class Trig extends Function {

    boolean isSine;
    double amplitude, xOffset, yOffset, b;

    public Trig(double amplitude, boolean sine, double b, double xOffset, double yOffset) {
        this.isSine = sine;
        this.amplitude = amplitude;
        this.yOffset = yOffset;
        this.xOffset = xOffset;
        this.b = b;
    }

    public Trig(double amplitude, boolean sine, double xOffset, double yOffset) {
        this(amplitude, sine, 1.0, xOffset, yOffset);
    }



    @Override
    public double getY(double x) {
        return (isSine) ? amplitude*Math.sin(b*(x-xOffset))+yOffset : amplitude*Math.cos(b*(x-xOffset))+yOffset;
    }

    @Override
    public Trig derivative() {
        Trig derivative;
        if(!isSine && amplitude>0){
            derivative = new Trig(-(amplitude*b), SINE,b, xOffset, yOffset);
        }else if(!isSine && amplitude<0){
            derivative = new Trig(-(amplitude*b), SINE,b, xOffset, yOffset);
        }else{
             derivative = new Trig(amplitude*b, !isSine,b, xOffset, yOffset);
        }
        return derivative;
    }


    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.format("%d", (long) num);
        } else {
            return String.valueOf(num);
        }
    }

    @Override
    public String toString() {
        String string = "";
        string += (amplitude != 1) ? formatNumber(amplitude) : "";
        string += (isSine) ?  "sin(" : "cos(";
        string += (b != 1) ? formatNumber(b) + "(" : "";
        string += (xOffset != 0) ? ((-xOffset > 0) ? "x+" + formatNumber(-xOffset) + ")" : "x" + formatNumber(-xOffset) + ")") : "x)";
        string += (b != 1) ? ")" : "";
        string += (yOffset != 0) ? ((yOffset > 0) ? "+" + formatNumber(yOffset) : formatNumber(yOffset)) : "";
        return string;
    }

    @Override
    double[] calculateRoots() {
        return new double[0];
    }

    public final static boolean SINE = true,
                                COSINE = false;

}
