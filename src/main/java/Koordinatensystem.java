import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Koordinatensystem extends JPanel {

    double stepX, stepY;
    int stepsX, stepsY;
    DragListener dragListener;

    public Koordinatensystem(double stepX, int stepsCountX, double stepY, int stepsCountY) {
            this.stepX = stepX;
            this.stepY = stepY;
            this.stepsX = stepsCountX;
            this.stepsY = stepsCountY;

            this.dragListener = new DragListener(this);
            this.addMouseMotionListener(dragListener);
            this.addMouseListener(dragListener);

            ScrollListener scrollListener = new ScrollListener(this);
            this.addMouseWheelListener(scrollListener);
    }

    List<Polynomial> graphen = new ArrayList<>();

    List<Koordinate> punkte = new ArrayList<>();



    void update(Graphics2D g2d){
        Window window = SwingUtilities.getWindowAncestor(this);
        if (!(window instanceof JFrame frame)) return;

        double windowWidth = getWidth();
        double windowHeight = getHeight();

        frame.setTitle("Koordinatensystem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //AXES

        /*X --- AXIS*/
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, getWindowY(0), (int) windowWidth, getWindowY(0));

        /*LABELING X*/
        g2d.setFont(g2d.getFont().deriveFont(14f));
        FontMetrics fm = g2d.getFontMetrics();

        int centerY = getWindowY(0);
        int centerX = getWindowX(0);

        double offsetPixelsX = dragListener.getOffsetX();
        double offsetPixelsY = dragListener.getOffsetY();

        // Calculate visible coordinate range based on panning
        double pixelsPerUnitX = (getWidth() / 2.0) / (stepsX * stepX);
        double pixelsPerUnitY = (getHeight() / 2.0) / (stepsY * stepY);

        double minX = -stepsX * stepX - offsetPixelsX / pixelsPerUnitX;
        double maxX = stepsX * stepX - offsetPixelsX / pixelsPerUnitX;
        double minY = -stepsY * stepY + offsetPixelsY / pixelsPerUnitY;
        double maxY = stepsY * stepY + offsetPixelsY / pixelsPerUnitY;

        // Find the first valid label position
        double startX = Math.ceil(minX / stepX) * stepX;
        double startY = Math.ceil(minY / stepY) * stepY;

        /*LABELING X*/
        //draw left to right
        for(double xCoord = startX; xCoord <= maxX; xCoord += stepX){
            drawLineX(g2d, xCoord);

            //String
            String label = String.format("%.2f", xCoord);
            int labelW = fm.stringWidth(label);
            int windowX = getWindowX(xCoord);
            g2d.drawString(label, windowX - labelW / 2, centerY + fm.getAscent() + 8);
        }

        /*Y --- AXIS*/
        g2d.drawLine(getWindowX(0), 0, getWindowX(0), (int) windowHeight);

        /*LABELING Y*/
        for(double yCoord = startY; yCoord <= maxY; yCoord += stepY){
            drawLineY(g2d, yCoord);

            //String
            String label = String.format("%.2f", yCoord);
            int labelH = (int) fm.getStringBounds(label, g2d).getHeight();
            int windowY = getWindowY(yCoord);
            if(Math.abs(yCoord) > 0.001) g2d.drawString(label, centerX+3, windowY - labelH / 2 + fm.getAscent() + 8);
        }

        /*POLYNOMIALS*/
        for(Polynomial polynomial : graphen){
            Koordinate vorherigeK = null;
            for(double xCoord = startX; xCoord <= maxX; xCoord+=widthBetweenPoints){
                Koordinate koordinate = new Koordinate(xCoord, polynomial.getY(xCoord));
                if(vorherigeK != null) {
                    drawLine(g2d, vorherigeK, koordinate);
                }
                //drawPoint(g2d, koordinate);
                vorherigeK = koordinate;
            }
        }

        /*POINTS*/
        for(Koordinate koordinate : punkte){
            g2d.setColor(Color.RED);
            drawPoint(g2d, koordinate);
        }

    }

    double widthBetweenPoints = 0.001;



    void addPolynom(Polynomial polynomial, boolean highlightRoots){
        graphen.add(polynomial);
        if(highlightRoots){
            nullstellenMarkieren(polynomial);
        }
    }

    void nullstellenMarkieren(Polynomial polynomial) {
        for (Koordinate koordinate : polynomial.gibNullpunkte()) {
            if(koordinate != null) punkte.add(koordinate);
        }
    }

    /*void drawPoint(Graphics2D g2d, double x, double y){
        g2d.fill(new Ellipse2D.Double(getWindowX(x)-5, getWindowY(y)-5, 10, 10));
    }*/

    void drawPoint(Graphics2D g2d, Koordinate k){
        g2d.fill(new Ellipse2D.Double(getWindowX(k.getX())-5, getWindowY(k.getY())-5, 10, 10));
    }

    void drawLineX(Graphics2D g2d, double x1){
        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getWindowX(x1), getWindowY(0)-10, getWindowX(x1), getWindowY(0)+10);
        g2d.setStroke(stroke);
    }

    void drawLineY(Graphics2D g2d, double y1){
        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(getWindowX(0)-10, getWindowY(y1), getWindowX(0)+10, getWindowY(y1));
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
            return (int) Math.round(centerX + dragListener.getOffsetX());
        }
        // Pixel: Panel-Breite/2 = stepsX * stepX
        double pixelsPerUnitX = (getWidth() / 2.0) / (stepsX * stepX);
        return (int) Math.round(centerX + xCoord * pixelsPerUnitX + dragListener.getOffsetX());
    }

    int getWindowY(double yCoord){
        double centerY = getHeight() / 2.0;
        //ungültig?
        if (stepsY <= 0 || stepY == 0.0) {
            return (int) Math.round(centerY + dragListener.getOffsetY());
        }
        // Pixel: Panel-Höhe/2 = stepsY * stepY
        double pixelsPerUnitY = (getHeight() / 2.0) / (stepsY * stepY);
        // invertieren
        return (int) Math.round(centerY - yCoord * pixelsPerUnitY + dragListener.getOffsetY());
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
        if (stepX > 0) {
            this.stepX = stepX;
        }
    }

    public double getStepY() {
        return stepY;
    }

    public void setStepY(double stepY) {
        if (stepY > 0) {
            this.stepY = stepY;
        }
    }

    public int getStepsX() {
        return stepsX;
    }

    public void setStepsX(int stepsX) {
        this.stepsX = stepsX;
    }

    public int getStepsY() {
        return stepsY;
    }

    public void setStepsY(int stepsY) {
        this.stepsY = stepsY;
    }

    public void zoomF(double zoomFactor) {
        double newStepX = getStepX() * zoomFactor;
        double newStepY = getStepY() * zoomFactor;

        setStepX(newStepX);
        setStepY(newStepY);
    }

    public void zoomAdd(double zoomAddent) {
        double newStepX = getStepX() + zoomAddent;
        double newStepY = getStepY() + zoomAddent;

        setStepX(newStepX);
        setStepY(newStepY);
    }
}
