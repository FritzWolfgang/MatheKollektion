import javax.swing.*;

void main() {
    Polynomial funktion = new Polynomial(15,2, 0, 0);

    Koordinatensystem koordinatensystem = new Koordinatensystem(1, 5, 5, 5);
    koordinatensystem.addPolynom(funktion, true);
    koordinatensystem.addPolynom(new Polynomial(2, 5), true);

    JFrame frame = new JFrame();
    frame.setSize(1000, 1000);
    frame.add(koordinatensystem);

    frame.setVisible(true);
}
