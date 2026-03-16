import java.util.Arrays; 
public class RotatingArrayList<E> {

    private E[] data;  // this is where we store the elements of the array
    private int size;    // this is the size of the array from the user's perspective
    private int offset;  // index of the "zero'th" element of the array, used for rotating
    // should always stay between 0 and size-1
    private int head; // the replacement for what I was originally trying to do with the size variable. use 
    // it as a flag for what is and isnt a valid index 
    private boolean noclobber; 

 //   private Class<E> typeChecker; 

    /**
     * Creates a new, empty list.
     */
    @SuppressWarnings("unchecked")
    public RotatingArrayList() {
        // We need to reserve some capacity in our data array for new elements to be added.
        // default constructor
        // We need to reserve some capacity in our data array for new elements to be added.
        data = (E[])(new Object[3]);  // reserves 3 spots in the data array for us to use
        size = 0;
        head = 0;
        offset = 0;
        noclobber = false; 

    }

    /** Returns a String representation of this list.  The returned string should
     * have all the elements of the list between square brackets, like this:
     * [ 10 20 30 40 ], or something similar.
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder( "["); 
        for (int i = 0; i < head ; i++)
        {
            if (i > 0) ret.append(" ");
            //System.out.println(normalizeIndex(i) + " " + i);
            ret.append(get(i));
        }
        return ret + " ]"; 

        /*
        int indexPointer = -1 + offset; 
        int counter = 0;
        
        while (true)
        {
            indexPointer++; 
          //  System.out.println("Starting loop Head: " + this.head);
            if (indexPointer > this.head || !this.checkExists(indexPointer)) // this is inelegant, but it works. 
            {
                indexPointer = -1; 
            }
            if(counter > this.head - 1){
                break;
            }
            if (!this.checkExists(indexPointer)){
                System.out.print("Null value found at " + this.normalizeIndex(indexPointer) + " " + indexPointer);
                System.out.println( " " + this.toStringDebug());
                counter++;
                indexPointer++;

                break; 
          
            }
           // System.out.println("Current index pointer " + indexPointer);
           // System.out.println(this.getExactPos(indexPointer).toString());
            ret += " " +  this.get(indexPointer).toString();
            

            counter++;
            
        } all of this was overcomplicating the thing, and well just didn't work. despite my hacks. 
        return ret + " ]";  // Fill in this method.

        */ 
    }


    /*
     * returns a string of the programmer side array for debugging purposes. 
     */
    public String toStringDebug() {
        String init = Arrays.toString(data); 
        String vals = "Offset: " + this.offset + ", Size: " + this.size + " Head: " + this.head; 
        return init + vals; 

    }

    /**
     * Returns the size of this list (from the user's perspective).
     */
    public int size() {
        return this.head; // Fill in this method.
    }

    //helper for the cardgame. 
    public E grabLast(){
        if (head == 0)
        {
            throw new IllegalArgumentException("List is empty");
        }
        return get(head - 1);
    }

    


    /**
     * Returns the element at index [pos] in the list.  You may assume pos
     * is a valid index in the list.
     */
    public E get(int pos) {
        //System.out.println(pos);
        
        return data[this.normalizeIndex(pos)];

    }

    /*
     * Removes the EXACT index. used to nullify values we don't need anymore. 
     */
    private void removeElement(int pos){
        this.setExactPos(pos, null);
    }   

    /*
     * checks value at user-defined position. returns false if the value is null. 
     * true otherwise.
     */
    public boolean checkExists(int pos)
    {
        if (this.get(pos) == null)
        {
            return false;
        }
        return true; 
    }


    /*
     * if setting at specified position would clobber an existing value in the list, an unmodifiablesetexception is thrown 
     *for debugging purposes. Outdated with the addition of the noclobber boolean 

     / public void setNoClobber(int pos, E value){
         if (pos < 0)
        {
            throw new ArrayIndexOutOfBoundsException(); 
        }
          if (pos >= this.size){
            throw new ArrayIndexOutOfBoundsException();  
        }
        int programPos = this.normalizeIndex(pos);
        if (this.data[programPos] != null) 
        {
            System.err.println("Index: " + pos + " value: " + data[pos].toString()  + " was clobbered.");
            throw new RuntimeException("Attempted to modify a existing value with a noClobber setter."); 
        }
        this.set(pos, value); 
    }


    /**
     * Sets the element at index [pos] in the list to the given value.  You may assume pos
     * is a valid index in the list.
     */
    public void set(int pos, E value){
//                System.out.println("Setting at pos" + pos);

        if (pos < 0)
        {
            throw new ArrayIndexOutOfBoundsException(); 
        }
       if (pos > this.head){
          throw new ArrayIndexOutOfBoundsException();  
      }
        //pos - this.offset <= this.size ? pos - this.offset : (pos + this.offset) - this.size;

        if (noclobber)
        {
            /*if (this.data[programPos] != null) 
            {
                System.err.println("Index: " + pos + " value: " + data[pos].toString()  + " was clobbered.");
                throw new RuntimeException("\nAttempted to modify a value while the noclobber flag was set to true. If this behavior is intentional, disable the flag with object.setNoClobberFlag(false)"); 
            }
                */ 
            if (this.checkExists(pos)) 
            {
                System.err.println("Index: " + this.normalizeIndex(pos) + " value: " + data[pos].toString()  + " was clobbered.");
                throw new RuntimeException("\nAttempted to modify a non-null value while the noclobber flag was set to true. If this behavior is intentional, disable the flag with object.setNoClobberFlag(false)"); 
            }else{ 
                // The check returns false if the value stored at position is null. In that case, we don't care if it's clobbered. 
                // the 
                this.setExactPos(normalizeIndex(pos), value);   
            }
        }else{
          this.setExactPos(normalizeIndex(pos), value);   

        }

    }
    /*
     * sets an index at an exact position to the specified value. the index is not normalized before setting.  
     */
    private void setExactPos(int pos, E value) {
        // Fill in this method.
        data[pos] = value; 
    }
    /*
     * grabs the data stored at the exact specified position on the programmer side.     
     */
    private E getExactPos(int pos)
    {
        return this.data[pos];
    }
    private int getIndexOfUserLast()
    { 
        return this.offset == 0 ?  this.head - 1 :  offset - 1;
    }
    private int getFirstValidDataIndex()
    {
        return this.head; 
    }

    /**
     * Rotates the elements in the list one spot to the right (from the user's
     * perspective).  Should not actually move any element's from the programmer's
     * perspective.
     */
    public void rotateRight() {
        if (this.offset + 1 == head){
            this.offset = 0;
        }else{
            offset++;
        }

        /*
        the below code goes against the spirit of the project.
        // Fill in this method.
        offset++; 
        E headVal = data[data.length - 1]; 
   //    E tailVal = data[0]; We don't actually need the tail, we just shift everything over by 1.  
        for (int i = data.length - 1; i > 0; i--)
        {
            data[i] = data[i - 1];
        }
        data[0] = headVal; 
        */ 
    }

    /**
     * Rotates the elements in the list one spot to the left (from the user's
     * perspective).  Should not actually move any element's from the programmer's
     * perspective.
     */
    public void rotateLeft() {
        // Fill in this method.

        if (this.offset == 0){
            this.offset = head -1; 
        }else{
            offset--; 
        } 
    }

    /**
     * Returns true if these two RotatingArrayLists are equal from the user's perspective.
     * Note that the underlying arrays may not be identical - you must take offset into account.
     * 
     * this is reqired to be O(N), so we can't duplicate arrays. 
     * 
     * Instead, if the indicies are normalized on the programmer end, we don't have to do 
     * any party tricks and can just do a simple check via array. 
     */
    public boolean equals(RotatingArrayList<E> o) {
 
        for (int i = 0; i < head; i++)
        {
            if (!(this.getExactPos(i).equals(o.get(i)))){
                return false;
            }
        }
    




        return true; // Fill in this method.
    }

    /**
     * Appends a new value to the end of this list from the user's perspective.
     * Because of the offset, the "end" of the list from the user's perspective may not
     * match the actual end of the array.
     */
    public void append(E value) {  // append should add an element to the "end" of the list
        // Fill in this method.

        if (this.head >= data.length)
        {
            expand(); 
        }
        
        this.setExactPos(getFirstValidDataIndex(), value);
        this.head++;


    }

    /**
     * Prepends a new value to the front of this list from the user's perspective.
     * Because of the offset, the "front" of the list from the user's perspective may not
     * match the actual front of the array.
     */

    // debug tip if the shift hack isnt working still.
     //Add this.head++; after this.set(offset, value); in the prepend method. Also, change this.set(offset, value); to this.set(0, value);.

    // I spent a lot of time on this method before realizing that techincally, it doesn't have to be an O(1) method like append is in my code. 
    public void prepend(E value) {  // adds an element to beginning of the list (always index 0)
        // Fill in this method.

         if (this.head >= data.length -1)
        {
            expand(); 
        }
        
      for (int i = data.length - 1; i > 0; i--) {
        data[i] = data[i - 1];
      }
        this.setExactPos(0, value);       
        this.offset = 0;
        this.head++;  
    }

    /**
     * Removes the first value in the list from the user's perspective.  Other values shift
     * to the left to fill in the "gap" at index 0.
     * Because of the offset, the value you are removing from the list may not actually
     * be at true index 0.
     */
    //this is the one to plan out on paper/
    /* a tricky spot is regarding the fact we don't know the type of the array until runtime. which means
    setting default values is harder. Java does have a builtin through the ArrayUtils...
    array = ArrayUtils.removeElement(array, element)
     due to the fact that it is a reference in a static context, and also based on the fact that the array needs to be 
     reassigned again, it's likely that the method destroys the element, and then returns a new array.
     since this method (and removelast) MUST be O(N), we can't do this. as we also need to shift all the bits to fill in
     the empty spot. 
     a solution

     or, or we can just clobber the value stored at the position and not have to worry about all that. 
     */ 
    public void removeFirst() {

        if (this.head == 0)
        {
            throw new NegativeArraySizeException();
        }
        trueShiftLeft(this.offset);

        if (this.offset != 0){
            this.offset--; 
        }
        removeElement(head - 1);
        this.head--; 
        // Fill in this method.
    }

    /**
     * Removes the last value in the list from the user's perspective.  Nothing shifts
     * from the user's perspective, though shifting might actually be required.
     * Because of the offset, the value you are removing from the list may not actually
     * be at true index size-1.
     */
    public void removeLast() {
        // Fill in this method.
        if (this.head == 0)
        {
            throw new NegativeArraySizeException();
        }
        trueShiftLeft(getIndexOfUserLast());
        removeElement(this.head - 1); 
        this.head--;
        //if (offset != 0) {
          //  offset--;
        //}
    }


   
