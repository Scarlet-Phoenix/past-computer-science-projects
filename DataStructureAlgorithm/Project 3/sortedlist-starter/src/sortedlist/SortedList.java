/*
 * “I have neither given nor received unauthorized aid on this program.” : Charles Reed
 */


package sortedlist;

import java.util.NoSuchElementException;

public class SortedList<E extends Comparable<E>> {

    // private ArrayList<Node<E>> skiplist = new ArrayList<>();
    // OR
    // private Node<E> skipperHead, skipperPointer;
    /*
     * the idea of a skiplist is to allow an "express" lane for larger lists.
     * so if a sortedlist has, say, 64 indexes, instead of running through
     * everything for the contains() and add() methods,
     * we can create a seperate arraylist (or linkedlist) of simply-linked nodes who
     * just have a head pointer and
     * 
     */
    // Idea taken from
    // https://www.geeksforgeeks.org/dsa/skip-list/

    private Node<E> head; // points to the head of the list.
    private Node<E> tail; // points to the tail of the list.
    private int size; // number of nodes (items) in the list.
    private boolean noClobber = false; // debug variable, if an action would overwrite a variable in the list, the
                                       // program will throw an exception.

    /**
     * Create a new, empty SortedList.
     */
    public SortedList() {
        head = null; // first of the list
        tail = null; // last of the list.
        size = 0;
    }

    /**
     * Return true if this SortedList is empty, false otherwise.
     */
    public int size() {
        return size;
    }

