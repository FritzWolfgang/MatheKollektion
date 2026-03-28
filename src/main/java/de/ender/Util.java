package de.ender;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    public static BufferedImage changeImageColor(BufferedImage image, Color targetColor) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int targetRGB = targetColor.getRGB();
        int targetRed = (targetRGB >> 16) & 0xFF;
        int targetGreen = (targetRGB >> 8) & 0xFF;
        int targetBlue = targetRGB & 0xFF;

        // Fill background with black
        Graphics2D g2d = result.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, result.getWidth(), result.getHeight());
        g2d.dispose();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);

                // Extract alpha channel
                int alpha = (rgb >> 24) & 0xFF;

                // Only process if pixel is not transparent
                if (alpha > 0) {
                    // Extract original RGB values
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Calculate luminance of original pixel (using standard formula)
                    double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;

                    // For dark pixels (luminance < 0.5), invert the luminance so dark becomes light
                    if (luminance < 0.5) {
                        luminance = 1.0 - luminance;
                    }

                    // Scale target color by adjusted luminance
                    int newRed = (int) (targetRed * luminance);
                    int newGreen = (int) (targetGreen * luminance);
                    int newBlue = (int) (targetBlue * luminance);

                    // Combine RGB values
                    int newRGB = (newRed << 16) | (newGreen << 8) | newBlue;
                    result.setRGB(x, y, newRGB);
                }
                // Transparent pixels will keep the black background
            }
        }

        return result;
    }


}
