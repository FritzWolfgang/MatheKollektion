package de.ender.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class DragListener implements MouseMotionListener, MouseListener {

    double offsetX = 0;
    double offsetY = 0;
    private int lastMouseX;
    private int lastMouseY;
    private final JPanel panel;

    public DragListener(JPanel panel) {
        this.panel = panel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int currentX = e.getX();
        int currentY = e.getY();

        offsetX += (currentX - lastMouseX);
        offsetY += (currentY - lastMouseY);

        lastMouseX = currentX;
        lastMouseY = currentY;

        panel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Not needed for dragging
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not needed for dragging
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Offset is retained, so coordinate system stays panned
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed for dragging
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed for dragging
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}
