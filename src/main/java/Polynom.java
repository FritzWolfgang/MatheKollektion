public class Polynom {

    double[] koeffizienten;

    public Polynom(double... koeffizienten) {
        this.koeffizienten = koeffizienten;
    }

    public double getY(double x) {
        double result = 0;
        int n = koeffizienten.length;
        for (int i = 0; i < n; i++) {
            result += koeffizienten[i] * Math.pow(x, n - i - 1);
        }
        return result;
    }


    public double numerischeAbleitung(double x){ //h-methode
        double h = 0.00001; //h->0
        return Util.runde2((getY(x + h) - getY(x)) / h);
    }

    public Polynom ableitung() {
        int n = koeffizienten.length;
        if (n == 1) return new Polynom(0); // konstante Funktion
        double[] abl = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            abl[i] = koeffizienten[i] * (n - i - 1);
        }
        return new Polynom(abl);
    }

    public double ableitung(double x){
        return ableitung().getY(x);
    }

    public double[] berechneNullstellen() {
        int n = koeffizienten.length;

        if (n == 1) { // konstant
            return new double[0];
        } else if (n == 2) { // linear
            double a = koeffizienten[0];
            double b = koeffizienten[1];
            if (a == 0) return new double[0];
            return new double[]{ Util.runde2(-b / a) };
        } else if (n == 3) { // quadratisch
            double a = koeffizienten[0];
            double b = koeffizienten[1];
            double c = koeffizienten[2];
            double diskriminante = b*b - 4*a*c;
            double nenner = 2*a;
            double[] nullstellen;

            if (diskriminante > 0) {
                double zaeler1 = -b + Math.sqrt(diskriminante);
                double zaeler2 = -b - Math.sqrt(diskriminante);
                nullstellen = new double[2];
                nullstellen[0] = Util.runde2(zaeler1 / nenner);
                nullstellen[1] = Util.runde2(zaeler2 / nenner);
            } else if (diskriminante == 0) {
                nullstellen = new double[]{ Util.runde2(-b / nenner) };
            } else {
                System.out.println("Keine reellen Nullstellen: Diskriminante negativ!");
                nullstellen = new double[0];
            }

            return nullstellen;
        } else {
            System.out.println("Polynome höheren Grades müssen numerisch gelöst werden!");
            return new double[0];
        }
    }

    public Koordinate[] gibNullpunkte(){
        double[] xKoordinate = berechneNullstellen();
        Koordinate[] koordinaten = new Koordinate[2];
        for(int i = 0; i < xKoordinate.length; i++){
            koordinaten[i] = new Koordinate(xKoordinate[i], getY(xKoordinate[i]));
        }
        return koordinaten;
    }

    public boolean aufGraphen(Koordinate koordinate){
        return getY(koordinate.getX()) == koordinate.getY();
    }


    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        int n = koeffizienten.length;
        for (int i = 0; i < n; i++) {
            double co = koeffizienten[i];   //koeffizient
            if (co == 0) continue;
            int exponent = n - i - 1;       //exponent
            if (!st.isEmpty()) st.append(co > 0 ? " + " : " - ");
            else if (co < 0) st.append("-"); //erster koeffizient kein Vorzeichen wenn +
            double abs = Math.abs(co); //Betrag
            double epsilon = 1e-10;
            if (!(abs == 1 && exponent > 0)) { //wenn co nicht 1 und exponent negativ -> nullen weg
                if (Math.abs(abs - Math.round(abs)) < epsilon) {
                    st.append(Math.round(abs));
                } else {
                    st.append(abs);
                }
            }
            if (exponent > 0) st.append("x").append(exponent > 1 ? "^" + exponent : ""); //exponenten
        }
        return st.isEmpty() ? "0" : st.toString();
    }
}
