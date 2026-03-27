package de.ender.listeners;

import de.ender.coordinates.CoordinateSystem;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ScrollListener implements MouseWheelListener{

    private final CoordinateSystem coordinateSystem;

    public ScrollListener(CoordinateSystem coordinateSystem) {
        this.coordinateSystem = coordinateSystem;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int currentStepsX = coordinateSystem.getStepsX();
        int currentStepsY = coordinateSystem.getStepsY();

        if(e.getWheelRotation() < 0){
            // zoomIn - scroll up

            //X
            if(currentStepsX == 5){
                coordinateSystem.setStepsX(10);
                coordinateSystem.setStepX(coordinateSystem.getStepX()/2);

                coordinateSystem.setStepsX(coordinateSystem.getStepsX() - 1);
            } else if(currentStepsX > 1) {
                coordinateSystem.setStepsX(currentStepsX - 1);
            }

            //Y
            if(currentStepsY == 5){
                coordinateSystem.setStepsY(10);
                coordinateSystem.setStepY(coordinateSystem.getStepY()/2);

                coordinateSystem.setStepsY(coordinateSystem.getStepsY() - 1);
            } else if(currentStepsY > 1) {
                coordinateSystem.setStepsY(currentStepsY - 1);
            }

        } else {
            // zoomOut - scroll down
            //X
            if(currentStepsX == 10){
                coordinateSystem.setStepsX(5);
                coordinateSystem.setStepX(coordinateSystem.getStepX()*2);

                coordinateSystem.setStepsX(coordinateSystem.getStepsX() + 1);
            } else if(currentStepsX < 10) {
                coordinateSystem.setStepsX(currentStepsX + 1);
            }

            //Y
            if(currentStepsY == 10){
                coordinateSystem.setStepsY(5);
                coordinateSystem.setStepY(coordinateSystem.getStepY()*2);

                coordinateSystem.setStepsY(coordinateSystem.getStepsY() + 1);
            } else if(currentStepsY < 10) {
                coordinateSystem.setStepsY(currentStepsY + 1);
            }
        }







        coordinateSystem.repaint();
    }

}
