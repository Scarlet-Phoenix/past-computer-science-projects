import java.util.ArrayList;
import java.util.List;

/**
 * A map implemented with a binary search tree.
 */
public class BSTMap<K extends Comparable<K>, V> {

    private Node<K, V> root;    // points to the root of the BST.
    boolean debugPrintDetails = false; 

    /**
     * Create a new, empty BST.
     */
    public BSTMap() {
        root = null;
    }

    /**
     * Put (add a key-value pair) into this BST.  If the key already exists, the old
     * value will be overwritten with the new one.
     */
    public void put(K newKey, V newValue)
    {
        // Your code here.
        //for empty bst. 
        
        if (root == null)
        {
            //base case. If the tree is empty, create a tree.
            Node<K,V> newnode = new Node<K,V>(); 
            newnode.key = newKey;
            newnode.value = newValue;
            this.root = newnode; 
        }
        else
        {
            put(root, newKey, newValue);
        }


    }

    /**
     * Helper function for put.
     */
    private void put(Node<K, V> curr, K newKey, V newValue)
    {
        //case of key already exists. 
        if (curr.key.compareTo(newKey)== 0)
        {
            curr.value = newValue;
         
        }
        else if (newKey.compareTo(curr.key) < 0)
        {
            if (curr.left == null)
            {
                Node<K,V> newNode = new Node<K,V>();
                newNode.key = newKey;
                newNode.value = newValue; 
                curr.left = newNode;
   
            }
            else
            {
                put(curr.left, newKey, newValue);
            }
        }
        else
        {
            if (curr.right == null)
            {
                Node<K,V> newNode = new Node<K,V>();
                newNode.key = newKey;
                newNode.value = newValue; 
                curr.right = newNode;

            }
            else
            {
                put(curr.right, newKey, newValue);
            }
        }



    }

    /**
     * Get a value from this BST, based on its key.  If the key doesn't already exist in the BST,
     * this method returns null.
     */
  
    public V get(K searchKey)
    {
         // remove this.
        // Your code here.

        Node<K, V> curr = root;
        if (curr == null)
        {
            throw new IllegalArgumentException("Cannot retrieve key from empty Tree.");
        }else{
            Node<K,V> getva = get(curr, searchKey);
            if (getva == null){
                return null;
            }
            return getva.value; //getva; 
        }

        
        
   //     return null;
    }

    private Node<K,V> get(Node<K, V> curr, K searchKey)
    {
        if (curr == null)
        {
            return null; // if key is not found.  
        }
        if (searchKey.compareTo(curr.key) < 0)
        {
            return get(curr.left, searchKey);
        }
        else if (searchKey.compareTo(curr.key) > 0)
        {
            return get(curr.right, searchKey);
        }
        else
        {   
            return curr;

        }
    }

    /**
     * Test if a key is present in this BST.  Returns true if the key is found, false if not.
     */
    public boolean containsKey(K searchKey)
    {
          // remove this.
        // Your code here.
        
        Node<K, V> curr = root;
        if (curr == null)
        {
            return false; 
        }else{

            return containsKey(curr, searchKey);
        }


    }
    private boolean containsKey(Node<K, V> curr, K searchKey)
    {
         if (curr == null)
        {
            return false; // if key is not found.  
        }
        if (searchKey.compareTo(curr.key) < 0)
        {
            return containsKey(curr.left, searchKey);
        }
        else if (searchKey.compareTo(curr.key) > 0)
        {
            return containsKey(curr.right, searchKey);
        }
        else
        {   
            return true;

        }
    }

    /**
     * Given a key, remove the corresponding key-value pair from this BST.  Returns true
     * for a successful deletion, or false if the key wasn't found in the tree.
     */
    public boolean remove(K removeKey)
    {
         // remove this.
        // Your code here.

        Node<K, V> curr = root;
        Node<K, V> parent = null; 

        while (curr != null && !(curr.key.compareTo(removeKey) == 0))
        {
            parent = curr; 
            if (removeKey.compareTo(curr.key) < 0) curr = curr.left;
            else curr = curr.right;  

        }
        if (curr == null || !(curr.key.compareTo(removeKey) == 0)) return false; 
        if (curr.left != null && curr.right != null)
        {
            Node<K,V> successor = curr.right;
            Node<K,V> successorParent = curr; 
            while(successor.left != null)
            {
                successorParent = successor; 
                successor = successor.left; 
            }
            curr.key = successor.key; 
            curr = successor;
            parent = successorParent; 

        }

        Node <K,V> subtree; 

        if (curr.left == null && curr.right == null ) subtree = null;
        else if (curr.left != null) subtree = curr.left; 
        else subtree  = curr.right; 

        if (parent == null) root = subtree;
        else if (parent.left == curr) parent.left = subtree;
        else parent.right = subtree; 
        


        return true; 
    }

    /**
     * Return the number of key-value pairs in this BST.
     */
    public int size()
    {

        /*Plan code
         * So. there are alraedy 3 traversal methods listed below. 
         * Since they already traverse through all the values, we can just call one of the traversal methods, and then just return
         * the size of the key array. This would make the problem a lot easier to solve through abstraction
         * however, one, the directions state that this function must be called recursively. 
         * and there may be some weird edge case where some of the keys don't have values, and vice versa. This 
         * could be avoided by strictly defining the tree so that 
         * ∀ n ∈ BST, (k,v) ∈ n 
         * (for all n nodes in the BST,  k,v  are a subset of n)
         *  ∀ n ∈ BST, k ∈ n is guarenteed. 
         * however, due to the fact that ∃ n ∈ BST | v ∉ n (there exist n nodes in the BST such that v is not a subset of n)
         *  because part B of the project mentions that
         * new words should be ignored, which means it will have either a null value or -1 to flag the word
         * we need to explicitly check that every key has a paired value. .  
         */

        Node<K,V> curr = root; 


        
        


        return size(curr);  // remove this.
        // Your code here.
    }

