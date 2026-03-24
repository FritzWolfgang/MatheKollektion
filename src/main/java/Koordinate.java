public class Koordinate {

    double x,y;

    public Koordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "("+Util.runde2(x)+"|"+Util.runde2(y)+")";
    }
}