    /**
     * Return the item at a specified index in this SortedList.
     * If index < the halfway point in the list (based on the size), the list should
     * be traversed
     * forwards from the head. If index > the halfway point, the traversal should
     * start at the tail
     * and proceed in reverse. For an index exactly halfway, you may start at either
     * end.
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        Node<E> curr;
        if (this.size() == 0) {
            System.err.println("Attempted to grab a value from a empty list.");
            throw new IllegalArgumentException();
        }
        if (index < 0) throw new IllegalArgumentException("Attempted negative array index access");
        if (index >= this.size()) throw new ArrayIndexOutOfBoundsException(); 

        // Your code here.
        // true - start from head.
        // false- start from tail.
        boolean startDirec = index < ((this.size() - 1) / 2);
        System.out.println(startDirec + " " + (int) ((this.size() - 1)/2));

        boolean checkSuccess = false;
        if (startDirec) {
            //System.out.println("Head");
            int i = 0; 
            
            // tail case
            curr = this.head;
           // for (int i = this.size - 1; i >= (int) ((this.size() - 1)   / 2); i--) {
           while (curr != null){           
              //  System.out.println(i);

                /*
                if (curr == null) {
                    System.err.println("Failure in the right side of the get() function. traversal " +
                            "currently at " + i + " and specified index at " + index);
                    // System.err.println("Current list: " + this.toInternalString());
                    throw new NullPointerException();
                }
                */
                if (i == index) {
                    checkSuccess = true;
                    break;
                }
                curr = curr.next;
                i++; 
            }

        } else {
           // System.out.println("tail");
            int i = this.size() - 1; 
            curr = this.tail;
           // for (int i = 0; i < (int) ((this.size() - 1) / 2); i++) {
           while (curr != null){
             //   System.out.println(i);
                /*
                if (curr == null) {
                    System.err.println("Failure in the left side of the get() function. traversal " +
                            "currently at " + i + " and specified index at " + index);
                    // System.err.println("Current list: " + this.toInternalString());
                    throw new NullPointerException();
                }
                */
                if (i == index) {
                    checkSuccess = true;
                    break;
                }
                curr = curr.prev;

                i--; 
            }
        }

        if (!checkSuccess) {
            System.err.println("Could not find element.");
            throw new NoSuchElementException();
        }

        return curr.data;
    }

    /**
     * Remove all the items in the SortedList.
     */
    public void clear() {
        // Your code here.
        if (noClobber) {
            System.out.println("""
                    Attempted to clear the list while the noClobber flag was set to true.
                    If this is intentional behavior, call obj.setClobberDebug(false);
                    Preventing for safety!""");
            return;
        }
        this.head = null; // If we nullify the tail and head pointers, then we can just let the garbage
                          // collecters
        // take charge of the rest of everything.
        this.tail = null;
        this.size = 0;

    }

    /**
     * Add a new item into this SortedList. Position will be determined
     * automatically based on sorted order.
     * An item < head or > tail should be added in O(1) time. Other items requiring
     * a traversal may be added
     * in O(n) time.
     * We assume this item does not already exist in the list.
     */
    // @SuppressWarnings("unchecked")
    public void add(E item) {
        // Your code here.

        Node<E> newnode = new Node<E>();
        newnode.data = item;
        if (this.size == 0) {
            this.size++;
            this.head = newnode;
            this.tail = newnode;
            return;
            // this.size++;
            // this.head = castItem;
            // his.tail = castItem;
        }
        if (item.compareTo(this.head.data) < 0) {
            newnode.next = this.head;
            this.head.prev = newnode;
            this.head = this.head.prev;
            this.size++;
            return;
        }
        if (item.compareTo(this.tail.data) > 0) {
            newnode.prev = this.tail;
            this.tail.next = newnode;
            this.tail = this.tail.next;
            this.size++;
            return;
        }
        Node<E> curr = head;
        // Node<E> holder = noClobber ? curr.next : null;
        // int i;
        // to check to see if we clobbered something, it's important to remember that
        // the item is being inserted at curr.next.
        // with that in mind, we can store the value originally stored at curr.next. and
        // hold it
        // and, later, once we've done the assignment,
        // if curr.next.next is no longer the same as the original curr.next, then
        // we hit the noclobber breakpoint.

        while (curr != null) {

            if (item.compareTo(curr.data) >= 0) {
                if (item.compareTo(curr.next.data) <= 0) {
                    size++;
                    /*
                     * 1:
                     * X- NEWNODE -X
                     * <- CURR -> <- CURR.NEXT -> <-CURR.NEXTNEXT...
                     * 2:
                     * X- NEWNODE -\
                     * <- CURR -> <- CURR.NEXT -> ...
                     * 3:
                     * /- NEWNODE -\
                     * <- CURR -> <- CURR.NEXT -> ...
                     * 4:
                     * /- NEWNODE (CURR.NEXT.PREV) -\
                     * <- CURR -> ^- CURR.NEXT -> ...
                     * 5:
                     * /- NEWNODE (CURR.NEXT && CURR.NEXT.NEXT.PREV) -\
                     * <- CURR -^ ^- CURR.NEXT.NEXT ->
                     */
                    newnode.next = curr.next;
                    newnode.prev = curr;
                    curr.next.prev = newnode;
                    curr.next = newnode;
                    break;
                }
            }
            curr = curr.next;
        }
        /*
         * if (noClobber)
         * {
         * if (holder == null){
         * return; // if the value is null, then it passed the ternary check.
         * //placing this here for explicit null safety.
         * }
         * 
         * if (curr.next.next != holder)
         * {
         * System.err.println("The value " + holder.data + " was clobbered at " + i +
         * " during the add() function. current array is as follows " +
         * this.toInternalString());
         * throw new RuntimeException("Clobbered Data");
         * }
         * never activated during debugging. nice.
         * 
         * 
         * }
         */

    }

    /**
     * Returns true if this SortedList contains item, false otherwise.
     * 
     */
    @SuppressWarnings("unchecked")
    public boolean contains(E item) {
        // Your code here.
        Node<E> curr = this.head;

        while (curr != null) {
            if (curr.data == item) {
                return true;
            }
            curr = curr.next;
        }

        return false;
    }

    /**
     * Remove an item from this SortedList. If the item occurs multiple times,
     * only one copy will be removed.
     */
    @SuppressWarnings("unchecked")
    public void remove(E item) {
        // Your code here.
        if (size == 0) {
            System.out.println("Could not remove item. List is empty.");
            return;
        }
        if (noClobber) {
            System.out.println("""
                    Attempted to remove an item while the noClobber flag was set to true.
                    If this is intentional behavior, call obj.setClobberDebug(false);
                    Preventing for safety!""");
            return;
        }

        if (item == this.head.data){
            head = head.next; 
            head.prev = null; 
            size--;
            return;  
        }
        if (item == this.tail.data){
            tail = tail.prev; 
            tail.next = null; 
            
            
            size--;
            return;  
        }
        //boolean deleteCheck = false;

        Node<E> curr = this.head;
        //Node<E> back = this.tail;

        while(curr  != null )
        {
            if (curr.data == item)
            {
                curr.prev.next = curr.next;
                curr.next.prev = curr.prev; 
                this.size--; 
                return; 
            }
            curr = curr.next; 
        }

        System.err.println("Could not find item in list.");
        throw new NoSuchElementException();

    }

    /**
     * This function should return an "internal" representation of the string, which
     * consists of the
     * list printed both head-to-tail and tail-to-head, and the size, calculated in
     * both directions.
     * Example: For a list with the numbers 1, 2, 3, this should return "[1 2 3]
     * size=3 [3 2 1] size=3"
     * Do this with a forwards traversal, counting the elements, followed by a
     * backwards traversal, re-counting
     * the elements. This is useful to detect incorrectly-pointing prev/next
     * pointers.
     *
     * Hint: Check to make sure the forward-size and the backward-size are the same
     * and both match the
     * size variable. If there is a mismatch, print an error message because this is
     * very helpful to
     * detect errors.
     *
     * (If you want, when the list is empty, you may just return "[] size=0" instead
     * of doing the traversals.)
     */
    public String toInternalString() {
        // Your code here.

        if (size == 0) {
            return "[] size=0";
        }
        int counter = 0;
        Node<E> front = this.head;
        Node<E> back = this.tail;
        String headret = "[ ";
        String tailret = "[ ";

        while (front != null) {
            headret += front.data + " ";
            front = front.next;
        }
        headret += "]";
        while (back != null) {
            tailret += back.data + " ";
            back = back.prev;
        }

        tailret += "]";

        return headret + " " + tailret + " " + "size=" + this.size();
    }

    /**
     * Return a string representation of this list from the user's perspective.
     * Should look like [item1 item2 item3...]
     */
    public String toString() {
        // Your code here.
        // Node<E> front = this.head;
        Node<E> back = this.tail;
        String ret = "[ ";
        for (int i = 0; i < this.size(); i++) {
            if (back == null) {
                break;
            }
            ret += back.data;
            ret += " ";
            back = back.next;
        }
        ret += "]";
        return ret; // remove this line
    }

    @SuppressWarnings("unchecked")
    public int compareTo(SortedList other) {
        Node<E> thistail = this.tail;
        Node<E> othertail = other.tail;
        if (this.size() > other.size()) {
            return 1;
        } else if (this.size() < other.size()) {
            return -1;
        }
        while (thistail != null) {
            if (thistail.data.compareTo(othertail.data) > 0) {
                return 1;
            } else if (thistail.data.compareTo(othertail.data) < 0) {
                return -1;
            }
            thistail = thistail.prev;
            othertail = othertail.prev;
        }

        return 0;
    }

    public void setClobberDebug(boolean b) {
        this.noClobber = b;
    }

    /**
     * It is very common to have private classes nested inside other classes. This
     * is most commonly used when
     * the nested class has no meaning apart from being a helper class or utility
     * class for the outside class.
     * In this case, this Node class has no meaning outside of this SortedList
     * class, so we nest it inside here
     * so as to not prevent another class from declaring a Node class as well.
     *
     * Note that even though the members of node are public, because the class
     * itself is private
     */
    private static class Node<E> {
        public E data = null;
        public Node<E> next = null; // you may initialize member variables of a class when they are defined;
        public Node<E> prev = null; // this behaves as if they were initialized in a constructor.
    }
}
