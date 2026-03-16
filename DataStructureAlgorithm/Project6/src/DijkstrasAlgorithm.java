/* I HAVE NEITHER GIVEN NOR RECIEVED UNAUTHORIZED AID ON THIS PROGRAM - CHARLES REED

*/


import java.io.InputStream;
import java.util.*;


public class DijkstrasAlgorithm {

    public static boolean verbose = false;  
    public static void main(String[] args)
    {
        if (args.length > 0 )
        {
            if (args[0].equals("-v")){
                System.out.println("Verbose mode enabled."); 
                verbose = true; 
            }
        }
        Scanner scan = new Scanner(System.in);
        System.out.print("What file do you want to read? ");
        String filename = scan.nextLine();
        processFile(filename);
    }

    public static void dijkstra(Graph g, String start, String finish)
    {

        System.out.println("Verticies: " + g.getVertices());
        System.out.println("Edges: ");
        
        for (String vert : g.getVertices())
        {
            for (String vert2 : g.getAdjacentVerticesFrom(vert))
            {
                System.out.println(vert + "->" + vert2 + ": " + g.getWeight(vert, vert2));
            }
        }

        System.out.println(); 
        System.out.println(); 

        
        Map<String, Integer> dist = new TreeMap<String,Integer>(); //[you pick HashMap or TreeMap]
        Map<String, String> prev = new TreeMap<String,String>(); //[you pick HashMap or TreeMap]
        PriQueue<String, Integer> pq = new PriQueue<String, Integer>(true);

        ArrayList<String> backPath = new ArrayList<>(); 

       //PriQueue<String, Integer> maxqueue = new PriQueue<String, Integer>(false);

        Set<String> verts = g.getVertices(); 

        for (String s : verts)
        {
            dist.put(s, Integer.MAX_VALUE);
            prev.put(s, null);
        }

        dist.put(start, 0); 
        
        pq.add(start, 0);

        int alt; 
        while (!pq.isEmpty())
        {
            // this is a minqueue, so we can just "pop" the first entry. 

            String curr = pq.remove(); 

            //if (verbose)
            System.out.println("Current Vertex: " + curr);
            if (curr.equals(finish)) break;
            for (String currNeighbor : g.getAdjacentVerticesFrom(curr)){
                alt = dist.get(curr) + g.getWeight(curr, currNeighbor);
                
                if (verbose) { System.out.println("Currently considering {" + curr + " , " + 
                    currNeighbor + "}. alt is " + alt); }
                if (alt < dist.get(currNeighbor)){
                    System.out.println("Updating dist[" + currNeighbor + "] from " + dist.get(currNeighbor) + " to " + alt);
                    dist.put(currNeighbor, alt); 
                    prev.put(currNeighbor, curr);
                    if (pq.contains(currNeighbor))
                    {
                        //System.out.println("Changing " + currNeighbor + "'s priority from " + pq.)
                        pq.changePriority(currNeighbor, alt);
                    }else{
                        pq.add(currNeighbor, alt); 
                    }
                    

                }
            }

        }

        if (verbose) System.out.println("Tracking Backwards. ");
        String currBackOrder = finish;
        //String temp;
        
        backPath.add(finish);
        while(true)
            {  
                // I have no clue if this will work.
          //      temp = currBackOrder;  
                currBackOrder = prev.get(currBackOrder);
             //   maxqueue.add(currBackOrder, g.getWeight(temp, currBackOrder)); 
                backPath.add(currBackOrder);



                if (currBackOrder.equals(start)){

                    break; 
                }
            } 


        Collections.reverse(backPath); 
        System.out.println(); 

        System.out.print("shortest path is : ");

        for (String s : backPath)
        {
            System.out.print(s + " "); 
        }
        System.out.println(); 
        System.out.println(); 





        System.out.println("Final Length: " + dist.get(finish));
        //System.out.println(maxqueue.toString());
       // System.out.println(backPath.toString());

        
        System.out.println(); 
        System.out.println(); 

        System.out.println("Final Dist Map:");

        for (String s : dist.keySet())
        {
            System.out.println(s + ": " + dist.get(s));
        }
        System.out.println(); 
        System.out.println(); 

        System.out.println("Final Prev Map:");
        for (String s : prev.keySet())
        {
            String t = prev.get(s);
            if (t == null)
            {
                System.out.println(s + ": undefined" );
            }else{
                System.out.println(s + ": " + t);
            }

        }





        // Your Dijkstra's algorithm code here.
    }

    /**
     * Read the file specified to add proper items to the word frequencies.
     */
    private static void processFile(String filename)
    {
        InputStream is = DijkstrasAlgorithm.class.getResourceAsStream(filename);
        if (is == null) {
            System.err.println("Bad filename: " + filename);
            System.exit(1);
        }
        Scanner scan = new Scanner(is);

        // Make a blank graph.
        Graph g = new Graph();
        if (verbose) System.out.println(g.toString());

        boolean firstline = true; 
        boolean directed = false; 
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] words = line.split(" ");
            if (firstline){
                if (verbose) System.out.println("firstline.");
                if (words[0].equals("directed")){
                    if (verbose) System.out.println("Detected as directed");
                    directed = true; 
                }
                firstline = false; 
                 
            }
            else if (words[0].equals("vertex"))
            {
                if (verbose) System.out.println("vertex: " + words[1]);
                g.addVertex(words[1]);

            }
            else if (words[0].equals("edge"))
            {
                if (verbose) System.out.println(Arrays.toString(words));
                g.addEdge(words[1], words[2], Integer.parseInt(words[3]));

                if (!directed) g.addEdge(words[2], words[1], Integer.parseInt(words[3]));
            }
            else if (words[0].equals("dijkstra")){
                if (verbose) System.out.println("Called Dijkstra.");
                dijkstra(g, words[1], words[2]);
            }
            else{
                System.out.println(Arrays.toString(words)); 
            }







            // Add your code here.  You can also define
            // other variables outside this loop if you want to.
            // Follow the examples from past projects that had code that
            // processed a file.  The "dijkstra" line will always be the
            // last line in the file, and you can call the dijkstra() function
            // directly from here.
        }
        scan.close();
    }
}
