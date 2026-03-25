import javax.swing.*;

void main() {
    Polynom funktion = new Polynom(0.2, 0, 0);

    Koordinatensystem koordinatensystem = new Koordinatensystem(1, 10, 5, 5);
    koordinatensystem.addPolynom(funktion, true);
    koordinatensystem.addPolynom(new Polynom(2, 5), true);

    JFrame frame = new JFrame();
    frame.setSize(1000, 1000);
    frame.add(koordinatensystem);

    frame.setVisible(true);
}
