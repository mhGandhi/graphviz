import app.view.EdgeComponent;
import app.view.NodeComponent;
import app.view.RedrawModes;
import app.view.View;
import app.view.point.InSysPoint;
import app.Tool;
import app.model.Edge;
import app.model.Graph;
import app.model.Node;

import java.awt.Color;
import java.util.*;

import app.Controller;

public class Tiefensuche implements Tool{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(){
        return "AS:Tiefensuche";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(){
        return "Tiefensuche";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return "Graphtravesierung: Tiefensuche";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthor(){
        return "Alan Emanuel Sitte";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showInMenu() {
        return true; //ob das Tool im Programm angezeigt werden soll
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromMenuBar() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromNode() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromSpot() {
        return false;
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public void runFromMenuBar(Controller pC){
        int n = pC.getView().nodeDialog();
        if(n>=0){
            runOnNode(pC, n);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runOnNode(Controller pC, int pNodeId) {
        depthSearch(pNodeId, pC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {
   
    }

    /**
     *
     * @param pStartNode
     * @param pC
     */
    private void depthSearch(int pStartNode, Controller pC){
        Collection<Integer> visitedNodes = new LinkedList<Integer>();

        depthSearchRec(pStartNode, pC, visitedNodes);
    }

    /**
     *
     * @param pCurrentNode
     * @param pC
     * @param pVisitedNodes
     */
    private void depthSearchRec(int pCurrentNode, Controller pC, Collection<Integer> pVisitedNodes){
        pVisitedNodes.add(pCurrentNode);
        pC.getView().getContentPanel().getNodeById(pCurrentNode).setMarked(true);
        List<Edge> outEdges = pC.getModel().getGraph().getOutEdges(pCurrentNode);

        outEdges.sort(Comparator.comparingInt(Edge::getWeight));

        for(Edge e: outEdges){
            if(!pVisitedNodes.contains(e.getNode().getId())){
                pC.getView().getContentPanel().setEdgeMarked(pCurrentNode, e.getNode().getId(), true);

                depthSearchRec(e.getNode().getId(), pC, pVisitedNodes);
            }
        }
    }
   
   
  /*  
            ******Funktioniert nd ka, aber der untere teil schon*******
   
     private void tiefensuche(int pStart, Controller pC ){
        System.out.println("Startknoten ID:"+ pStart);
        
        List<Integer> unbesucht = new ArrayList<Integer>();
        List<Integer> besucht = new ArrayList<Integer>();

        //put pStart in besucht
        besucht.add(pStart);
       
        //alle anderen nodes in unbesucht
        List<Integer> nodes = pC.getModel().getIds();
        for(int i=0; i<nodes.size(); i++){
        if (nodes.get(i) != pStart) {
            unbesucht.add(nodes.get(i));
         }
        }
    
        //gucke auf neighbors von den zuletzt in "besucht" gepackten node 
        int letzteNodeId = besucht.get(besucht.size() - 1);
        List<Edge> nachbarEdges = pC.getModel().getGraph().getOutEdges(letzteNodeId);   

        //gehe zu dem knoten, wo der pfad am geringsten ist und packe diesen knoten jetzt in besucht und aus unbesucht
        int niedrigstesGewicht = Integer.MAX_VALUE;
            int niedrigsteGewichtKnotenId = -1;

            for (Edge nachbarEdge : nachbarEdges) {
             if (!besucht.contains(nachbarEdge.getNode().getId())) {
             if (nachbarEdge.getWeight() < niedrigstesGewicht) {
              niedrigstesGewicht = nachbarEdge.getWeight();
                 niedrigsteGewichtKnotenId = nachbarEdge.getNode().getId();
                 }
                }
               }

            // Füge den Knoten mit der niedrigsten Gewichtung zur Liste der besuchten Knoten hinzu
            if (niedrigsteGewichtKnotenId != -1) {
                 besucht.add(niedrigsteGewichtKnotenId);
                
                 for (Integer nodeId : besucht) {
                    NodeComponent node = pC.getView().getContentPanel().getNodeById(nodeId);
                    node.setMarked(true); 
                }

            }

            // Entferne den Knoten mit der niedrigsten Gewichtung aus der Liste der unbesuchten Knoten
            if (niedrigsteGewichtKnotenId != -1) {
                unbesucht.remove(Integer.valueOf(niedrigsteGewichtKnotenId));
}
        

        // wenn keine nachbarn, dann zurück zum letzten knoten, wo ein knoten ein nachbar hatte, wo wir nicht hingegangen sind
        boolean unbesuchteNachbarnVorhanden = false;
        for (Edge nachbarEdge : nachbarEdges) {
            if (!besucht.contains(nachbarEdge.getNode().getId()) && unbesucht.contains(nachbarEdge.getNode().getId())) {
                unbesuchteNachbarnVorhanden = true;
                break;
            }
        }
        
        if (!unbesuchteNachbarnVorhanden) {
            //knoten mut unbesuchten nachbarn suchen
            boolean knotenGefunden = false;
            for (Integer knotenId : besucht) {
                List<Node> nachbarnDesKnotens = pC.getModel().getGraph().getAdjNodes(knotenId);
                for (Node nachbar : nachbarnDesKnotens) {
                    if (unbesucht.contains(nachbar.getId())) {
                        besucht.add(nachbar.getId());
                        unbesucht.remove(nachbar.getId());
                        knotenGefunden = true;
                        break;
                    }
                }
                //wenn ein knoten mit unbesuchten nachbarn gefunden wurde, dann aufhören, wenn nicht, dann weitermachen
                if (knotenGefunden) {
                    break;
                }
                    else{
                        continue;
                    }
            }
        }
       //macht des gleiche wie oben, sozusagen nur nach dem nachbar mit dem geringsten pfad suchen und den dann aus der liste von "unbesucht" rausmachen
        for (Edge nachbarEdge : nachbarEdges) {
            if (!besucht.contains(nachbarEdge.getNode().getId())) {
            if (nachbarEdge.getWeight() < niedrigstesGewicht) {
             niedrigstesGewicht = nachbarEdge.getWeight();
                niedrigsteGewichtKnotenId = nachbarEdge.getNode().getId();
                }
               }
              }
              if (niedrigsteGewichtKnotenId != -1) {
                besucht.add(niedrigsteGewichtKnotenId);
   
             }

             //damit der algorithmus rekursiv is
             tiefensucheRekursiv(pStart, pC, unbesucht, besucht);

            // färbe die besuchten knoten als grün und die unbesuchten knoten als rot ein
            for (Integer nodeId : besucht) {
                NodeComponent node = pC.getView().getContentPanel().getNodeById(nodeId);
                node.setMarked(true); 
            }



            for (Integer nodeId : unbesucht) {
                NodeComponent node = pC.getView().getContentPanel().getNodeById(nodeId);
                node.setMarked(false); 
            }

            pC.getView().redraw(RedrawModes.RESCALE);



 }

 //macht die teiefensuche rekursiv
 private void tiefensucheRekursiv(int currentNode, Controller pC, List<Integer> unbesucht, List<Integer> besucht) {
    System.out.println("Besuche Knoten ID: " + currentNode);

    // Füge den aktuellen Knoten zu den besuchten Knoten hinzu und entferne ihn aus den unbesuchten Knoten
    besucht.add(currentNode);
    unbesucht.remove(Integer.valueOf(currentNode));

    // Hole die Nachbarn des aktuellen Knotens
    List<Edge> nachbarEdges = pC.getModel().getGraph().getOutEdges(currentNode);

    // Durchlaufe die Nachbarn
    for (Edge nachbarEdge : nachbarEdges) {
        int nachbarId = nachbarEdge.getNode().getId();
        if (unbesucht.contains(nachbarId)) {
            // Führe die Tiefensuche rekursiv für jeden unbesuchten Nachbarn durch
            tiefensucheRekursiv(nachbarId, pC, unbesucht, besucht);
        }
    }
 }

    private EdgeComponent getConnectingEdge(int pNodeId1, int pNodeId2, List <EdgeComponent> pEdges  ){

        for(EdgeComponent eC:pEdges){
            if(eC.getFrom().getId()==pNodeId1&&eC.getTo().getId()==pNodeId2)
                return eC;

        }
        return null;
    }
    */     

    @Deprecated
    private void tiefensuche(int pStart, Controller pC) {
        System.out.println("Startknoten ID:" + pStart);
        
        List<Integer> unbesucht = new ArrayList<Integer>();
        List<Integer> besucht = new ArrayList<Integer>();
    
        // Startknoten zu den besuchten Knoten hinzufügen
        besucht.add(pStart);
       
        // Alle anderen Knoten zu den unbesuchten Knoten hinzufügen
        List<Integer> nodes = pC.getModel().getIds();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) != pStart) {
                unbesucht.add(nodes.get(i));
            }
        }
    
        // Führe die Tiefensuche rekursiv aus
        tiefensucheRekursiv(pStart, pC, unbesucht, besucht);
    
        // Markiere besuchte Knoten als grün und unbesuchte als rot
        for (Integer nodeId : besucht) {
            NodeComponent node = pC.getView().getContentPanel().getNodeById(nodeId);
            node.setMarked(true); 
        }
    
        for (Integer nodeId : unbesucht) {
            NodeComponent node = pC.getView().getContentPanel().getNodeById(nodeId);
            node.setMarked(false); 
        }
    
        pC.getView().redraw(RedrawModes.RESCALE);
    }
    
    // Macht die Tiefensuche rekursiv
    @Deprecated
    private void tiefensucheRekursiv(int currentNode, Controller pC, List<Integer> unbesucht, List<Integer> besucht) {
        System.out.println("Besuche Knoten ID: " + currentNode);
    
        // Füge den aktuellen Knoten zu den besuchten Knoten hinzu und entferne ihn aus den unbesuchten Knoten
        besucht.add(currentNode);
        unbesucht.remove(Integer.valueOf(currentNode));
    
        // Hole die Nachbarn des aktuellen Knotens
        List<Edge> nachbarEdges = pC.getModel().getGraph().getOutEdges(currentNode);
    
        // Durchlaufe die Nachbarn
        for (Edge nachbarEdge : nachbarEdges) {
            int nachbarId = nachbarEdge.getNode().getId();
            if (unbesucht.contains(nachbarId)) {
                // Führe die Tiefensuche rekursiv für jeden unbesuchten Nachbarn durch
                tiefensucheRekursiv(nachbarId, pC, unbesucht, besucht);
            }
        }

    }
    













}