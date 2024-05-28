package app.model;

import java.io.Serializable;

/**
 * Die Edge-Klasse stellt eine Kante im Graphen dar.
 */
public class Edge implements Serializable{

////////////////////////////////////////////////////////////////////////////////////////////////////attributes

    /**
     * Gewichtung der Kante
     */
    private int weight;

    /**
     * Knoten, auf den die Kante zeigt
     */
    private Node node;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set    
    public int getWeight(){
        return this.weight;
    }
    public void setWeight(int pWeight){
        this.weight = pWeight;
    }
    public Node getNode(){
        return this.node;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons 
    /**
     * 
     * @param pNode Zielknoten
     */
    public Edge(Node pNode){
        this(1, pNode);
    }

    /**
     * 
     * @param pWeight Gewichtung
     * @param pNode Zielknoten
     */
    public Edge(int pWeight, Node pNode){
        this.node = pNode;
        setWeight(pWeight);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        Edge e = (Edge) obj;
        return this.getNode().equals(e.getNode()); //Gewichtung bei überprüfung auf gleichheit irrelevant
    }
}
