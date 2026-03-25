import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Koordinatensystem extends JPanel {

    double stepX, stepY;
    int stepsX, stepsY;

    public Koordinatensystem(double stepX, int stepsCountX, double stepY, int stepsCountY) {
            this.stepX = stepX;
            this.stepY = stepY;
            this.stepsX = stepsCountX;
            this.stepsY = stepsCountY;
    }

    List<Polynom> graphen = new ArrayList<>();

    List<Koordinate> punkte = new ArrayList<>();



    void update(Graphics2D g2d){
        Window window = SwingUtilities.getWindowAncestor(this);
        if (!(window instanceof JFrame frame)) return;

        double windowWidth = getWidth();
        double windowHeight = getHeight();

        frame.setTitle("Koordinatensystem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //AXES

        //X Axis
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, getWindowY(0), (int) windowWidth, getWindowY(0));

        //add labeling x
        g2d.setFont(g2d.getFont().deriveFont(14f));
        FontMetrics fm = g2d.getFontMetrics();

        int centerY = getWindowY(0);
        int centerX = getWindowX(0);

        for(int i = -stepsX; i <= stepsX; i++){
            double xCoord = i * stepX;
            drawLine(g2d, xCoord, -0.1, xCoord, +0.1);
            String label = String.valueOf(xCoord);
            int labelW = fm.stringWidth(label);
            int x = getWindowX(xCoord);
            g2d.drawString(label, x - labelW / 2, centerY + fm.getAscent() + 8);
        }

        //Y Axis
        g2d.drawLine(getWindowX(0), 0, getWindowX(0), (int) windowHeight);

        //add labeling y
        for(int i = -stepsY; i <= stepsY; i++){
            double coord = i * stepY;
            int y = getWindowY(coord);
            drawLine(g2d, -0.1, coord, 0.1, coord);
            String label = String.valueOf(coord);
            int labelH = (int) fm.getStringBounds(label, g2d).getHeight();
            if(coord != 0) g2d.drawString(label, centerX+3, y - labelH / 2 + fm.getAscent() + 8);
        }

        //draw Polynoms
        for(Polynom polynom : graphen){
            Koordinate vorherigeK = null;
            for(double i = -stepsX; i <= stepsX; i=i+widthBetweenPoints){
                double xCoord = i * stepX;
                Koordinate koordinate = new Koordinate(xCoord, polynom.getY(xCoord));
                if(vorherigeK != null) {
                    drawLine(g2d, vorherigeK, koordinate);
                }
                //drawPoint(g2d, koordinate);
                vorherigeK = koordinate;
            }
        }

        for(Koordinate koordinate : punkte){
            drawPoint(g2d, koordinate);
        }

    }

    double widthBetweenPoints = 0.001;



    void addPolynom(Polynom polynom, boolean nullstellenHighlighten){
        graphen.add(polynom);
        if(nullstellenHighlighten){
            nullstellenMarkieren(polynom);
        }
    }

    void nullstellenMarkieren(Polynom polynom) {
        for (Koordinate koordinate : polynom.gibNullpunkte()) {
            if(koordinate != null) punkte.add(koordinate);
        }
    }

    /*void drawPoint(Graphics2D g2d, double x, double y){
        g2d.fill(new Ellipse2D.Double(getWindowX(x)-5, getWindowY(y)-5, 10, 10));
    }*/

    void drawPoint(Graphics2D g2d, Koordinate k){
        g2d.fill(new Ellipse2D.Double(getWindowX(k.getX())-5, getWindowY(k.getY())-5, 10, 10));
    }

    void drawLine(Graphics2D g2d, double x1, double y1, double x2, double y2){
        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getWindowX(x1), getWindowY(y1), getWindowX(x2), getWindowY(y2));
        g2d.setStroke(stroke);
    }

    void drawLine(Graphics2D g2d, Koordinate k1, Koordinate k2){
        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getWindowX(k1.getX()), getWindowY(k1.getY()), getWindowX(k2.getX()), getWindowY(k2.getY()));
        g2d.setStroke(stroke);
    }


    int getWindowX(double xCoord){
        double centerX = getWidth() / 2.0;
        //ungültig?
        if (stepsX <= 0 || stepX == 0.0) {
            return (int) Math.round(centerX);
        }
        // Pixel: Panel-Breite/2 = stepsX * stepX
        double pixelsPerUnitX = (getWidth() / 2.0) / (stepsX * stepX);
        return (int) Math.round(centerX + xCoord * pixelsPerUnitX);
    }

    int getWindowY(double yCoord){
        double centerY = getHeight() / 2.0;
        //ungültig?
        if (stepsY <= 0 || stepY == 0.0) {
            return (int) Math.round(centerY);
        }
        // Pixel: Panel-Höhe/2 = stepsY * stepY
        double pixelsPerUnitY = (getHeight() / 2.0) / (stepsY * stepY);
        // Grafische Y-Achse zeigt nach unten, daher invertieren
        return (int) Math.round(centerY - yCoord * pixelsPerUnitY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        update(g2d);



    }

    public double getStepX() {
        return stepX;
    }

    public void setStepX(double stepX) {
        this.stepX = stepX;
    }
}
