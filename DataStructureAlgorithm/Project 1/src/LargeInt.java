import java.util.ArrayList;
import java.util.Arrays;

public class LargeInt implements Comparable<LargeInt> {

    // before anything else, largeints still have a maximum amounts of digits. which is the same as Java's native integer limit.
    // you cannot use longs when referencing array indexes. this is an intended feature of the language. 

    private ArrayList<Integer> digits = new ArrayList<>();

    public LargeInt() {
        digits.add(Integer.valueOf(0));
    }
    
    private LargeInt(ArrayList<Integer> inpDigits, boolean stripBeginning) {
        //the stripping happens automatically when the public constructor is called.
        //this is mostly here to avoid an issue with the add() method where all the significant zeroes would
        //get stripped away. 
        String[] intstring;
        StringBuilder fullInt = new StringBuilder(inpDigits.size());
        for (int i = 0; i < inpDigits.size(); i++) {
            fullInt.append(inpDigits.get(i));
        }
        if (stripBeginning) {
            String strippedArrList = stripLeadingZeroes(fullInt.toString());
            intstring = strippedArrList.split("");
        } else {
            intstring = fullInt.toString().split("");
        }
        /*
         * for (int n = intstring.length - 1; n >= 0; n--)
         * {
         * this.digits.add(Integer.valueOf(intstring[n]));
         * }
         */
        for (int n = 0; n < intstring.length; n++) {
            this.digits.add(Integer.valueOf(intstring[n])); // the string is already in correct order for
            //the use cases for the private method,  so no need to do this.

        }

    }

    public LargeInt(ArrayList<Integer> inpDigits) {
        StringBuilder fullInt = new StringBuilder(inpDigits.size());
        for (int i = 0; i < inpDigits.size(); i++) {
            fullInt.append(inpDigits.get(i));
        }
        String strippedArrList = stripLeadingZeroes(fullInt.toString());
        String[] intstring = strippedArrList.split("");

        for (int n = intstring.length - 1; n >= 0; n--) {
            this.digits.add(Integer.valueOf(intstring[n]));
        }

    }

    private LargeInt(String str, boolean stripString) {
        if (stripString) {
            str = stripLeadingZeroes(str);
        }
        String[] intstring = str.split("");
        for (int n = intstring.length - 1; n >= 0; n--) {
            digits.add(Integer.valueOf(intstring[n]));
        }
    }

    public LargeInt(String str) {
        str = stripLeadingZeroes(str);
        String[] intstring = str.split("");
        /* 
        System.out.println(Arrays.toString(intstring));
        if (intstring.length == 0)
        {
            digits.add(0);
            return; 
        }
        */
        for (int n = intstring.length - 1; n >= 0; n--) {
            digits.add(Integer.valueOf(intstring[n]));
        }
    }

    public LargeInt(int i) {
        String[] intstring = Integer.toString(i).split("");
        for (int n = intstring.length - 1; n >= 0; n--) {
            digits.add(Integer.valueOf(intstring[n]));
        }

    }

    private String stripLeadingZeroes(String str) {
        String star = str.replaceFirst("^0+", ""); 
        if (star.equals("")){
            star = "0"; // prevents numberformatexceptions that occur with LargeInts that equate in value to "0". 
        }
        return star;
    }

    public int numDigits() {
        int counter = 0;

        // boolean isSignificantZero = true; //

        for (Integer i : digits) {
            counter++;
        }
        return counter;
    }

    public boolean equals(LargeInt other) {
       return this.toString().equals(other.toString());
    }

    public ArrayList<Integer> getDigitsArrayList() {
        return this.digits;
    }

    public int getDigitAt(int i) {
        return this.digits.get(i);
    }

    public Integer getIntegerAt(int i) {
        return this.digits.get(i);
    }


