import java.util.Scanner;

public class LargeIntDemo {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter an upper bound for the factoral. ");
        String upperbound = input.nextLine(); 
        LargeInt limit = new LargeInt(upperbound);
        System.out.println("Factorials from 0 to 100:");
        LargeInt counter = new LargeInt();
        while (counter.compareTo(limit) == -1)
        {
            counter = counter.add(new LargeInt(1));
            LargeInt factResult = factorial(counter);
            
            System.out.println(counter.toString() + " factorial is " + factResult.toString() + " and has " + factResult.numDigits() + " digits.");
        }

        /*
        for (int i = 0; i < 101; i++)
        {
            LargeInt factResult = factorial(new LargeInt(i))
            System.out.println(i + " factorial is " + factResult.toString() + " and has " + factResult.numDigits() + " digits.");
;
        }
            */ 

    }

    public static LargeInt factorial(LargeInt i)
    {
        if (i.equals(new LargeInt(0)) || i.equals(new LargeInt(1))){
            return new LargeInt( 1); 
        }
        LargeInt product = new LargeInt(1);
        LargeInt counter = new LargeInt(1);
        while (true) 
        { 
            counter = counter.add(new LargeInt(1));
            product = product.multiply(counter);
            
            // System.out.println("Current counter " + counter.toString() + " current product " + product.toString());

            if (counter.equals(i) || counter.compareTo(i) == 1){
                break; 
            }
        }
        return product; // I FUCKING LOVE ABSTRACTION
    }

    //public static Largeint recursiveFactorial(LargeInt i)
}
