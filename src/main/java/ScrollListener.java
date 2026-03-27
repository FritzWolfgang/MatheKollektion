import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ScrollListener implements MouseWheelListener{

    private final Koordinatensystem koordinatensystem;

    public ScrollListener(Koordinatensystem koordinatensystem) {
        this.koordinatensystem = koordinatensystem;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int currentStepsX = koordinatensystem.getStepsX();
        int currentStepsY = koordinatensystem.getStepsY();

        if(e.getWheelRotation() < 0){
            // zoomIn - scroll up

            //X
            if(currentStepsX == 5){
                koordinatensystem.setStepsX(10);
                koordinatensystem.setStepX(koordinatensystem.getStepX()/2);

                koordinatensystem.setStepsX(koordinatensystem.getStepsX() - 1);
            } else if(currentStepsX > 1) {
                koordinatensystem.setStepsX(currentStepsX - 1);
            }

            //Y
            if(currentStepsY == 5){
                koordinatensystem.setStepsY(10);
                koordinatensystem.setStepY(koordinatensystem.getStepY()/2);

                koordinatensystem.setStepsY(koordinatensystem.getStepsY() - 1);
            } else if(currentStepsY > 1) {
                koordinatensystem.setStepsY(currentStepsY - 1);
            }

        } else {
            // zoomOut - scroll down
            //X
            if(currentStepsX == 10){
                koordinatensystem.setStepsX(5);
                koordinatensystem.setStepX(koordinatensystem.getStepX()*2);

                koordinatensystem.setStepsX(koordinatensystem.getStepsX() + 1);
            } else if(currentStepsX < 10) {
                koordinatensystem.setStepsX(currentStepsX + 1);
            }

            //Y
            if(currentStepsY == 10){
                koordinatensystem.setStepsY(5);
                koordinatensystem.setStepY(koordinatensystem.getStepY()*2);

                koordinatensystem.setStepsY(koordinatensystem.getStepsY() + 1);
            } else if(currentStepsY < 10) {
                koordinatensystem.setStepsY(currentStepsY + 1);
            }
        }







        koordinatensystem.repaint();
    }

}
