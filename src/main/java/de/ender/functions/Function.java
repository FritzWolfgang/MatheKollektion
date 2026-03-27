package de.ender.functions;

import de.ender.coordinates.Coords;
import de.ender.Util;

public abstract class Function {




    abstract public double getY(double x);
    abstract public Function derivative();

    public double getDerivative(double x){
        return derivative().getY(x);
    }

    @Override
    abstract public String toString();

    public double numericDerivative(double x){ //h-methode
        double h = 0.00001; //h->0
        return Util.runde2((getY(x + h) - getY(x)) / h);
    }

    public boolean aufGraphen(Coords coords){
        return getY(coords.getX()) == coords.getY();
    }

    abstract double[] calculateRoots();

    public Coords[] getRoots(){
        double[] xKoordinate = calculateRoots();
        Coords[] koordinaten = new Coords[2];
        for(int i = 0; i < xKoordinate.length; i++){
            koordinaten[i] = new Coords(xKoordinate[i], getY(xKoordinate[i]));
        }
        return koordinaten;
    }


}
