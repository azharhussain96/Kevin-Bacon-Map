import net.datastructures.*;
import java.io.*;
import java.util.*;


/**
 * Created by azharhussain on 2/18/16.
 */
public class KevinBacon {

    java.util.Map<Integer, String> actorMap;
    java.util.Map<Integer, String> movieMap;
    java.util.Map<Integer, java.util.ArrayList<Integer>> actorMovieMap;
    NamedAdjacencyMapGraph<String, String> kevinBaconMap = new NamedAdjacencyMapGraph<>(false);
    NamedAdjacencyMapGraph<String, String> directedBaconMap = new NamedAdjacencyMapGraph<>(true);
    SentinelDLL<Vertex> linkedList = new SentinelDLL<>();


    public KevinBacon() {
        actorMap = new HashMap<>();
        movieMap = new HashMap<>();
        actorMovieMap = new HashMap<>();
    }

    public void makeActorMap() throws IOException {
        try {

            File file = new File("actorsTest.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int pipe = line.indexOf("|");

                actorMap.put(Integer.parseInt(line.substring(0, pipe)), (line.substring(pipe + 1)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeMovieMap() throws IOException {
        try {

            File file = new File("moviesTest.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int pipe = line.indexOf("|");
                movieMap.put(Integer.parseInt(line.substring(0, pipe)), (line.substring(pipe + 1)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeActorMovieMap() throws IOException {
        try {

            File file = new File("movie-actorsTest.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int pipe = line.indexOf("|");
                int key = Integer.parseInt(line.substring(0, pipe));

                if (actorMovieMap.containsKey(key)) {
                    actorMovieMap.get(key).add(Integer.parseInt(line.substring(pipe + 1)));
                } else {
                    actorMovieMap.put(key, new java.util.ArrayList<Integer>());
                    actorMovieMap.get(key).add(Integer.parseInt(line.substring(pipe + 1)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeGraph(NamedAdjacencyMapGraph<String, String> map) {
        for (int key : actorMap.keySet()) {
            map.insertVertex(actorMap.get(key));
        }

        for (int movie : actorMovieMap.keySet()) {
            for (int actorOne : actorMovieMap.get(movie)) {
                for (int actorTwo : actorMovieMap.get(movie)) {
                    String actorOneName = actorMap.get(actorOne);
                    String actorTwoName = actorMap.get(actorTwo);
                    if ((actorOne != actorTwo) && (map.getEdge(actorOneName, actorTwoName) == null)) {
                        map.insertEdge(actorMap.get(actorOne), actorMap.get(actorTwo), movieMap.get(movie));
                    }
                }
            }
        }
    }

    public void makeKevinBaconGraph() {

        makeGraph(kevinBaconMap);
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

        Vertex<String> rootVertex = kevinBaconMap.getVertex(root); //This is the root to be used for the BFS graph

        linkedList.add(rootVertex); //put the root in a linkedList
        directedBaconMap.insertVertex(root); //put the root into the graph

        while (!linkedList.isEmpty()) {// while there are still actors that haven't been added to the graph
            Vertex<String> vertex = linkedList.getFirst();//assign vertex to be the first element in the list
            linkedList.remove(); //get rid of the first element
            java.lang.Iterable<Edge<String>> list = kevinBaconMap.incomingEdges(vertex); //query all of the incoming edges

            for (Edge<String> incoming : list) {
                String oppositeVertex = kevinBaconMap.returnElement(kevinBaconMap.opposite(vertex, incoming));//the vertex across the edge in this iteration
                if (!directedBaconMap.vertexInGraph(oppositeVertex)) { //if already in the directed graph then do nothing
                    directedBaconMap.insertVertex(oppositeVertex); //add to the directed graph
                    directedBaconMap.insertEdge(oppositeVertex, kevinBaconMap.returnElement(vertex),//make an edge pointing back toward the root
                            kevinBaconMap.returnEdge(kevinBaconMap.getEdge(kevinBaconMap.getVertex(oppositeVertex), vertex)));//with the name of the edge in the Movie-Actor graph
                    linkedList.addLast(kevinBaconMap.opposite(vertex, incoming)); //add the previous element to the linked list before looping
                }
            }
        }
    }
    /**
     * Takes as input an actors name, and follows pointers leading to the root.
     * While doing this it counts the number of edges that it needs to follow returning the bacon number.
     * For each edge, it prints out the actor's name, the movie, and the next actor's name, until it reaches the root of the BFS graph.
     * 
     * @param actor the name of an actor to find in the graph and determine the bacon number
     * @return
     */
    public String traverseMap(String actor) {

        if (!directedBaconMap.vertexInGraph(actor)){ //if the actor name isn't in the directed bacon map then either
            if (!actorMap.containsValue(actor)){ //it is not in the database
                return actor + " is not in our database.";
            } else {
                return actor + "'s Kevin Bacon number is infinite."; //or if it is also in the database, then the KB number is infinite.
            }
        }

        Vertex<String> currentVertex = directedBaconMap.getVertex(actor);//setup a vertex to use as current vertex, initialize to actor param
        java.lang.Iterable<Edge<String>> list = directedBaconMap.outgoingEdges(currentVertex); //make an iterable list of outgoing edges from the vertex
        java.util.Iterator<Edge<String>> outgoingEdgesList = list.iterator(); //make an iterator for this list
        String baconPathTemp = ""; //an initial bacon path string that will be added onto the final bacon path at the end
        int baconNumber = 0;//if doesn't have next then this is the right actor


        while (outgoingEdgesList.hasNext()) { //while there is an outgoing for this vertex continue.
            baconNumber++; //increase the bacon number on each round
            Edge<String> nextEdge = outgoingEdgesList.next(); //grab the next edge in the list (first edge)
            baconPathTemp += currentVertex.getElement() + " appeared in " + nextEdge.getElement() + " with " + 
            		directedBaconMap.opposite(currentVertex, nextEdge).getElement() + ". \n"; // add the link in the list of connections to the string
            currentVertex = directedBaconMap.opposite(currentVertex, nextEdge); //grab the next vertex and call it current
            outgoingEdgesList = directedBaconMap.outgoingEdges(currentVertex).iterator(); //get the next vertex's outgoing (only toward root)
        }

        String baconPath = actor + "'s " + currentVertex.getElement() + " number is " + baconNumber + "\n";
        baconPath += baconPathTemp;

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
