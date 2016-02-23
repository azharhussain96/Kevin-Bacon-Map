import com.sun.tools.javadoc.Start;
import net.datastructures.*;
import java.io.*;
import java.util.*;


/**
 * KevinBacon
 * Class that creates a directed graph to find the Kevin Bacon number of any actor in list
 * Uses breath-first search to create graph and then traverses directed edges to find the shortest
 * path back to the root (Kevin Bacon)
 * 2/23/16
 * CS 10 Lab 5 Winter 2016
 * @author Azhar Hussain
 * @author Alec Cobban
 *
 */
public class KevinBacon {

    // starting bacon number
    private int START = 0;

    // map containing actor id as key and name
    private java.util.Map<Integer, String> actorMap;

    // map containing movie id as key and name
    private java.util.Map<Integer, String> movieMap;

    // map containing id for movie as key, and an array containing ids of all actors in that movie
    private java.util.Map<Integer, java.util.ArrayList<Integer>> actorMovieMap;

    // undirected graph containing all connections
    private NamedAdjacencyMapGraph<String, String> kevinBaconGraph = new NamedAdjacencyMapGraph<>(false);

    // directed graph from specific root node
    private NamedAdjacencyMapGraph<String, String> directedBaconGraph = new NamedAdjacencyMapGraph<>(true);

    // linked list to create queue for BFS
    private SentinelDLL<Vertex> linkedList = new SentinelDLL<>();


    /**
     * KevinBacon
     * Constructor
     * Creates empty actor, movie, and actorMovie maps
     */
    public KevinBacon() {
        actorMap = new HashMap<>();
        movieMap = new HashMap<>();
        actorMovieMap = new HashMap<>();
    }