    private int size(Node<K,V> curr)
    {
        
        if (curr == null) return 0;

        int leftsum = size(curr.left);
        int rightsum = size(curr.right);

        //size(root.left), size(root.right); turns this from a o(n) function into an o(n!) function.
        //if it even halts at all. it could just loop infinitely. 
        //again. CURR, NOT ROOT. 
        return 1 + leftsum + rightsum;

        //below is pretty bad code. it returned the same value as the height() function.
       /* //curr.key is already implicitly checked for when adding a new node. 
       
        if (curr.value != null) check = 1; 


        if (curr.left != null){
            return size(curr.left) + check; 

        }
        else if (curr.right != null) {
            return size(curr.right) + check;
        }
        else {
            return check; 
        }
            */
        
    }

    /**
     * Return the height of this BST.
     */
    public int height()
    {

        return height(root);  // remove this.
        // Your code here.
    }
   
    private int height(Node<K, V> curr)
    {
         int leftsum = -1 ;
        int rightsum = -1;  //dummy values   

        if (root == null) return -1; 

        if (curr.left != null){
             leftsum = height(curr.left);
        }
        if (curr.right != null){
             rightsum = height(curr.right);
        } 

        //since the height is a maximum, we naturally have a max function.
        //the +1 is to include the current node. 
        return Math.max(leftsum, rightsum) + 1;
    }

    /**
     * Return a List of the keys in this BST, ordered by a preorder traversal.
     */
    public List<K> preorderKeys()
    {
        ArrayList<K> retList = new ArrayList<>(); 

        return preorderKeys(root, retList);  // remove this.
        // Your code here.
    }

    private List<K> preorderKeys(Node <K, V> curr, ArrayList<K> list)
    {
        if (curr == null) return list; 

        list.add(curr.key);
         if (debugPrintDetails)
        {
            System.out.println(curr.toString());
        }


        if (curr.left != null)
        {
            preorderKeys(curr.left, list);
        }
        if (curr.right != null)
        {
            preorderKeys(curr.right, list);
        }

        return list; 


        

    }

    /**
     * Return a List of the keys in this BST, ordered by a inorder traversal.
     */
    public List<K> inorderKeys()
    {


        ArrayList<K> retList = new ArrayList<>(); 

        return inorderKeys(root, retList); 
        // Your code here.
    }

    private List<K> inorderKeys(Node<K,V> curr, ArrayList<K> list)
    {
        if (curr == null) return list; 

        inorderKeys(curr.left, list);

        list.add(curr.key);
         if (debugPrintDetails)
        {
            System.out.println(curr.toString());
        }

        
        inorderKeys(curr.right, list);

        return list; 


    }

    /**
     * Return a List of the keys in this BST, ordered by a postorder traversal.
     */
    public List<K> postorderKeys()
    {
         ArrayList<K> retList = new ArrayList<>(); 

        return postorderKeys(root, retList); 
        // Your code here.
    }

    private List<K> postorderKeys(Node<K,V> curr, ArrayList<K> list) 
    {
        
        if (curr == null) return list; 


        inorderKeys(curr.left, list);
        
        inorderKeys(curr.right, list);

        list.add(curr.key);

        if (debugPrintDetails)
        {
            System.out.println(curr.toString());
        }

        return list; 
    }

    public void setPrintDetailsDebug(boolean val) {this.debugPrintDetails = val;}  

    public void printBST()
    {
        System.out.println(this.preorderKeys().toString());
        System.out.println(this.inorderKeys().toString());
        System.out.println(this.postorderKeys().toString());

    }

    /*
     * 
     * 
     * 
     * PRINTS OUT THE DETAILS OF A NODE. 
     
    private String nodeToString(Node<K,V> node)
    {
        String ret = ""; 

        ret += "key:" + node.key;
        if (node.value != null) ret += ",value:" + node.value;
        if (node.left != null) ret += ",leftkey:" + node.left.key;
        if (node.right != null ) ret += ",rightkey:" + node.right.key;  


        return ret; 
    }
        */

    





    /**
     * It is very common to have private classes nested inside other classes.  This is most commonly used when
     * the nested class has no meaning apart from being a helper class or utility class for the outside class.
     * In this case, this Node class has no meaning outside of this BSTMap class, so we nest it inside here
     * so as to not prevent another class from declaring a Node class as well.
     *
     * Note that even though the members of node are public, because the class itself is private
     */
    private static class Node<K extends Comparable<K>, V> {
        public K key = null;
        public V value = null;
        public Node<K, V> left = null;     // you may initialize member variables of a class when they are defined;
        public Node<K, V> right = null;    // this behaves as if they were initialized in a constructor.
        
        @Override // suppresses a compiler warning of the overriden object.toString() method. 
        public String toString() {
            String ret = ""; 

            ret += "key:" + key;
            if (value != null) ret += ",value:" + value; 
            if (left != null) ret += ",leftkey:" + left.key;
            else ret += ",leftkey:null"; 
            if (right != null ) ret += ",rightkey:" + right.key;  
            else ret += ",rightkey:null";
            return ret;
        }
    }

}
