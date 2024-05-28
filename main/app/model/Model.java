package app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stellt im MVC-Modell den Controller dar und dient der allumfassenden Definition der Darstellung eines Graphen.
 */
public class Model implements Serializable{
////////////////////////////////////////////////////////////////////////////////////////////////////attributes
    /**
     * Definition der Knoten und Kanten
     */
    Graph graph;
    /**
     * Assosiation der IDs mit ihren zugehörigen Knoten.
     */
    Map<Integer, Node> nodes;//todo unnötig?
    /**
     * Assosiation der IDs mit ihren zugehörigen Knotendarstellungen.
     */
    Map<Integer, NodePresentation> nodePres;
    /**
     * Liste der IDs aller Knoten im Graphen dient als Register
     */
    List<Integer> ids;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public Graph getGraph(){
        return this.graph;
    }
    public Map<Integer, Node> getNodes(){
        return this.nodes;
    }
    public Map<Integer, NodePresentation> getNodePresentations(){
        return this.nodePres;
    }
    public List<Integer> getIds(){
        return this.ids;
    }

    public Node getNode(int pId){
        return getNodes().get(pId);
    }
    public NodePresentation getNodePresentation(int pId){
        return getNodePresentations().get(pId);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons
    public Model(){
        this.graph = new Graph();
        this.nodes = new TreeMap<Integer, Node>();
        this.nodePres = new TreeMap<Integer, NodePresentation>();
        this.ids = new ArrayList<Integer>();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fügt dem Modell einen Knoten hinzu.
     * @return Id des neuen Knotens
     */
    public int addNode(){
        return addNode(getGraph().addNode());
    }

    /**
     * Fügt dem Modell einen Knoten hinzu.
     * @param pId gewünschte Id
     * @return Id des neuen Knotens
     */
    public int addNode(int pId){
        return addNode(getGraph().addNodeWithId(pId));
    }

    /**
     * Fügt dem Modell einen Knoten hinzu.
     * @param pNode
     * @return Id des neuen Knotens
     */
    private int addNode(Node pNode){
        int id = pNode.getId();
        NodePresentation nPres = new NodePresentation(id); //hinzufügen der Präsentation
        getNodes().putIfAbsent(id, pNode); //hinzufügen der direkten Referenz zum Knoten
        getNodePresentations().putIfAbsent(id, nPres); 
        getIds().add(id); //hinzufügen der Id zum Knotenregister

        return id;
    }

    /**
     * Entfernt Knoten aus dem Graphen
     * @param pId Id des zu entfernenden Knoten
     */
    public void removeNode(int pId){
        getGraph().removeNode(pId);
        getNodes().remove(pId);
        getNodePresentations().remove(pId);
        getIds().remove((Integer)pId);
    }

    /**
     * ändert NodePresentation von bestimmtem Knoten entsprechend den Parametern
     * @param pId Id der zu ändernden Präsentation
     * @param pX Neue X-Koordinate
     * @param pY Neue Y-Koordinate
     * @param pLabel Neues Label
     * @param pColorCode Neue Farbe
     */
    public void saveNodePres(int pId, double pX, double pY, String pLabel, String pColorCode){
        if(!getIds().contains(Integer.valueOf(pId))){
            throw new IllegalArgumentException("Node does not seem to exist");
        }
        NodePresentation np = getNodePresentation(pId);
        np.setPosX(pX);
        np.setPosY(pY);
        np.setLabel(pLabel);
        np.setColorCode(pColorCode);
    }

    /**
     * Gibt zurück, ob der Graph mit den aktuellen Kantengewichten als Gewichtet gelten sollte
     * Beim Berechnen von Distanzen zu beachten
     * @return wahr, wenn alle Kanten die selbe Gewichtung haben
     */
    public boolean isWeighted(){
        boolean firstIteration = true;
        int weightInPriorIteration = 0;

        for (Integer node : getIds()) {
            for (Edge edge : getGraph().getOutEdges(node)){
                if(firstIteration){
                    firstIteration = false;
                }else{
                    if(weightInPriorIteration!=edge.getWeight()){
                        return false;
                    }
                }
                weightInPriorIteration = edge.getWeight();
            }
        }
        return true;
    }

    /**
     * Entfernt jegliche Kanten von Knoten
     * @param pId Id Knoten 1
     */
    public void removeEdges(int pId) {
        getGraph().clearEdges(pId);
    }

    /**
     * Ändert Position der Knotenpräsentation entsprechend den Parametern, ohne alle Attribute ändern zu müssen
     * @param pId   Id des betreffenden Knotens
     * @param pX    neue Position X
     * @param pY    neue Position Y
     */
    public void syncNodePos(int pId, double pX, double pY) {
        if(!getIds().contains(Integer.valueOf(pId))){
            throw new IllegalArgumentException("Node does not seem to exist");
        }
        NodePresentation np = getNodePresentation(pId);
        np.setPosX(pX);
        np.setPosY(pY);
    }

    /**
     * Fügt anderes Modell an Aktuelles an
     * @param pModel Amzuhängendes model
     */
    public void appendModel(Model pModel){
        //Zuordnung der Ids im alten Modell zu denen im Neuen
        Map<Integer,Integer> transferedNodes = new HashMap<Integer,Integer>();
        for (Integer oldId : pModel.getIds()){//für jede Id im alten Modell
            int newId = this.addNode();//Knoten in aktuellem Modell
            transferedNodes.put(oldId,newId);//Id von diesem wird der alten zugeordnet

            //Präsentation wird übertragen
            NodePresentation oPres = pModel.getNodePresentation(oldId);
            this.saveNodePres(newId, oPres.getPosX(), oPres.getPosY(), oPres.getLabel(), oPres.getColorCode());
        }

        //die Kanten der alten Knoten werden jeweils auf die neuen übertragen
        for (Integer oldId : pModel.getIds()) {
            int newId = transferedNodes.get(oldId);
            List<Edge> oldEgs = pModel.getGraph().getAdjList().get(pModel.getNode(oldId));

            for (Edge edge : oldEgs) {//jede Kante wird neu erstellt
                this.getGraph().addDirectedEdge(newId, transferedNodes.get(edge.getNode().getId()), edge.getWeight());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        Model compMod = (Model) obj;
        
        return  this.getIds().equals(compMod.getIds())
                &&this.getNodes().equals(compMod.getNodes())
                &&this.getNodePresentations().equals(compMod.getNodePresentations())
                &&this.getGraph().equals(compMod.getGraph());
    }
}

