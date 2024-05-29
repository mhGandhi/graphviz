package app.model;

import java.io.Serializable;
import java.util.*;

/**
 * Die Graphklasse verkörpert eine Adjazenzliste mit einem System zur automatischen Zuweisung von Id's für die Knoten.
 */
public class Graph implements Serializable{

////////////////////////////////////////////////////////////////////////////////////////////////////attributes
    /**
     * Adjazenzliste in Form einer Map
     */
    private Map<Node, List<Edge>> adjList;
    
    /**
     * Gibt die nächste Id an, welche neuen Knoten zugewiesen wird.
     */
    private int nextId;
    
    /**
     * Wird ein Knoten entfernt, wird seine Id an diese Queue angefügt, sodass sie erneut verwendet werden kann.
     */
    private Queue<Integer> freeIds;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public Map<Node, List<Edge>> getAdjList(){
        return this.adjList;
    }

    private int getNextId(){
        return this.nextId;
    }

    private Queue<Integer> getFreeIds(){
        return this.freeIds;
    }

    /**
     * Increments attribute nextId by one.
     */
    private void incNextId(){
        this.nextId++;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////cons
    public Graph(){
        this.adjList = new HashMap<Node, List<Edge>>();
        this.nextId = 0;
        this.freeIds = new PriorityQueue<Integer>();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Fügt dem Graphen einen Knoten mit automatisch ausgewählter Id hinzu.
     * @return  Knoten, der dem Graphen hinzugefügt wurde.
     */
    public Node addNode(){
        if(getFreeIds().isEmpty()){
            int id = getNextId();
            incNextId();
            return addNode(id);
        }else{
            return addNode(getFreeIds().remove());
        }
    }

    /**
     * Fügt dem Graphen einen Knoten mit bestimmter Id hinzu, wenn diese Verfügbar ist.
     * @param pId   Wunsch-Id.
     * @return      Knoten, der dem Graphen hinzugefügt wurde.
     */
    public Node addNodeWithId(int pId){
        if((!getFreeIds().contains(pId))&&pId<getNextId()){
            return null;
        }else{
            return addNode(pId);
        }
    }


    /**
     * Fügt dem Graphen einen Knoten mit bestimmter Id hinzu.
     * @param pId   Id des Knotens.
     * @return      Knoten, der dem Graphen hinzugefügt wurde.
     */
    private Node addNode(int pId){
        Node n = new Node(pId);

        getAdjList().putIfAbsent(n, new ArrayList<>());

        return n;
    }

    /**
     * Fügt Kante zwischen 2 Knoten mit Gewicht pWeight hinzu.
     * @param pId1      Id Knoten 1
     * @param pId2      Id Knoten 2
     * @param pWeight   Gewichtung
     */
    public void addEdge(int pId1, int pId2, int pWeight){

        addDirectedEdge(pId1, pId2, pWeight);
        addDirectedEdge(pId2, pId1, pWeight);
    }

    /**
     * Fügt gerichtete Kante zwischen 2 Knoten mit Gewicht pWeight hinzu.
     * @param pId1      Id Knoten 1
     * @param pId2      Id Knoten 2
     * @param pWeight   Gewichtung
     */
    public void addDirectedEdge(int pId1, int pId2, int pWeight){
        if(pId1==pId2)return;

        Node k = new Node(pId1);
        Edge e = new Edge(pWeight, new Node(pId2));
        if(!getAdjList().get(k).contains(e)){
            getAdjList().get(k).add(e);
        }else{
            getAdjList().get(k).remove(e);
            getAdjList().get(k).add(e);
        }
    }

    /**
     * Entfernt gegbenenfalls Kante zwischen 2 Knoten.
     * @param pId1  Id Knoten 1
     * @param pId2  Id Knoten 2
     */
    public void removeEdge(int pId1, int pId2){
        Node k1 = new Node(pId1);
        Node k2 = new Node(pId2);

        getAdjList().get(k1).remove(new Edge(k2));
        getAdjList().get(k2).remove(new Edge(k1));
    }

    /**
     * Entfernt gegebenenfalls Kante in eine Richtung zwischen 2 Knoten
     * @param pId1  Id Knoten 1
     * @param pId2  Id Knoten 2
     */
    public void removeDirEdge(int pId1, int pId2){
        Node k1 = new Node(pId1);
        Node k2 = new Node(pId2);

        getAdjList().get(k1).remove(new Edge(k2));
    }

    /**
     * Gibt Liste an benachbarten Knoten des Knotens mit der Id zurück.
     * @param pId   Id des Knotens, dessen Nachbarn gesucht werden.
     * @return      Liste mit allen benachbarten Knoten.
     */
    public List<Node> getAdjNodes(int pId) {
        List<Edge> edges = getAdjList().get(new Node(pId));
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < edges.size(); i++){
            nodes.add(edges.get(i).getNode());
        }
        return nodes;
    }

    /**
     * Gibt Liste an entspringenden Kanten des Knotens mit der Id zurück.
     * @param pId   Id des Knotens, dessen Kanten gesucht werden.
     * @return      Liste mit allen ausgehenden Kanten.
     */
    public List<Edge> getOutEdges(int pId) {
        List<Edge> edges = getAdjList().get(new Node(pId));
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String ret = "";

        for (Node n : getAdjList().keySet()) {
            ret += n.getId();
            for (Edge e : getOutEdges(n.getId())) {
                ret += " ["+e.getWeight()+"]> "+e.getNode().getId();
            }
            ret += "\n";
        }
        return ret;
    }

    /**
     * Entfernt Knoten aus dem Graphen
     * @param pId   Id Knoten
     */
    public void removeNode(int pId){
        Node k = new Node(pId);

        getAdjList().values().stream().forEach(e -> e.remove(new Edge(k)));//Entfernt alle Kanten gerichtet auf Knoten k
        getAdjList().remove(k);       

        getFreeIds().add(pId);//gibt Id des gelöschten Knotens frei zur Wiederverwendung
    }

    /**
     * entfernt Kanten von und zu Knoten
     * @param pId   Id Knoten
     */
    public void clearEdges(int pId){
        Node k = new Node(pId);

        getAdjList().get(k).clear();
        getAdjList().values().stream().forEach(e -> e.remove(new Edge(k)));//Entfernt alle Kanten gerichtet auf Knoten k
    }

    /**
     * Überprüft, ob ein bestimmter Knoten über eine gerichtete Kante zu bestimmten anderen Knoten verfügt
     * @param pN1 Id bestimmter Knoten
     * @param pN2 Id anderer bestimmter Knoten
     * @return ob eine gerichtete Kante besteht
     */
    public boolean hasEdgeTo(int pN1, int pN2){
        List<Edge> egs = getAdjList().get(new Node(pN1));

        for (Edge edge : egs) {
            if(edge.getNode().getId()==pN2){
                return true;
            }
        }

        return false;
    }

    /**
     * Überprüft, ob zwischen 2 bestimmten Knoten eine direkte Kante besteht
     * @param pN1 Id Knoten 1
     * @param pN2 Id Knoten 2
     * @return ob eine Kante besteht
     */
    public boolean isEdgeBetween(int pN1, int pN2){
        return (hasEdgeTo(pN1, pN2)||hasEdgeTo(pN2, pN1));
    }

    /**
     * gibt Adjazenzmatrix zurück
     * @return
     */
    public String getAdjMatrix(){
        List<Node> nodes = new ArrayList<Node>(getAdjList().keySet());

        {//17.03.2024 20:33 cool zu wissen, dass das geht
            boolean sorted = false;
            while(!sorted){
                sorted = true;
                for (int i = 0; i < nodes.size()-1; i++) {
                    if(nodes.get(i).getId()>nodes.get(i+1).getId()){
                        Node tmp = nodes.get(i);
                        nodes.set(i, nodes.get(i+1));
                        nodes.set(i+1, tmp);
                        sorted = false;
                    }
                }
            }
        }

        String ret = "";
        for (Node nodeFrom : nodes){
            ret += nodeFrom.getId()+": ";
            for (Node nodeTo : nodes) {
                if(!hasEdgeTo(nodeFrom.getId(), nodeTo.getId())){
                    ret += "X, ";
                }else{
                    List<Edge> egs = getAdjList().get(nodeFrom);
                    for (Edge eg : egs) {
                        if(eg.getNode().getId()==nodeTo.getId()){
                            ret += eg.getWeight()+", ";
                        }
                    }
                }
            }
            ret += "\n";
        }
        return ret;
    }

    /**
     * ob der Graph negativ gewichtete Kanten enthält
     * @return Ergebnis
     */
    public boolean containsNegativeEdges(){
        for(List<Edge> edgeList : getAdjList().values()){
            for (Edge edge : edgeList){
                if(edge.getWeight()<0)
                    return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        Graph compGraph = (Graph) obj;
        return this.getAdjList().equals(compGraph.getAdjList());
    }

    /**
     * Gibt Liste an mündenden, umgekehrten Kanten des Knotens mit der Id zurück.
     * @param pId   Id des Knotens, dessen eingehende Kanten gesucht werden.
     * @return      Liste mit allen ausgehenden Kanten.
     */
    public List<Edge> getRevInEdges(int pId) {
        List<Edge> ret = new LinkedList<Edge>();
        for (Node n : getAdjList().keySet()){
            for (Edge e : getAdjList().get(n)){
                if(e.getNode().getId()==pId){
                    ret.add(new Edge(e.getWeight(),n));
                }
            }
        }
        return ret;
    }
}
