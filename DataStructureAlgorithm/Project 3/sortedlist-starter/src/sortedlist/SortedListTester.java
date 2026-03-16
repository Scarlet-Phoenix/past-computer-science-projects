package sortedlist;

public class SortedListTester {
    public static void main(String[] args) {
        // testAddToEnd();
        // testAddToBeginning();
        // testAddToMiddle();
        // testGet();
        // testEquals();
       testremove();
        // More tests here for the other functions.
        // Be sure to test get/contains/remove for elements
        // at the head, tail, and in the middle!
    }

    private static void testremove() {
        SortedList<Integer> mylist = new SortedList<Integer>();
        // mylist.setClobberDebug(true);
        System.out.println(mylist.toInternalString());
        mylist.add(40);
        System.out.println(mylist.toInternalString());
        mylist.add(30);
        System.out.println(mylist.toInternalString());
        mylist.add(20);
        System.out.println(mylist.toInternalString());
        mylist.add(10);
        System.out.println(mylist.toInternalString());

        mylist.remove(20);
        System.out.println(mylist.toInternalString());
        mylist.remove(10);
        System.out.println(mylist.toInternalString());
        mylist.remove(40);
        System.out.println(mylist.toInternalString());
        mylist.add(50);
        mylist.add(70);
        System.out.println(mylist.toInternalString());
        mylist.remove(30);
        System.out.println(mylist.toInternalString());
        mylist.remove(70);
        System.out.println(mylist.toInternalString());
        mylist.add(40);
        mylist.add(50);
        mylist.add(60);
        System.out.println(mylist.toInternalString());
                mylist.remove(50);
        System.out.println(mylist.toInternalString());

        mylist.clear();
        System.out.println(mylist.toInternalString());
    }

    private static void testEquals() {
        SortedList<Integer> mylist = new SortedList<Integer>();
        mylist.setClobberDebug(true);
        // System.out.println(mylist.toInternalString());
        mylist.add(20);
        // System.out.println(mylist.toInternalString());
        mylist.add(10);
        // System.out.println(mylist.toInternalString());
        mylist.add(40);
        // System.out.println(mylist.toInternalString());
        mylist.add(30);
        mylist.add(50);
        System.out.println(mylist.toInternalString());
        SortedList<Integer> mylist2 = new SortedList<Integer>();
        mylist2.add(20);
        mylist2.add(10);
        mylist2.add(50);
        mylist2.add(40);
        mylist2.add(30);
        System.out.println(mylist2.toInternalString());

        System.out.println(mylist.compareTo(mylist2));

    }

    private static void testGet() {

        SortedList<Integer> mylist = new SortedList<Integer>();
        mylist.setClobberDebug(true);
        System.out.println(mylist.toInternalString());
        mylist.add(20);
        System.out.println(mylist.toInternalString());
        mylist.add(10);
        System.out.println(mylist.toInternalString());
        mylist.add(40);
        System.out.println(mylist.toInternalString());
        mylist.add(30);
        mylist.add(50);
        System.out.println(mylist.toInternalString());

        Object getone = mylist.get(4);
        Object getwo = mylist.get(1);
        System.out.println(getone);
        System.out.println(getwo);

    }

    private static void testAddToMiddle() {
        SortedList<Integer> mylist = new SortedList<Integer>();
        mylist.setClobberDebug(true);
        System.out.println(mylist.toInternalString());
        mylist.add(20);
        System.out.println(mylist.toInternalString());
        mylist.add(10);
        System.out.println(mylist.toInternalString());
        mylist.add(40);
        System.out.println(mylist.toInternalString());
        mylist.add(30);
        mylist.add(25);
        mylist.add(35);
        mylist.add(50);
        System.out.println(mylist.toInternalString());
    }

    private static void testAddToBeginning() {
        SortedList<Integer> mylist = new SortedList<Integer>();
        mylist.setClobberDebug(true);
        System.out.println(mylist.toInternalString());
        mylist.add(40);
        System.out.println(mylist.toInternalString());
        mylist.add(30);
        System.out.println(mylist.toInternalString());
        mylist.add(20);
        System.out.println(mylist.toInternalString());
        mylist.add(10);
        System.out.println(mylist.toInternalString());
    }

    private static void testAddToEnd() {
        // Test adding items to end of list.
        SortedList<Integer> mylist = new SortedList<Integer>();
        mylist.setClobberDebug(true);
        System.out.println(mylist.toInternalString());
        mylist.add(10);
        System.out.println(mylist.toInternalString());
        mylist.add(20);
        System.out.println(mylist.toInternalString());
        mylist.add(30);
        System.out.println(mylist.toInternalString());
        mylist.add(40);
        System.out.println(mylist.toInternalString());
    }

}
