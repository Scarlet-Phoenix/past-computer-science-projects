import java.io.InputStream;
import java.util.*; 

/*
 * 
 * I have neither given nor recieved unauthorized aid on this program.
 */
public class SentimentAnalysis {
    private static double movieSentiment = 0; 
    private static int reviewsProcessed = 0; 
    public static void main(String[] args)
    {
        final boolean PRINT_TREES = false;  // whether or not to print extra info about the maps.
        final boolean SKIP_USER_ENTRY = false; 
        BSTMap<String, Integer> wordFreqs = new BSTMap<String, Integer>();
        BSTMap<String, Integer> wordTotalScores = new BSTMap<String, Integer>();
        Set<String> stopwords = new TreeSet<String>();

        System.out.print("Enter filename: ");
        Scanner scan = new Scanner(System.in);
        String filename = scan.nextLine();

        processFile(filename, wordFreqs, wordTotalScores);

        System.out.println("Number of words is: " + wordFreqs.size());
        System.out.println("Height of the tree is: " + wordFreqs.height());

        if (PRINT_TREES)
        {
            System.out.println("Preorder:  " + wordFreqs.preorderKeys());
            System.out.println("Inorder:   " + wordFreqs.inorderKeys());
            System.out.println("Postorder: " + wordFreqs.postorderKeys());
            printFreqsAndScores(wordFreqs, wordTotalScores);
        }

        removeStopWords(wordFreqs, wordTotalScores, stopwords);

        System.out.println("After removing stopwords:");
        System.out.println("Number of words is: " + wordFreqs.size());
        System.out.println("Height of the tree is: " + wordFreqs.height());

        if (PRINT_TREES)
        {
            System.out.println("Preorder:  " + wordFreqs.preorderKeys());
            System.out.println("Inorder:   " + wordFreqs.inorderKeys());
            System.out.println("Postorder: " + wordFreqs.postorderKeys());
            printFreqsAndScores(wordFreqs, wordTotalScores);
        }

        while (true)
        {
            if (!SKIP_USER_ENTRY){


            
                System.out.print("\nEnter a new review to analyze: ");
                String line = scan.nextLine();
                if (line.equals("quit")) break;

                //Integer holder; 
                String[] words = line.split(" ");

                double aggregateWordScores = 0; 
                int totalWordsCovered = 0;
                for (String ent : words)
                {
                    if (!wordTotalScores.containsKey(ent))
                    {
                        System.out.println("Skipping " + ent + " (stopword)"); // we already removed the stopwords,
                        // and since the compliment of the wordlists are the stopword sets, we just need to check that it's not in. 

                    }else{
                        Integer rawScore = wordTotalScores.get(ent);
                        Integer rawFreq = wordFreqs.get(ent); 
                        double average = ((double) rawScore  / (double) rawFreq); 
                        aggregateWordScores += average;
                        totalWordsCovered++; 
                        //reviewsProcessed++; 
                        System.out.println("The average sentiment of " + ent + " is " + average);
                    }
                }
                System.out.println("The average sentiment of this review is  " + (aggregateWordScores / totalWordsCovered));

            }else{
                debugAnalysis(wordFreqs, wordTotalScores);

            }
            // this section was meant to test analysis without going through user input.
            //weirdly, the numbers produced by reviews-big failed to have any real meaning, as they often scored above 
            //4 
           
        }
        System.out.println("Analyse a little more? y/n");
        String line = scan.nextLine();
        if (line.equals("n")) System.exit(0);


        System.out.println("""
                1: average of all movie scores  
                2: List all words score
                3 and above: exit
                """);
        line = scan.nextLine();
        if (line.equals("1")){
            System.out.println("The average review for this movie gave it a " + (movieSentiment /  (double) reviewsProcessed)); 
        
        }else if (line.equals("2")){
            BSTMap<String, Double> ranking = analyzeAllWords(wordFreqs, wordTotalScores);
            List<String> analyzeKeyList = ranking.inorderKeys(); // originally, I had planned to write a quicksort method
            // to sort this, but the sort was already abstracted away in the BST, so. 
            for (int i = analyzeKeyList.size() - 1; i >= 0; i--)
            {
                 System.out.println(analyzeKeyList.get(i) + " : " + ranking.get(analyzeKeyList.get(i)));
            }
        }




            
    }
    


