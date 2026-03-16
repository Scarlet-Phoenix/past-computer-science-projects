import java.util.ArrayList;

public class LargeIntTester {
    public static void main(String args[]) {
       // testConstructors();
       // testAdd(); 
        //testEquals();  
        //testCompareTo();
       //testMultiply();
      //  testSorting();
      testSubtraction();
        
    }
    public static void testSubtraction()
    {
        LargeInt test1 = new LargeInt(4444);
        LargeInt test2 = new LargeInt(3333);
        System.out.println(test1.subtract(test2).toString());
        LargeInt test3 = new LargeInt(5555);
        LargeInt test4 = new LargeInt(5555);
        System.out.println(test3.subtract(test4).toString());
        LargeInt test5 = new LargeInt(5555);
        LargeInt test6 = new LargeInt("555");
        System.out.println(test5.subtract(test6).toString());
        LargeInt test7 = new LargeInt(5);
        LargeInt test8 = new LargeInt("783278237832487234872347882346823487");
        System.out.println(test7.subtract(test8).toString());
    }

    public static void testConstructors()
    {
        ArrayList<Integer> testList = new ArrayList<>();
        testList.add(0);
        testList.add(5);
        testList.add(1);
        ArrayList<Integer> zeroList = new ArrayList<>();
        zeroList.add(0);
        LargeInt test1 = new LargeInt();
        LargeInt test2 = new LargeInt("000000000000000003434");
        LargeInt test3 = new LargeInt(26);
        LargeInt test4 = new LargeInt(testList);
        LargeInt test5 = new LargeInt("0");
        LargeInt test6 = new LargeInt(zeroList);
        System.out.println(test1.toString());
        System.out.println("");
        System.out.println(test2.toString());
        System.out.println("");
        System.out.println(test3.toString());
        System.out.println("");
        System.out.println(test4.toString());
        System.out.println("");
        System.out.println(test5.toString());
        System.out.println("");
        System.out.println(test6.toString());
        

    }
    public static void testAdd()
    {
        LargeInt test1 = new LargeInt(4444);
        LargeInt test2 = new LargeInt(3333);
        System.out.println(test1.add(test2).toString());
        LargeInt test3 = new LargeInt(5555);
        LargeInt test4 = new LargeInt(5555);
        System.out.println(test3.add(test4).toString());
        LargeInt test5 = new LargeInt("777");
        LargeInt test6 = new LargeInt(223);
        System.out.println(test5.add(test6).toString());

        LargeInt testzero1 = new LargeInt("0");
        LargeInt testzero2 = new LargeInt(2222);
        System.out.println(testzero1.add(testzero2).toString());
    }

    public static void testEquals()
    {
        LargeInt test1 = new LargeInt(44111424); // 4444, 4424
        LargeInt test2 = new LargeInt(44111424);
        System.out.println(test1.equals(test2));
        LargeInt test3 = new LargeInt(1);
        LargeInt test4 = new LargeInt(1);
        System.out.println(test3.equals(test4));
        LargeInt test5 = new LargeInt("0");
        LargeInt test6 = new LargeInt(0);
        System.out.println(test5.equals(test6));
    }

    public static void testMultiply()
    {
        LargeInt test1 = new LargeInt(52);
        LargeInt test2 = new LargeInt("0123");
        System.out.println(test1.multiply(test2).toString());
        LargeInt test3 = new LargeInt("0000000000");
        LargeInt test4 = new LargeInt(12);
        System.out.println(test3.multiply(test4).toString());

    }
    public static void testCompareTo()
    {
        //testSorting();
        LargeInt test1 = new LargeInt("0000000000000000000000000000000000000000000000000000000000000000000000000035321235523");
        LargeInt test2 = new LargeInt("132732872357887217861278748758127728154785214758124785124");
        System.out.println(test1.compareTo(test2));
        LargeInt test3 = new LargeInt("477373887");
        LargeInt test4 = new LargeInt("21");
        System.out.println(test4.compareTo(test3));
        LargeInt test5 = new LargeInt("22");
        LargeInt test6 = new LargeInt("22");
        System.out.println(test5.compareTo(test6));
 
    }
    
    public static void testSorting() {
        ArrayList<LargeInt> randomNumbers = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            randomNumbers.add(new LargeInt((int)(Math.random() * 100000)));
        }
        randomNumbers.sort(null);  // null means use the built-in compareTo() method
        System.out.println(randomNumbers);
    } 

}
