//PSEUDO CODE:
/*
markiere alle Knoten als nicht besucht yk

markiere den Startknoten als besucht
füge den Startknoten in eine (zunächst leere) Schlange s ein 

solange s nicht leer ist wiederhole

entnenme den vordersten knoten < aus s (Schlange)

für alle Nachbarknoten n von k wiederhole (amk)
falls n als nicht besucht markiert ist dann
markiere n als besucht
füge n nun in s ein
ende falls
ende wiederhole
ende wiederhole amk
*/


import java.util.*;


import app.ActionHandler;
import app.Controller;
import app.Tool;
import app.model.Edge;
import app.model.Graph;
import app.model.Node;
import app.view.ContentPanel;
import app.view.EdgeComponent;
import app.view.NodeComponent;
import app.view.RedrawModes;
import app.view.point.InSysPoint;
/*
// klasse für einen Knoten im Graphen
class Node {
    private boolean visited;
    private List<Node> neighbors;

    public Node() {
        this.visited = false;
        this.neighbors = new ArrayList<>();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Node neighbor) {
        this.neighbors.add(neighbor);
    }
}

// klasse für einen Graphen
class Graph {
    private List<Node> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }
}
*/
// Beispieltool, das Breitensuche implementiert amk
public class Breitensuche implements Tool {

    // Breitensuche Algorithmus
    @Deprecated
    public void breitensuche(int pStart, Controller pC) {

        // Markiere den Startknoten als besucht und füge ihn in die Warteschlange ein
        Queue<Integer> queue = new LinkedList<>();
        queue.add(pStart);

        while (!queue.isEmpty()) {
            // Entnehme den vordersten Knoten aus der Warteschlange
            int currentNode = queue.poll();
            System.out.println("Besucht: " + currentNode);

            // Markiere den aktuellen Knoten als besucht
            NodeComponent node = pC.getView().getContentPanel().getNodeById(currentNode);
            EdgeComponent edge = pC.getView().getContentPanel().getEdges().get(currentNode);
            node.setMarked(true);
            edge.setMarked(true);

            // Hole die Nachbarn des aktuellen Knotens
            List<Node> adjNodes = pC.getModel().getGraph().getAdjNodes(currentNode);

            // Durchlaufe die Nachbarn und füge sie zur Warteschlange hinzu, wenn sie noch nicht besucht wurden
            for (Node adjNode : adjNodes) {
                if (!nodeIsVisited(adjNode.getId(), pC)) {
                    queue.add(adjNode.getId());
                    // Färbe den Nachbarknoten grün und füge ihn zur Liste der besuchten Knoten hinzu
                    NodeComponent adjNodeComponent = pC.getView().getContentPanel().getNodeById(adjNode.getId());
                    EdgeComponent adjEdge = pC.getView().getContentPanel().getEdges().get(adjNode.getId());
                    adjNodeComponent.setMarked(true);
                    adjEdge.setMarked(true);
                }
            }
        }
    }

    // Überprüfe, ob ein Knoten bereits besucht wurde
    @Deprecated
    private boolean nodeIsVisited(int nodeId, Controller pC) {
        NodeComponent node = pC.getView().getContentPanel().getNodeById(nodeId);
        return node.isMarked();
    }

    @Deprecated
    private boolean edgeIsVisited(int nodeId, Controller pC) {
        EdgeComponent edge = pC.getView().getContentPanel().getEdges().get(nodeId);
        return edge.isMarked();
    }



    @Override
    public String getId(){
        return "an:Breitensuche";
    }

    public void runFromMenuBar(Controller pC){
        int n = pC.getView().nodeDialog();
        if(n>=0){
            runOnNode(pC, n);
        }
    }


    @Override
    public String getName(){
        return "Breitensuche";
    }
    
    public String getAuthor(){
        return "Shanh";
    }

    @Override
    public String getDescription(){
        return "Breitensuche amk.";
    }

    @Override
    public boolean showInMenu() {
        return true; //ob das Tool im Programm angezeigt werden soll
    }

    @Override
    public boolean canBeRanFromMenuBar() {
        return true;
    }

    @Override
    public boolean canBeRanFromNode() {
        return true;
    }

    @Override
    public boolean canBeRanFromSpot() {
        return false;
    }

   // @Override
    
    // public void run(Controller pC){
        //vtl noch mit selection
   // }

    @Override
    public void runOnNode(Controller pC, int pNodeId) {
        broadSearch(pNodeId, pC);
        pC.getView().redraw(RedrawModes.RESCALE);
    }

    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {
        //
    }

    /**
     *
     * @param pStartNode Anfangspunkt
     * @param pC Controller
     */
    public void broadSearch(int pStartNode, Controller pC){
        Collection<Integer> visitedNodes = new LinkedList<Integer>();
        Queue<Integer> nextToVisit = new LinkedList<>();

        nextToVisit.add(pStartNode);
        //int count = 0;
        while(!nextToVisit.isEmpty()){
            int currentNode = nextToVisit.remove();

            visitedNodes.add(currentNode);
            pC.getView().getContentPanel().getNodeById(currentNode).setMarked(true);
            //pC.getView().getContentPanel().getNodeById(currentNode).setLabel(count+"");
            //count++;

            List<Edge> outEdges = pC.getModel().getGraph().getOutEdges(currentNode);
            outEdges.sort(Comparator.comparingInt(Edge::getWeight));

            for (Edge e : outEdges){
                if(!visitedNodes.contains(e.getNode().getId()) && !nextToVisit.contains(e.getNode().getId())){
                    pC.getView().getContentPanel().setEdgeMarked(currentNode, e.getNode().getId(), true);
                    nextToVisit.add(e.getNode().getId());
                }
            }
        }
    }
}


//HAB ALLES ICH GEMACHT kappa