    /**
     * Read the file specified to add proper items to the word frequencies and word scores maps.
     */
    @SuppressWarnings("UnnecessaryTemporaryOnConversionFromString")
    private static void processFile(String filename,
                                    BSTMap<String, Integer> wordFreqs, BSTMap<String, Integer> wordTotalScores)
    {
        InputStream is = SentimentAnalysis.class.getResourceAsStream(filename);
        if (is == null) {
            System.err.println("Bad filename: " + filename);
            System.exit(1);
        }
        Scanner scan = new Scanner(is);

        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] words = line.split(" ");
         //   System.out.println(words[0]);
        int holder; 
   //     try{
            holder = Integer.parseInt(words[0]);
     //   }catch (NumberFormatException e){
         //    holder = 2; // two is the middle between 0 and 4
       // }
           // System.out.println("holder");
            for (int i = 1; i < words.length; i++)
            {
                
                if (wordTotalScores.containsKey(words[i])){ //&& wordFreqs.containsKey(words[i])){
                    // we only need to check one of the maps, see comment in removestopword method. 
                    // of file 
                    wordFreqs.put(words[i], wordFreqs.get(words[i]) + 1); 
                    wordTotalScores.put(words[i], wordTotalScores.get(words[i]) + holder);
                }else{
                    wordFreqs.put(words[i],  1); 
                    wordTotalScores.put(words[i], holder );
                }

                reviewsProcessed++;
                movieSentiment += holder;
                
                /*
                if (wordTotalScores.containsKey(words[i]))
                {
                    wordFreqs.put(words[i], wordFreqs.get(words[i]) + Integer.parseInt(words[1])); 
                }
                    the put() method already handles new words, we don't have to check for it. 
                    */
            }

        }
        scan.close();
    }

    /**
     * Print a table of the words found in the movie reviews, along with their frequencies and total scores.
     * Hint: Call wordFreqs.inorderKeys() to get a list of the words, and then loop over that list.
     */
    private static void printFreqsAndScores(BSTMap<String, Integer> wordFreqs, BSTMap<String, Integer> wordTotalScores)
    {
        System.out.println("Word Frequencies");
        List<String> freqKeys = wordFreqs.inorderKeys(); 
        System.out.println("""
                _________________________________________________________
                |
                """);
        for (int i = 0; i < freqKeys.size(); i++)
        {
            Integer val = wordFreqs.get(freqKeys.get(i));
            System.out.println(freqKeys.get(i) + ": " + val); 
        }
        System.out.println("""
                                                                        |
                __________________________________________________________
                """);
        System.out.println("Word Scores");
        List<String> wordScore = wordTotalScores.inorderKeys(); 
        System.out.println("""
                _________________________________________________________
                |
                """);
        for (int i = 0; i < wordScore.size(); i++)
        {
            Integer val = wordTotalScores.get(wordScore.get(i));
            System.out.println(wordScore.get(i) + ": " + val); 
        }
        System.out.println("""
                                                                        |
                __________________________________________________________
                """);
        

        


        
    }

    /**
     * Read the stopwords.txt file and add each word to the stopwords set.  Also remove each word from the
     * word frequencies and word scores maps.
     */
    private static void removeStopWords(BSTMap<String, Integer> wordFreqs,
                                        BSTMap<String, Integer> wordTotalScores, Set<String> stopwords)
    {
        InputStream is = SentimentAnalysis.class.getResourceAsStream("stopwords.txt");
        if (is == null) {
            System.err.println("Bad filename: " + "stopwords.txt");
            System.exit(1);
        }
        Scanner scan = new Scanner(is);

        while (scan.hasNextLine()) {
            String word = scan.nextLine();
            // Your code here.
            // You should add the word to the stopwords set, and also remove it from the
            // two maps.
            if (wordFreqs.containsKey(word))
            {
                
                wordFreqs.remove(word);
                wordTotalScores.remove(word);

                /*  
                wordfreqs and wordtotalscores contain all the words encountered. therefore, 
                if stopword ∈ wordFreqs, then it implies that stopword is also ∈ wordTotalScores

                Assertion : wordFreqs = wordScores 

                P1. ∀ word ∈ (Universal Set), wordfreqs.add(word) AND wordscores.add(word) (universal instansiation)
                P2. word ∈ wordFreqs AND word ∈ wordScores (conjunction)
                P3. word ∈ wordFreqs ∩ wordScores (definition of intersection)
                ∴ wordFreqs = wordScores (universal generalization)
                
                therefore, we really need to only check one if we maintain parity 

                this also means that 
                ∀ word ∈ (universal set), word ∉ wordFreqs -> word ∈ Stopwords. 
                ∴ stopwords = compliment(wordFreqs)
                */  
                stopwords.add(word);
            }


        }
        scan.close();
    }

    /**
     * 
     * 
     * @param wordFreqs
     * @param wordTotalScores
     * @return BSTMap <String, Double> 
     */
    private static BSTMap<String, Double> analyzeAllWords(BSTMap<String, Integer> wordFreqs, BSTMap<String, Integer> wordTotalScores)
    {
        BSTMap<String, Double> ret = new BSTMap<>(); 
        List<String> wordKeys = wordFreqs.preorderKeys();
        //preorder()  is used to keep the topology of the tree

        
        for (String s : wordKeys)
        {
            Double freq = Double.valueOf(wordFreqs.get(s));
            if (freq == null) continue; // catching an possible nullptr exception.
            Double word = Double.valueOf(wordTotalScores.get(s));

            double avgresult = word / freq; 
            ret.put(s, avgresult); 
            
        }
        return ret; 
     /*    Integer rawScore = wordTotalScores.get(ent);
                        Integer rawFreq = wordFreqs.get(ent); 
                        double average = ((double) rawScore  / (double) rawFreq); 
                        */ 
    } 
    private static void debugAnalysis(BSTMap<String, Integer> wordFreqs, BSTMap<String, Integer> wordTotalScores)
    {
        //added to allow ease of analysis for the debugger.
        //the debug broke on the large test due to bad math, but works well for reviews small. 
        List<String> freqKeys = wordFreqs.inorderKeys();
        List<String> scoreKeys = wordTotalScores.inorderKeys();

        ArrayList<Integer> freqScore = new ArrayList<Integer>();
        ArrayList<Integer> wordScore = new ArrayList<>();
        for (int i = 0; i < freqKeys.size(); i++) {
            freqScore.add(wordFreqs.get(freqKeys.get(i)));
        }
        for (int i = 0; i < scoreKeys.size(); i++) {
            wordScore.add(wordTotalScores.get(scoreKeys.get(i)));
        }
        double totalFreqCount = 0;
        double totalScoreCount = 0;

        for (Integer i : freqScore)
            totalFreqCount += i;
        for (Integer i : wordScore)
            totalScoreCount += i;

        double finalFreqAvgScore = totalFreqCount / freqScore.size();
        double finalWordAvgScore = totalScoreCount / wordScore.size();

        System.out.println(
                "final frequency average : " + finalFreqAvgScore + " final word average : " + finalWordAvgScore);
        double trueFinalAvg = (finalFreqAvgScore + finalWordAvgScore) / 2;

        System.out.println("True final average " + trueFinalAvg);
    }

}
