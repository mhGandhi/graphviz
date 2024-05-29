import app.Controller;
import app.Tool;
import app.model.Edge;
import app.model.Graph;
import app.model.Node;
import app.view.RedrawModes;
import app.view.point.InSysPoint;

import java.util.*;


public class Path implements Tool{

    /**
     * {@inheritDoc}
     * pC.getView().getContentPanel().setEdgeMarked(bool) (+ setDirEdgeMarked(bool))
     */
    @Override
    public String getId(){
        return "aa:dijkPath_91448";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(){
        return "Pfadberechnung";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return "Berechned Pfad yo.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthor(){
        return "Adrian";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showInMenu() {
        return true;
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
        if(pC.getModel().getGraph().containsNegativeEdges())
            return;
        int destination = pC.getView().nodeDialog();
        if(destination<0){
            return;
        }
        pC.removeMarkings();
        Map<Integer,Integer> dists = getDijkDists(pNodeId, pC.getModel().getGraph());

        //Distanzen ersichtlich machen
        //for(int nd : dists.keySet()){
        //    pC.getView().getContentPanel().getNodeById(nd).setLabel(""+dists.get(nd));
        //}

        drawPathByDists(dists, pC, destination);

        pC.getView().redraw(RedrawModes.RESCALE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {

    }

    /**
     *
     * @param pDists Map Id->Distanz zu Start
     * @param pC Controller
     * @param pDestination Zielknoten
     */
    private void drawPathByDists(Map<Integer,Integer> pDists, Controller pC, int pDestination){

        int cur = pDestination;
        while(pDists.get(cur)!=0){
            List<Edge> inEdges = pC.getModel().getGraph().getRevInEdges(cur);
            inEdges.sort(Comparator.comparingInt(Edge::getWeight).reversed());
            for (Edge e : inEdges){
                if(pDists.get(cur)==pDists.get(e.getNode().getId())+e.getWeight()){

                    pC.getView().getContentPanel().getNodeById(cur).setMarked(true);
                    pC.getView().getContentPanel().setEdgeMarked(e.getNode().getId(), cur, true);

                    cur = e.getNode().getId();
                    break;
                }
            }

            pC.getView().getContentPanel().getNodeById(cur).setMarked(true);
        }
    }

    /**
     *
     * @param pStartNode
     * @param pG
     * @return
     */
    private Map<Integer, Integer> getDijkDists(int pStartNode, Graph pG){
        Map<Integer,Integer> dists = new TreeMap<Integer,Integer>();
        for(Node n : pG.getAdjList().keySet()){
            dists.put(n.getId(), Integer.MAX_VALUE);
        }
        Collection<Integer> visited = new LinkedList<Integer>();
        Queue<Integer> next = new LinkedList<Integer>();
        next.add(pStartNode);

        dists.put(next.peek(), 0);

        while(!next.isEmpty()){
            int cur = next.peek();

            List<Edge> outEdges = pG.getOutEdges(cur);
            outEdges.sort(Comparator.comparingInt(Edge::getWeight));

            for(Edge e : outEdges){
                if(((e.getWeight()+dists.get(cur)) < dists.get(e.getNode().getId()))
                        && !visited.contains(e.getNode().getId())){
                    dists.put(e.getNode().getId(), e.getWeight()+dists.get(cur));
                    next.add(e.getNode().getId());
                }
            }

            visited.add(cur);
            next.remove();
        }

        return dists;
    }

}
