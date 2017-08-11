/**
 * Created by scott on 7/24/2017.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DijkstraClass
{

    public class Vertex
    {
        public List<Vertex> adj = new ArrayList<>(); // Adjacency list
        public boolean known;
        public int price;

        // Used so we have a list of all the edge prices and what nodes they are tied to for access
        public HashMap<Vertex, Integer> edgePriceMap = new HashMap<>();
        public Vertex path;
        public String name;

        public String toString() // Used so we can check the adjacency list
        {
            return name;
        }
    } // Vertex class

    private void printPath (Vertex v)
    {
        if (v.path != null)
        {
            printPath(v.path);
            System.out.print(" -> ");
        }
        System.out.print(v);
    } // printPath

    private int getNumConnections (Vertex v)
    {
        if (v.path != null)
        {
            getNumConnections(v.path);
            return connections++;
        }
        return connections;
    } // getNumConnections

    private boolean confirmPathExists (Vertex arrive, Vertex depart)
    {
        if (arrive.path != null)
        {
            if (!confirmPathExists(arrive.path, depart)) // If false is returned up the tree, keep returning false, else true
                return false;
            else
                return true;
        }
        else
        {
            // If the final vertex in the path does not equal depart, the path does not exist
            if (arrive.name.equals(depart.name))
                return true;
            else
                return false;
        }
    } // confirmPathExists

    class MyComparator implements Comparator<Vertex>
    {
        public int compare(Vertex a, Vertex b)
        {
            return Integer.compare(a.price, b.price);
        }
    }

    private Vertex getVertex (String str)
    {
        return hm.get(str);
    }

    public void getShortestPath (String depart, String arrive)
    {
        // Make sure we capitalize the depart and arrive vertices before doing anything with them
        depart = depart.toUpperCase();
        arrive = arrive.toUpperCase();

        // Get the corresponding vertex objects from the hash table and store them for easier use in other function calls
        Vertex arriveVertex = getVertex(arrive);
        Vertex departVertex = getVertex(depart);

        // Verify that both vertices the user enters exist in the hash map. If not, inform them.
        if (hm.containsValue(arriveVertex) && hm.containsValue(departVertex))
        {

            // If the source vertex passed in is the same as the previous source, don't redo the algorithm
            if (source == null || !source.name.equals(departVertex.name))
            {
                pq.addAll(hm.values()); // Load up the priority queue with the vertices from the hash map

                Dijkstra(departVertex);
                source = departVertex; // Next time we come in, we can compare with this to see if the algorithm should run again
            }

            if (confirmPathExists(arriveVertex, departVertex))
            {
                System.out.println("\nPrice: " + arriveVertex.price); // Print the price

                System.out.println("Connections: " + getNumConnections(arriveVertex)); // Print our number of connections
                connections = 0; // Reset the number of connections after printing it

                printPath(arriveVertex); // Print the path
            }
            else
                System.out.println("\nSorry, a path from your city to your destination does not exist.");
        }
        else
            System.out.println("One or both of your cities does not exist");
    }

    private void Dijkstra (Vertex s)
    {
        for (Vertex v : pq)
        {
            v.price = Integer.MAX_VALUE;
            v.known = false;
            v.path = null;
        }

        s.price = 0; // Set source vertex as cost of 0

        // Update the priority queue after setting the price for the starting node
        pq.remove(s);
        pq.add(s);

        while (!pq.isEmpty())
        {
            Vertex v = pq.remove();

            if (!v.known)
            {
                v.known = true;

                for (Vertex w : v.adj)
                {
                    if (!w.known)
                    {
                        int cvw = v.edgePriceMap.get(w); // Cost from v to w

                        if (v.price + cvw < w.price)
                        {
                            w.price = v.price + cvw; // Not sure if correct
                            w.path = v;
                        }
                        pq.remove(w);
                        pq.add(w);
                    }
                }
            }
        }
    }

    private DijkstraClass ()
    {
        Parser();
    }

    private void Parser ()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("airports.txt"));
            String fileRead = br.readLine();

            while (fileRead != null)
            {
                String[] token = fileRead.split(" "); // Split input with a space delimiter

                Vertex V = new Vertex();
                V.name = token[0].toUpperCase();

                if (!hm.containsKey(V.name)) // If not already in the has table, put it in
                {
                    hm.put(V.name.toUpperCase(), V);
                }
                else // If already in the hash table, change V to be the vertex that's already in the table
                {
                    V = hm.get(V.name);
                }

                for (int i = 0; i < token.length; i++)
                {
                    if (token[i].equals(""))
                    {
                        int tempPrice = Integer.parseInt(token[i + 2]);
                            Vertex V2 = new Vertex();
                            V2.name = token[++i].toUpperCase();

                        if (!hm.containsKey(V2.name)) // If the vertex is not yet in the hash map, set it up and add it
                        {
                            // Add the edge price and what vertex the price is associated with to our edgePriceMap
                            V.edgePriceMap.put(V2, tempPrice);

                            // After V2 has been set up, add it to V's adjacency list
                            V.adj.add(V2);
                            hm.put(V2.name, V2);
                        }
                        else // If the vertex is already in the hash map, set up V's adjacency and edge price map with it
                        {
                            // Load the vertex from the hash map and use that for adjacency list and edgePriceMap
                            Vertex temp = hm.get(V2.name);
                            V.adj.add(temp);
                            V.edgePriceMap.put(temp, tempPrice);
                        }
                    }
                }
                fileRead = br.readLine(); // Read in the next line from the file
            }
        }
        catch (FileNotFoundException nf)
        {
            System.out.println("File not found");
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private HashMap<String, Vertex> hm = new HashMap<>();
    private Comparator<Vertex> comparator = new MyComparator();
    private PriorityQueue<Vertex> pq = new PriorityQueue<>(20, comparator);
    private Vertex source; // Used to avoid redoing Dijkstra's algorithm if the source is the same as the last run
    private int connections; // Keep track of the number of connections for use in the output. Note that the number of
                             // connections excludes the start and end vertices/cities

    public static void main (String args [])
    {
        DijkstraClass D = new DijkstraClass();
        Scanner sc = new Scanner (System.in);
        String arrivalAirport, departureAirport;
        String choice;

        // Get the users departure and arrival airports
        do
        {
            System.out.println("Enter departure airport:\t");
            departureAirport = sc.next();
            System.out.println("Enter arrival airport:\t");
            arrivalAirport = sc.next();

            D.getShortestPath(departureAirport, arrivalAirport);

            System.out.println("\nEnter Q to quit or something else to continue");
            choice = sc.next();
        } while (!choice.equals("Q") && !choice.equals("q"));
    } // main
} // DijkstrasAlgorithm class
