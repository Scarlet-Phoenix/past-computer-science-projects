public class RotatingArrayListTester {
    public static void main(String[] args) {

        // Write your tests here. You will probably want to
        // write separate test functions and call them here.
        // testInit();
        // testRotate();
        //testPop();
        testEquals();

    }
    public static void testEquals()
    {
         RotatingArrayList<Integer> arrLi = new RotatingArrayList<Integer>();
        arrLi.append(1);

        arrLi.append(2);

        arrLi.append(3);

        arrLi.append(4);
        arrLi.append(5);
        arrLi.append(6);
        RotatingArrayList<Integer> arrLi2 = new RotatingArrayList<Integer>();
        arrLi2.append(1);
        arrLi2.append(2);
        arrLi2.append(3);
        arrLi2.append(4);
        arrLi2.append(5);
        arrLi2.append(6);
        System.out.println(arrLi.equals(arrLi2));
        arrLi.prepend(7);
        arrLi2.set(0, 7);
        arrLi.append(1);
        
        System.out.println(arrLi.toString());
        System.out.println(arrLi2.toString());
        System.out.println(arrLi.equals(arrLi2));
        arrLi2.append(1);
System.out.println(arrLi2.toString());
        System.out.println(arrLi.equals(arrLi2));
        
    }

    public static void testPop() {
        RotatingArrayList<Integer> arrLi = new RotatingArrayList<Integer>();
        arrLi.append(1);

        arrLi.append(2);

        arrLi.append(3);

        arrLi.append(4);
        arrLi.append(5);
        arrLi.prepend(6);
        System.out.println(arrLi.toString());
        arrLi.removeFirst();
        System.out.println(arrLi.toString());
       // System.out.println(arrLi.toStringDebug());

     //  arrLi.rotateLeft();
        //System.out.println(arrLi.toString());
        // System.out.println(arrLi.toStringDebug());

        arrLi.removeLast();
        System.out.println(arrLi.toString());

       arrLi.rotateRight();
         //       System.out.println(arrLi.toStringDebug());

        System.out.println(arrLi.toString());
       arrLi.removeLast();
        //System.out.println(arrLi.toStringDebug());
        System.out.println(arrLi.toString());

        /*
         * System.out.println(arrLi.toString());
         * arrLi.removeFirst();
         * arrLi.rotateRight();
         * System.out.println(arrLi.toString());
         * arrLi.removeFirst();
         * 
         * arrLi.rotateLeft();
         * 
         * arrLi.rotateLeft();
         * 
         * System.out.println(arrLi.toString());
         */

    }

    public static void testRotate() {
        RotatingArrayList<Integer> arrLi = new RotatingArrayList<Integer>();
        arrLi.setNoClobberFlag(true);
        arrLi.append(1);

        arrLi.append(2);

        arrLi.append(3);

        arrLi.append(4);
        arrLi.append(5);
        System.out.println(arrLi.toString());
        System.out.println(arrLi.toString());

        arrLi.rotateLeft();
        System.out.println(arrLi.toStringDebug());

        System.out.println(arrLi.toString());

        arrLi.rotateRight();
        System.out.println(arrLi.toString());
        System.out.println(arrLi.toStringDebug());

        arrLi.rotateRight();
        System.out.println(arrLi.toString());

        arrLi.rotateRight();
        arrLi.rotateRight();
        arrLi.rotateRight();
        arrLi.rotateRight();
        System.out.println(arrLi.toString());

    }

    public static void testInit() {
        RotatingArrayList<Integer> arrLi = new RotatingArrayList<Integer>();
        arrLi.setNoClobberFlag(false);

        System.out.println(arrLi.toStringDebug());
        arrLi.append(1);
        System.out.println(arrLi.toStringDebug());

        System.out.println(arrLi.toString());

        System.out.println(arrLi.toStringDebug());
        arrLi.append(2);
        System.out.println(arrLi.toStringDebug());

        arrLi.append(3);

        arrLi.prepend(6);
        System.out.println(arrLi.toStringDebug());
        arrLi.append(4);

        System.out.println(arrLi.toStringDebug());
        System.out.println(arrLi.toString());

    }
}