    /*
     * PSEUDOCODE START:
     * 
     * public LargeInt add() (LargeInt other)
     * 
     * CREATE ArrayList<INTEGER> "totalsum" AS AN ArrayList<INTEGER>:
     * CREATE boolean equalSize ASSIGN this.digits(size) == other.numDigits()
     * CREATE long maxiterations ASSIGN this.digits.size() >= other.numDigits() ?
     * other.numDigits() : this.digits.size()
     * 
     * CREATE int sum
     * CREATE int carry
     * 
     * 
     * FOR int = 0 UNTIL i < maxiterations AND INCREMENTING i BY 1:
     * sum ASSIGN digits.get(i) + other.getDigitAt(i) + carry
     * carry ASSIGN (int)Math.floor(digits / 10);
     * sum ASSIGN sum MODULUS 10
     * totalsum.add(sum)
     * END
     * 
     * IF equalsize THEN
     * RETURN LargeInt(totalsum)
     * END
     * 
     * IF this.digits(size) > other.numDigits() THEN
     * FOR int i = maxIterations UNTIL i < other.numDigits() AND INCREMENTING i by
     * !:
     * sum ASSIGN other.getDigitAt(i) + carry
     * carry ASSIGN (int)Math.floor(digits / 10);
     * sum ASSIGN sum MODULUS 10
     * totalsum.add(sum)
     * END
     * 
     * ELSE:
     * FOR int i = maxIterations UNTIL i < digits.size() AND INCREMENTING i by !:
     * sum ASSIGN digits.get(i) + carry
     * carry ASSIGN (int)Math.floor(digits / 10);
     * sum ASSIGN sum MODULUS 10
     * totalsum.add(sum)
     * END
     * END
     * RETURN LargeInt(totalsum)
     */
    public LargeInt add(LargeInt other) {
        ArrayList<Integer> totalsum = new ArrayList<Integer>();
        boolean equalSize = digits.size() == other.numDigits();
        int maxiterations = this.digits.size() >= other.numDigits() ? other.numDigits() : this.digits.size();

        int sum;
        int carry = 0;

        for (int i = 0; i < maxiterations; i++) {
            sum = digits.get(i) + other.getDigitAt(i) + carry;
            carry = (int) Math.floor(sum / 10);
            sum = sum % 10;
            totalsum.add(sum);
        }
        if (equalSize) {
            if (carry == 1) {
                totalsum.add(carry);
                return new LargeInt(totalsum, false);
            } else {
                return new LargeInt(totalsum, false);
            }
        }
        if (digits.size() > other.numDigits()) {
            for (int i = maxiterations; i < digits.size(); i++) {
                sum = digits.get(i) + carry;
                carry = (int) Math.floor(sum / 10);
                sum = sum % 10;
                totalsum.add(sum);
            }
            if (carry == 1) {
                totalsum.add(carry);
                return new LargeInt(totalsum, false);
            } else {
                return new LargeInt(totalsum, false);
            }

        } else {
            for (int i = maxiterations; i < other.numDigits(); i++) {
                sum = other.getDigitAt(i) + carry;
                carry = (int) Math.floor(sum / 10);
                sum = sum % 10;
                totalsum.add(sum);
            }
            if (carry == 1) {
                totalsum.add(carry);
                return new LargeInt(totalsum, false);
            } else {
                return new LargeInt(totalsum, false);
            }
        }
    }

    public LargeInt multiply(LargeInt other) {
        LargeInt product = new LargeInt(); 
        LargeInt counter = new LargeInt(); 
        /*
         * if this check is not performed, then it will repeat the addition operation an infinite number of times.
         * if any input is zero, the result will always be zero due to the zero property of multiplication. 
         */
        boolean checkThisAllZeroes = true;
        boolean checkOtherAllZeroes = true;
        for (Integer i : digits)
        {
            if (!(i.equals(0)))
            {
                checkThisAllZeroes = false; 
            }
        }
        for (Integer i : other.getDigitsArrayList())
        {
            if (!(i.equals(0)))
            {
                checkOtherAllZeroes = false;
            }
        }

        if (checkOtherAllZeroes || checkThisAllZeroes)
        {
            return new LargeInt(0);
        }

        if (digits.size() > other.numDigits()) {
            while (true) {
                counter = counter.add(new LargeInt(1));
                product = product.add(new LargeInt(digits, false));
                if (counter.equals(other)) {
                    break;
                }
            }
        } else {
            while (true) {
                counter = counter.add(new LargeInt(1));
                product = product.add(other);
                if (counter.equals(new LargeInt(digits, false))) {
                    break;
                }
            }
        }
        return product;
    }


    
    /*
     * PUBLIC LargeInt subtraction (L)
     * CREATE ArrayList<INTEGER> result
     * CREATE ArrayList<INTEGER> digitstemp = this.digits; 
     * CREATE ArrayList<INTEGER> othertemp = other.getArrayList() 
     * CREATE INTEGER dig1 
     * CREATE INTEGER dig2
     * CREATE INTEGER borrow ASSIGN 0
     * CREATE INTEGER maxiterations ASSIGN this.digits.size() >= other.numDigits() ? this.digits.size() : -1;
     * IF maxiterations == -1 THEN
     *  THROW NEW EXCEPTION ILLEGALARGUMENTEXCEPTION("negative numbers are not supported.")
     * IF (this.compareTo(other) == 0) THEN
     *  return NEW LARGEINT("0")  
     * end
     *  WHILE otherstemp.size() < maxiterations DO
     *   otherstemp.add(0) 
     *  end
     * 
     * FOR i = 0 , i < maxiterations, AND INCREMENTING i BY 1 DO
         dig1 ASSIGN digits.get(i)
        dig2 ASSIGN  othertemp.get(i)

        dig1 ASSIGN dig1 - borrow
        IF dig1 < dig2 THEN
          dig1 ASSIGN dig1 + 10
          borrow ASSIGN 1
        else
          borrow ASSIGN 0
          END

        APPEND dig1 - dig2 TO RESULT
        END
     * RETURN NEW LARGEINT (result, true) -- turns out i did acutally need to strip it.
     */


