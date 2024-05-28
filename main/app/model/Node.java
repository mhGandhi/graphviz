package app.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Die Node Klasse stellt einen Knoten im Graphen dar.
 */
public class Node implements Serializable{

////////////////////////////////////////////////////////////////////////////////////////////////////attributes
    /**
     * Zahl dient der eindeutigen Identifikation des Knotens.
     */
    private int id;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public int getId(){
        return this.id;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons
    public Node(int pId){
        id = pId;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj){

        Node nd = (Node) obj;
        return id == nd.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
