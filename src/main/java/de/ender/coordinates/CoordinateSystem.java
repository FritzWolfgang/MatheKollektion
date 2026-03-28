package de.ender.coordinates;

import de.ender.Util;
import de.ender.functions.Function;
import de.ender.listeners.DragListener;
import de.ender.listeners.ScrollListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CoordinateSystem extends JPanel {

    double stepX, stepY;
    int stepsX, stepsY;
    DragListener dragListener;
    JFrame frame;

    public CoordinateSystem(double stepX, int stepsCountX, double stepY, int stepsCountY, JFrame frame) {
            this.stepX = stepX;
            this.stepY = stepY;
            this.stepsX = stepsCountX;
            this.stepsY = stepsCountY;
            this.frame = frame;
            this.dragListener = new DragListener(this);
            this.addMouseMotionListener(dragListener);
            this.addMouseListener(dragListener);

            frame.setTitle("Coordinate System");
        try {
            URI url = Paths.get("https://cdn-icons-png.freepik.com/512/14694/14694921.png").toUri();
            BufferedImage image = ImageIO.read(url.toURL());
            // Change color to white
            BufferedImage coloredImage = Util.changeImageColor(image, Color.WHITE);
            frame.setIconImage(new ImageIcon(coloredImage).getImage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

            ScrollListener scrollListener = new ScrollListener(this);
            this.addMouseWheelListener(scrollListener);

            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    repaint();
                }
            });
    }

    List<Function> graphen = new ArrayList<>();

    List<Coords> punkte = new ArrayList<>();

    Color getGraphColor(Function f){
        List<Color> colors = List.of(new Color(220, 4, 4)
                , new Color(232, 104, 3)
                , new Color(243, 222, 3)
                , new Color(85, 222, 4)
                , new Color(8, 229, 144)
                , new Color(7, 58, 224)
                , new Color(225, 6, 222)
                , new Color(210, 4, 67));

        int index = graphen.indexOf(f);
        if (index < 0) {
            return Color.BLACK;
        }
        return colors.get(index % colors.size());
    }

    void update(Graphics2D g2d){

        double windowWidth = getWidth();
        double windowHeight = getHeight();



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

        //first label position
        double coordAtWindowX0 = -(getWidth() / 2.0) / pixelsPerUnitX - offsetPixelsX / pixelsPerUnitX;
        double startX = Math.ceil(coordAtWindowX0 / stepX) * stepX;
        double startY = Math.ceil(minY / stepY) * stepY;

        /*LABELING X*/
        //draw left to right
        for(double xCoord = startX; xCoord <= maxX; xCoord += stepX){
            if(xCoord != 0){
                g2d.setColor(Color.gray);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(getWindowX(xCoord), (int) windowHeight, getWindowX(xCoord), 0);
                g2d.setColor(Color.black);
                g2d.setStroke(new BasicStroke(3));
            }

            drawLineX(g2d, xCoord);
            //String
            String label = Util.formatLabel(xCoord);
            int labelW = fm.stringWidth(label);
            int windowX = getWindowX(xCoord);
            g2d.drawString(label, windowX - labelW / 2, centerY + fm.getAscent() + 8);
        }

        /*Y --- AXIS*/
        g2d.drawLine(getWindowX(0), 0, getWindowX(0), (int) windowHeight);

        /*LABELING Y*/
        for(double yCoord = startY; yCoord <= maxY; yCoord += stepY){
            if(yCoord != 0){
                g2d.setColor(Color.gray);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(0, getWindowY(yCoord), (int) windowWidth, getWindowY(yCoord));
                g2d.setColor(Color.black);
                g2d.setStroke(new BasicStroke(3));
            }
            drawLineY(g2d, yCoord);
            //String
            String label = Util.formatLabel(yCoord);
            int labelH = (int) fm.getStringBounds(label, g2d).getHeight();
            int windowY = getWindowY(yCoord);
            if(Math.abs(yCoord) > 0.001) g2d.drawString(label, centerX+3, windowY - labelH / 2 + fm.getAscent() + 8);
        }

        double widthBetweenPoints = Math.max(5/pixelsPerUnitX, stepX/10);

        /*POLYNOMIALS*/
        for(Function function : graphen){
            double lastY = Double.NaN;
            double visibleYRange = maxY - minY;
            double maxYJump = 3 * visibleYRange;

            for(double xCoord = minX; xCoord <= maxX; xCoord+=widthBetweenPoints){
                double y = function.getY(xCoord);

                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    lastY = Double.NaN;
                    continue;
                }

                if (!Double.isNaN(lastY) && Math.abs(y - lastY) > maxYJump) {
                    lastY = Double.NaN;
                    continue;
                }

                Coords c = new Coords(xCoord, y);
                if (!Double.isNaN(lastY)) {
                    Coords lastC = new Coords(xCoord - widthBetweenPoints, lastY);
                    drawLine(g2d, lastC, c, getGraphColor(function));
                }
                lastY = y;
            }
        }

        /*POINTS*/
        for(Coords c : punkte){
            g2d.setColor(Color.RED);
            drawPoint(g2d, c);
        }

    }





    public void addFunction(Function f){
        graphen.add(f);
        repaint();
    }

    public void removeFunction(Function f){
        graphen.remove(f);
        repaint();
    }

    /*void nullstellenMarkieren(Function f) {
        for (Coords c : f.getRoots()) {
            if(c != null) punkte.add(c);
        }
    }*/

    /*void drawPoint(Graphics2D g2d, double x, double y){
        g2d.fill(new Ellipse2D.Double(getWindowX(x)-5, getWindowY(y)-5, 10, 10));
    }*/

    void drawPoint(Graphics2D g2d, Coords k){
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

    void drawLine(Graphics2D g2d, Coords k1, Coords k2, Color color){
        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(color);
        g2d.drawLine(getWindowX(k1.getX()), getWindowY(k1.getY()), getWindowX(k2.getX()), getWindowY(k2.getY()));
        g2d.setStroke(stroke);
        g2d.setColor(Color.black);
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
        if (stepX > 0.01 && stepX < 20) {
            this.stepX = stepX;
        }
    }

    public double getStepY() {
        return stepY;
    }

    public void setStepY(double stepY) {
        if (stepY > 0.01 && stepY < 20) {
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

    public List<Function> getGraphen() {
        return graphen;
    }

    public void setGraphen(List<Function> graphen) {
        this.graphen = graphen;
    }
}
