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

    public void makeGraph(NamedAdjacencyMapGraph map) {
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

    public void BFS(String root) {

        Vertex rootVertex = kevinBaconMap.getVertex(root);

        linkedList.add(rootVertex);
        directedBaconMap.insertVertex(root);

        while (!linkedList.isEmpty()) {
            //String element = linkedList.getFirst().toString();
            Vertex vertex = linkedList.getFirst();
            linkedList.remove();
            java.lang.Iterable<Edge<String>> list = kevinBaconMap.incomingEdges(vertex);

            for (Edge<String> incoming : list) {
                String oppositeVertex = kevinBaconMap.returnElement(kevinBaconMap.opposite(vertex, incoming));
                if (!directedBaconMap.vertexInGraph(oppositeVertex)) {
                    directedBaconMap.insertVertex(oppositeVertex);
                    directedBaconMap.insertEdge(oppositeVertex, kevinBaconMap.returnElement(vertex),
                            kevinBaconMap.returnEdge(kevinBaconMap.getEdge(kevinBaconMap.getVertex(oppositeVertex), vertex)));

                    linkedList.addLast(kevinBaconMap.opposite(vertex, incoming));
                }
            }
        }
    }

    public String traverseMap(String actor) {

        if (!directedBaconMap.vertexInGraph(actor)){
            if (!actorMap.containsValue(actor)){
                return actor + " is not in our database.";
            } else {
                return actor + "'s Kevin Bacon number is infinite.";
            }
        }

        Vertex<String> currentVertex = directedBaconMap.getVertex(actor);
        java.lang.Iterable<Edge<String>> list = directedBaconMap.outgoingEdges(currentVertex);
        java.util.Iterator<Edge<String>> outgoingEdgesList = list.iterator();
        String baconPathTemp = "";
        int baconNumber = 0;


        while (outgoingEdgesList.hasNext()) {
            baconNumber++;
            Edge<String> nextEdge = outgoingEdgesList.next();
            baconPathTemp += currentVertex.getElement() + " appeared in " + nextEdge.getElement() + " with " + directedBaconMap.opposite(currentVertex, nextEdge).getElement() + ". \n";
            currentVertex = directedBaconMap.opposite(currentVertex, nextEdge);
            outgoingEdgesList = directedBaconMap.outgoingEdges(currentVertex).iterator();
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