    /**
     * makeActorMap
     * Reads each line in the file and creates map of actor id and corresponding name
     */
    public void makeActorMap() throws IOException {

        // try catch block since we are opening a file
        try {

            // Open file and read each line
            File file = new File("actors.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            // while there is a line to read, separate id and name
            while ((line = bufferedReader.readLine()) != null) {
                int pipe = line.indexOf("|");

                // insert key and data into the actorMap
                actorMap.put(Integer.parseInt(line.substring(START, pipe)), (line.substring(pipe + 1)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * makeMovieMap
     * Reads each line in the file and creates map of movie id and corresponding name
     */
    public void makeMovieMap() throws IOException {

        // try catch block since we are opening a file
        try {

            // Open file and read each line
            File file = new File("movies.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            // while there is a line to read, separate id and name
            while ((line = bufferedReader.readLine()) != null) {
                int pipe = line.indexOf("|");

                // insert key and data into the actorMap
                movieMap.put(Integer.parseInt(line.substring(START, pipe)), (line.substring(pipe + 1)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * makeActorMovieMap
     * Reads each line in the file and creates map of movie id and array containg ids of actors in movie
     */
    public void makeActorMovieMap() throws IOException {

        // try catch block since we are opening a file
        try {

            // Open file and read each line
            File file = new File("movie-actors.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            // while there is a line to read, separate id and name
            while ((line = bufferedReader.readLine()) != null) {
                int pipe = line.indexOf("|");
                int key = Integer.parseInt(line.substring(START, pipe));

                // if movie already in map, insert actor id into movie array list
                if (actorMovieMap.containsKey(key)) {
                    actorMovieMap.get(key).add(Integer.parseInt(line.substring(pipe + 1)));
                } else {

                    // else, create new array list for movie and insert actor id
                    actorMovieMap.put(key, new java.util.ArrayList<Integer>());
                    actorMovieMap.get(key).add(Integer.parseInt(line.substring(pipe + 1)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * makeKevinBaconGraph
     * helper function to create an undirected graph
     */
    public void makeKevinBaconGraph() {

        makeGraph(kevinBaconGraph);
    }

    /**
     * makeGraph
     * Creates an undirected map of all actors in the file, and connects them by common movie edges
     * @param map the Map that will be used to place vertexes and edges to create undirected graph
     */
    public void makeGraph(NamedAdjacencyMapGraph<String, String> map) {

        // for each actor in the Map
        for (int key : actorMap.keySet()) {

            // insert actor into the undirected graph (vertex)
            map.insertVertex(actorMap.get(key));
        }

        // for each actor in the same movie
        for (int movie : actorMovieMap.keySet()) {
            for (int actorOne : actorMovieMap.get(movie)) {
                for (int actorTwo : actorMovieMap.get(movie)) {

                    // get two actors out of the movie arrayList
                    String actorOneName = actorMap.get(actorOne);
                    String actorTwoName = actorMap.get(actorTwo);

                    // if the two actors are different and an edge does not exist between them
                    if ((actorOne != actorTwo) && (map.getEdge(actorOneName, actorTwoName) == null)) {

                        // insert the edge between the two actors
                        map.insertEdge(actorMap.get(actorOne), actorMap.get(actorTwo), movieMap.get(movie));
                    }
                }
            }
        }
    }


    /**
     * BFS is a method which takes the name of a root vertex and returns
     * a directed graph (built using the undirected actors-movies graph),
     * with all nodes pointing to that actor through other actors. Each actor
     * will only appear in the graph once, with one arrow pointing toward them
     * and one toward the root, and this graph is determined by examining adjacent vertices
     * to the root and querying all of their adjacent vertices, putting back pointers back to the root.
     * @param root this is the root to examine here, one could examine a wide variety of roots.
     * Stores as instance variable the BFS graph.
     */
    public void BFS(String root) {

        //This is the root to be used for the BFS graph
        Vertex<String> rootVertex = kevinBaconGraph.getVertex(root);

        //put the root in a linkedList
        linkedList.add(rootVertex);

        //put the root into the graph
        directedBaconGraph.insertVertex(root);

        // while there are still actors that haven't been added to the graph
        while (!linkedList.isEmpty()) {

            //assign vertex to be the first element in the list
            Vertex<String> vertex = linkedList.getFirst();

            //get rid of the first element
            linkedList.remove();

            //query all of the incoming edges
            java.lang.Iterable<Edge<String>> list = kevinBaconGraph.incomingEdges(vertex);

            for (Edge<String> incoming : list) {
                //the vertex across the edge in this iteration
                String oppositeVertex = kevinBaconGraph.returnElement(kevinBaconGraph.opposite(vertex, incoming));

                //if already in the directed graph then do nothing
                if (!directedBaconGraph.vertexInGraph(oppositeVertex)) {

                    //add to the directed graph
                    directedBaconGraph.insertVertex(oppositeVertex);

                    //make an edge pointing back toward the root
                    directedBaconGraph.insertEdge(oppositeVertex, kevinBaconGraph.returnElement(vertex),

                            //with the name of the edge in the Movie-Actor graph
                            kevinBaconGraph.returnEdge(
                                    kevinBaconGraph.getEdge(kevinBaconGraph.getVertex(oppositeVertex), vertex)));

                    //add the previous element to the linked list before looping
                    linkedList.addLast(kevinBaconGraph.opposite(vertex, incoming));
                }
            }
        }
    }

    /**
     * Takes as input an actors name, and follows pointers leading to the root.
     * While doing this it counts the number of edges that it needs to follow returning the bacon number.
     * For each edge, it prints out the actor's name, the movie, and the next actor's name,
     * until it reaches the root of the BFS graph.
     * 
     * @param actor the name of an actor to find in the graph and determine the bacon number
     * @return string of path back from the actor we are querying and the root (Kevin Bacon)
     */
    public String traverseMap(String actor) {

        //if the actor name isn't in the directed bacon map then either
        if (!directedBaconGraph.vertexInGraph(actor)){

            //it is not in the database
            if (!actorMap.containsValue(actor)){
                return actor + " is not in our database.";
            } else {

                //or if it is also in the database, then the KB number is infinite.
                return actor + "'s Kevin Bacon number is infinite.";
            }
        }

        //setup a vertex to use as current vertex, initialize to actor param
        Vertex<String> currentVertex = directedBaconGraph.getVertex(actor);

        //make an iterable list of outgoing edges from the vertex
        java.lang.Iterable<Edge<String>> list = directedBaconGraph.outgoingEdges(currentVertex);

        //make an iterator for this list
        java.util.Iterator<Edge<String>> outgoingEdgesList = list.iterator();

        //an initial bacon path string that will be added onto the final bacon path at the end
        String baconPathTemp = "";

        //if doesn't have next then this is the right actor
        int baconNumber = START;

        //while there is an outgoing for this vertex continue.
        while (outgoingEdgesList.hasNext()) {

            //increase the bacon number on each round
            baconNumber++;

            //grab the next edge in the list (first edge)
            Edge<String> nextEdge = outgoingEdgesList.next();

            // add the link in the list of connections to the string
            baconPathTemp += currentVertex.getElement() + " appeared in " + nextEdge.getElement() + " with " + 
            		directedBaconGraph.opposite(currentVertex, nextEdge).getElement() + ". \n";

            //grab the next vertex and call it current
            currentVertex = directedBaconGraph.opposite(currentVertex, nextEdge);

            //get the next vertex's outgoing (only toward root)
            outgoingEdgesList = directedBaconGraph.outgoingEdges(currentVertex).iterator();
        }

        String baconPath = actor + "'s " + currentVertex.getElement() + " number is " + baconNumber + "\n";
        baconPath += baconPathTemp;

        // path from actor back to root
        return baconPath;
    }


    public static void main(String[] args) {
        try {
            String actor = "Kevin Bacon";
            String actorName = "";
            KevinBacon test = new KevinBacon();
            test.makeActorMap();
            test.makeMovieMap();
            test.makeActorMovieMap();
            test.makeKevinBaconGraph();
            test.BFS(actor);

            while (true) {
                Scanner input = new Scanner(System.in);
                System.out.println("Enter the name of an actor: ");
                String nextLine = input.nextLine();
                
                actorName = nextLine;


                System.out.println(test.traverseMap(actorName));

            }
        }catch (IOException e) {
                e.printStackTrace();
            }


    }
}