/*
 * 
 * 
 * previous attemps at normalization. 
        int normalizedPos; 

        if (pos + offset >= head)
        {
            normalizedPos = (pos + offset) - head;   
        }else{
            normalizedPos = pos + offset; 
        }

        return data[normalizedPos]; 
 * 
 */



    //helper method for grabbing the index from the user's perspective 
    private int normalizeIndex(int unNormIndex){
        int normalizedPos; 
        if (head == 0) return 0; //we don't need to normalize a 0 index array. 
        normalizedPos = (unNormIndex + offset) % head; // required to wrap around the array when it exceeds the head
        if (normalizedPos < 0) normalizedPos += head; 
        return normalizedPos; 
        
        /*

        if (unNormIndex + offset >= head)
        {
            normalizedPos = (unNormIndex + offset) - head;   
        
        }else{
            normalizedPos = unNormIndex + offset; 
        }
       // System.out.println(normalizedPos);
        return normalizedPos; // sh**ty code that didn't work  
        */
    }

    /*
     * an abstraction. Shifts all the values of the programmer array to the left by one to the specified index. .
     * PREREQUISITE: NOCLOBBER SET TO FALSE.
     */
    private void trueShiftLeft(int index)
    {
        for (int i = index; i < this.head - 1; i++)
        {
            this.setExactPos(i, this.getExactPos(i + 1));
        }

    }
    //same, but it drags all values to the right by one instead of the other way around.
     private void trueShiftRight(int index)
    {
        for (int i = head; i > index; i--)
        {
            this.setExactPos(i + 1, this.getExactPos(i));
        }

    }


    public void setNoClobberFlag(boolean flag)
    {
        noclobber = flag; 
    }
    /**
     * Expands the data[] array by creating a new array of a bigger size than the existing one,
     * and copying everything from the old array into the new array.
     */

    @SuppressWarnings("unchecked")
    private void expand() {
        size += 3; 
        E[] newdata = (E[])(new Object[data.length + 3]);
        // copy everything from data -> newdata
        for (int i = 0; i < data.length; i++) {
            newdata[i] = data[i];
        }
        data = newdata;
    }
}
