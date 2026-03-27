import de.ender.coordinates.CoordinateSystem;

import javax.swing.*;

void main() {
    JFrame frame = new JFrame();
    frame.setSize(1000, 1000);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);  // null layout for absolute positioning

    CoordinateSystem coordinateSystem = new CoordinateSystem(Math.PI/6, 5, 1, 5);

    // Set the CoordinateSystem to fill the entire frame
    coordinateSystem.setBounds(0, 0, frame.getWidth(), frame.getHeight());

    frame.add(coordinateSystem);


    frame.setVisible(true);
}