    public LargeInt subtract(LargeInt other)
    {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> othertemp = other.getDigitsArrayList();
        int dig1;
        int dig2; 
        int borrow = 0;
        int maxIterations = this.digits.size() >= other.numDigits() ? this.digits.size() : -1;
        int positiveNumCheck = this.compareTo(other);
        if (positiveNumCheck == -1){
            throw new ArithmeticException("LargeInt objects do not support negative numbers.\n The subtrahend cannot be longer than the miunuend");
        }else if (positiveNumCheck == 0){
            return new LargeInt("0");
        }
        if (other.equals(new LargeInt(0)))
        {
            return this; 
        }
        while (othertemp.size() < maxIterations)
        {
            othertemp.add(0); 
        }
        for (int i = 0; i < maxIterations; i++)
        {
            dig1 = digits.get(i);
            dig2 = othertemp.get(i);
            
            dig1 = dig1 - borrow; 
            if (dig1 < dig2)
            {
                dig1 += 10; 
                borrow = 1; 
            }else{
                borrow = 0; 
            }
            result.add(dig1 - dig2); 
        }
        return new LargeInt(result, false);
    }
    /*
     * returns: LargeInt quotient. 
     * Discards the remainder. 
     
    public LargeInt divide(LargeInt other)
    {
        LargeInt quotient = new LargeInt();
        LargeInt counter = new LargeInt(); 

        if (other.equals(new LargeInt(0)))
        {
            throw new ArithmeticException("Cannot divide by zero.");
        }else if (this.equals(new LargeInt(0)))
        {
            return new LargeInt(0); 
        }
        if (this.compareTo(other) == -1)
        {
            throw new IllegalArgumentException("the divisor cannot be larger than the dividend");
        }else if (this.compareTo(other) == 0)
        {
            return new LargeInt(1);
        }

        int  = 0; 

    }
    */
    @Override
    public int compareTo(LargeInt other) {
        if (this.toString().equals(other.toString()))
        {
            return 0;
        }
        //first, the size check. this and the other should have been stripped in the constructor, so there should be 
        // no issues from leading zeroes. 
        // this check also helps to avoid array index exceptions. 
        if (digits.size() > other.numDigits())
        {
            return 1; 
        }else if (digits.size() < other.numDigits() ){
            return -1; 
        }

        /*
         *PSUDEOCODE BEGIN 
         * 
         * FOR int i = digits.size(); i >= 0  AND INCREMENTING i by -1
         *  IF digits.get(i) > other.getDigitAt(i) THEN
         *   return 1 
         *  ELSE IF digits.get(i) < other.getDigitAt(i) THEN
         *   return -1
         *  end 
         * end 
         */
        
        for (int i = digits.size() - 1; i >= 0; i--)
        {
            if (digits.get(i) > other.getDigitAt(i))
            {
                return 1; 
            }
            else if (digits.get(i) < other.getDigitAt(i))
            {
                return -1;
            }
        }


        return 0;



    }

    public String toString() {
        StringBuilder fullInt = new StringBuilder(digits.size());
        for (int i = digits.size() - 1; i >= 0; i--) {
            fullInt.append(digits.get(i));
        }
        return fullInt.toString();

    }
}
