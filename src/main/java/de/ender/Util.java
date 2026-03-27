package de.ender;

public class Util {

    public static double runde2(double a){
        return Math.round(a*100)/100.0;
    }

    /**
     * Format label as fraction of π if close to a multiple of π/3 or π/4.
     */
    public static String formatLabel(double coord) {
        int[] denominators = {1, 2, 3, 4, 5, 6};
        double tolerance = 0.0001;

        for (int denom : denominators) {
            double piOverDenom = Math.PI / denom;
            long num = Math.round(coord / piOverDenom);

            if (Math.abs(coord - num * piOverDenom) < tolerance) {
                // Simplify fraction
                long g = gcd(Math.abs(num), denom);
                num /= g;
                int simpleDenom = denom / (int) g;

                if (num == 0) return "0";
                if (simpleDenom == 1) return (num == 1 ? "π" : num == -1 ? "-π" : num + "π");
                if (num == 1) return "π/" + simpleDenom;
                if (num == -1) return "-π/" + simpleDenom;
                return num + "π/" + simpleDenom;
            }
        }
        return String.format("%.2f", coord);
    }

    public static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }




}
