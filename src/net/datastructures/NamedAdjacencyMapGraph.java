package net.datastructures;


import java.util.HashMap;
import java.util.Map;

/**
 * NamedAdjacencyMapGraph
 * creates a mapgraph which contains each vertex object mapped to its name as key
 * Created by azharhussain on 2/12/16.
 */
public class NamedAdjacencyMapGraph<V,E> extends AdjacencyMapGraph<V,E> {
    Map<V,Vertex<V>> map;

    /**
     * NamedAdjacencyMapGraph
     * constructor of map graph
     * @param directed tells if the graph is directed or not
     */
    public NamedAdjacencyMapGraph(boolean directed) {
        super(directed);
        map = new HashMap<V,Vertex<V>>();  // map with key as vertex name and object as vertex
    }

    /**
     * getVertex
     * returns the vertex from the map
     * @param name name of the vertex in Map
     * @return the vertex object
     */
    public Vertex<V> getVertex(V name){
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return null;
    }

    /**
     * vertexInGraph
     * Returns boolean value if vertex is in graph
     * @param name name of the vertex object
     * @return boolean if object is in map
     */
    public boolean vertexInGraph(V name) throws IllegalArgumentException{
        return map.containsKey(name);
    }

    /**
     * insertEdge
     * inserts a new edge in the graph
     * @param uName name of the first vertex
     * @param vName name of the second vertex
     * @param element edge element
     * @return Edge object
     */
    public Edge<E> insertEdge(V uName, V vName, E element) throws IllegalArgumentException{

        // find if edge exists, if not then insert
        if (super.getEdge(map.get(uName), map.get(vName)) == null){
            super.insertEdge(map.get(uName),map.get(vName), element);
            return super.getEdge(map.get(uName), map.get(vName));

        } else
            throw new IllegalArgumentException("Edge already Exists");
    }

    /**
     * getEdge
     * returns the edge object
     * @param uName name of first vertex
     * @param vName name of the second vertex
     * @return returns an edge object
     */
    public Edge<E> getEdge(V uName, V vName) throws IllegalArgumentException{

        // find if edge exists, if does, return
        if (super.getEdge(map.get(uName), map.get(vName)) != null){
            return super.getEdge(map.get(uName), map.get(vName));
        } else {
            return null;
        }
    }

    /**
     * insertVertex
     * creates a new vertex and inserts it into the graph
     * @param element name of vertex element
     * @return a new vertex element
     */
    public Vertex<V> insertVertex(V element) throws IllegalArgumentException{

        // check if map contains the vertex, if not, insert new vertex
        if (!map.containsKey(element)) {
            map.put(element,super.insertVertex(element));
            return map.get(element);
        } else {
            throw new IllegalArgumentException("Vertex already in Graph");

        }
    }

    /**
     * removeVertex
     * removes a vertex from the graph
     * @param v vertex object that should be removed
     */
    public void removeVertex(Vertex<V> v) throws IllegalArgumentException{

        // check if map contains vertex, if does, remove
        if (map.containsValue(v)){
            super.removeVertex(v);
            for (V key: map.keySet()){
                if (map.get(key).equals(v)){
                    map.remove(key);
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("Vertex does not exist");
        }
    }

    public static void main(String [] args) {
        boolean isDirected = false;
        NamedAdjacencyMapGraph<String, String> baconGraph;

        do {
            baconGraph = new NamedAdjacencyMapGraph<String, String>(isDirected);

            System.out.println("\nisDirected = " + isDirected);

            baconGraph.insertVertex("Kevin Bacon");
            baconGraph.insertVertex("Laura Linney");
            baconGraph.insertVertex("Tom Hanks");
            baconGraph.insertVertex("Liam Neeson");
            baconGraph.insertEdge("Laura Linney","Kevin Bacon", "Mystic River");
            baconGraph.insertEdge("Liam Neeson", "Laura Linney", "Kinsey");
            baconGraph.insertEdge( "Tom Hanks", "Kevin Bacon", "Apollo 13");

            System.out.println("\nvertexInGraph for Laura Linney = " +
                    baconGraph.vertexInGraph("Laura Linney"));

            System.out.println("\nvertexInGraph for L. Linney = " +
                    baconGraph.vertexInGraph("L. Linney"));

            System.out.println("\ngetEdge between Laura Linney and Tom Hanks = " +
                    baconGraph.getEdge("Laura Linney", "Tom Hanks"));

            System.out.println("\ngetEdge between Laura Linney and Kevin Bacon = " +
                    baconGraph.getEdge("Laura Linney", "Kevin Bacon").getElement());

            Edge<String> e = baconGraph.getEdge("Kevin Bacon", "Laura Linney");
            if(e == null)
                System.out.println("\nNo edge between Kevin Bacon and Laura Linney");
            else
                System.out.println("\ngetEdge between between Kevin Bacon and Laura Linney = " +
                        e.getElement());


            System.out.println("\nInDegree of Laura Linney = " +
                    baconGraph.inDegree(baconGraph.getVertex("Laura Linney")));

            System.out.println("\nOutDegree of Laura Linney = " +
                    baconGraph.outDegree(baconGraph.getVertex("Laura Linney")));

            System.out.println("\nEdges into to Laura Linney:");
            for(Edge<String> edge : baconGraph.incomingEdges(baconGraph.getVertex("Laura Linney")))
                System.out.println(edge.getElement());

            System.out.println("\nEdges out of to Laura Linney:");
            for(Edge<String> edge : baconGraph.outgoingEdges(baconGraph.getVertex("Laura Linney")))
                System.out.println(edge.getElement());

            System.out.println("\nThe entire graph:");
            for(Vertex<String> vertex : baconGraph.vertices()) {

                System.out.println("\nEdges into " + vertex.getElement() + ":");
                for(Edge<String> edge : baconGraph.incomingEdges(vertex))
                    System.out.println(edge.getElement());

                System.out.println("\nEdges out of " + vertex.getElement() + ":");
                for(Edge<String> edge : baconGraph.outgoingEdges(vertex))
                    System.out.println(edge.getElement());
            }

            baconGraph.removeVertex(baconGraph.getVertex("Kevin Bacon"));

            System.out.println("\nCalled removeVertex for Kevin Bacon");
            System.out.println("getVertex for Kevin Bacon returns: " +
                    baconGraph.getVertex("Kevin Bacon"));

            System.out.println("\nThe entire graph after Kevin Bacon removed:");
            for(Vertex<String> vertex : baconGraph.vertices()) {

                System.out.println("\nEdges into " + vertex.getElement() + ":");
                for(Edge<String> edge : baconGraph.incomingEdges(vertex))
                    System.out.println(edge.getElement());

                System.out.println("\nEdges out of " + vertex.getElement() + ":");
                for(Edge<String> edge : baconGraph.outgoingEdges(vertex))
                    System.out.println(edge.getElement());
            }
            isDirected = !isDirected;
        } while(isDirected);

        try{
            baconGraph.insertVertex("Laura Linney");
        }
        catch(IllegalArgumentException ex) {
            System.out.println("\nCaught exception for duplicate vertex name: " +
                    ex.getMessage());
        }
    }

}

