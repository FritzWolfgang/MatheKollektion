import java.util.Arrays;

public class Main {

    void main(){
        Polynom funktion = new Polynom(3,5,0,0,0);
        System.out.println(funktion.ableitung(10));
        System.out.println(funktion.numerischeAbleitung(10));
    }

}
