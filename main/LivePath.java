import app.Controller;
import app.Tool;
import app.model.Edge;
import app.view.RedrawModes;
import app.view.Theme;
import app.view.point.InSysPoint;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LivePath implements Tool{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(){
        return "aa:LivePfad";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(){
        return "Live-Pfad";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return "Sucht Live weg zwischen 2 Knoten (mit Dijkstra-Algorithmus). Geschwindigkeit kann angepasst werden.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthor(){
        return "Adrian Akipi";
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
        return false;
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

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runOnNode(Controller pC, int pNodeId) {
        if(pC.getModel().getGraph().containsNegativeEdges())
            pC.getView().nativeNotification("Dijkstra funktioniert nicht mit negativen Kanten");
        int destination = pC.getView().nodeDialog();
        if(destination<0){
            pC.getView().nativeNotification("Kein Zielknoten ausgewählt");
            return;
        }
        pC.removeMarkings();
        Path pt = new Path(pNodeId, pC, destination, 1000);
        pt.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {
   
    }

    private static class Path extends Thread{
        private final int startNode;
        private final Controller controller;

        private final int destinationNode;

        private final int delayMod;

        public Path(int pStart, Controller pC, int pDestination, int pDelayMod){
            this.startNode = pStart;
            this.controller = pC;
            this.destinationNode = pDestination;
            this.delayMod = pDelayMod;
        }

        @Override
        public void run() {
            Map<Integer,Integer> dists = dijkDists(startNode, controller, destinationNode);

            if(dists.get(destinationNode)==null){
                controller.getView().nativeNotification("Kein Pfad zum Ziel");
                return;
            }

            //schick blinken
            {
                Collection<Integer> visitedNodes = dists.keySet();
                Map<Integer, Color> markCol = new TreeMap<>();
                Map<Integer, String> col = new TreeMap<>();
                for (int n : visitedNodes) {
                    markCol.put(n, controller.getView().getContentPanel().getNodeById(n).getColor());
                    col.put(n, controller.getModel().getNodePresentation(n).getColorCode());
                }
                for (int i = 0; i < 4; i++) {
                    for (int n : visitedNodes) {
                        Color color = null;
                        if (col.get(n) != null) {
                            color = Theme.decodeColor(col.get(n));
                        } else {
                            color = controller.getView().getViewState().getTheme().getDefaultNodeColor();
                        }
                        controller.getView().getContentPanel().getNodeById(n).setColor(color);
                    }
                    updateViz(0.5d);
                    for (int n : visitedNodes) {
                        controller.getView().getContentPanel().getNodeById(n).setColor(markCol.get(n));
                    }
                    updateViz(0.5d);
                }
            }

            controller.getView().redraw(RedrawModes.RESCALE);
        }

        /**
         *
         * @param pStartNode Anfangsknoten
         * @param pC Controller
         */
        private Map<Integer,Integer> dijkDists(int pStartNode, Controller pC, int pDestinationNode){
            Map<Integer,Integer> dists = new TreeMap<Integer,Integer>();
            Collection<Integer> done = new LinkedList<Integer>();
            List<Integer> toDo = new LinkedList<Integer>();

            toDo.add(pStartNode);
            dists.put(pStartNode, 0);


            while(!toDo.isEmpty()){
                int cur = toDo.get(0);
                {
                    int min = Integer.MAX_VALUE;
                    for(int id:toDo){
                        int dist = Objects.requireNonNullElse(dists.get(id), Integer.MAX_VALUE);
                        if(dist<min && !done.contains(id)){
                            cur = id;
                            min = dist;
                        }
                    }
                    toDo.remove(Integer.valueOf(cur));
                }
                if(done.contains(cur)){
                    break;
                }

                pC.getView().getContentPanel().getNodeById(cur).setColor(Color.WHITE);
                pC.getView().getContentPanel().getNodeById(cur).setMarked(true);
                updateViz(2d);

                List<Edge> outEdges = pC.getModel().getGraph().getOutEdges(cur);
                outEdges.sort(Comparator.comparingInt(Edge::getWeight));

                for(Edge e : outEdges){
                    int neighbour = e.getNode().getId();
                    if(!done.contains(neighbour)){
                        int newDist = e.getWeight()+dists.get(cur);

                        pC.getView().getContentPanel().setEdgeMarked(cur, neighbour, true);
                        pC.getView().getContentPanel().getNodeById(neighbour).setMarked(true);
                        {
                            int dist = Objects.requireNonNullElse(dists.get(neighbour), Integer.MAX_VALUE);
                            pC.getView().getContentPanel().getNodeById(neighbour).setLabel(
                                    newDist+" < "+ (dist==Integer.MAX_VALUE ? "∞" : dist) +" ?"
                            );
                        }
                        updateViz(1.5d);
                        pC.getView().getContentPanel().setEdgeMarked(cur, neighbour, false);
                        pC.getView().getContentPanel().getNodeById(neighbour).setMarked(false);

                        if(newDist < Objects.requireNonNullElse(dists.get(neighbour), Integer.MAX_VALUE)){
                            dists.put(neighbour, newDist);
                            pC.getView().getContentPanel().getNodeById(neighbour).setColor(Color.BLUE);
                            pC.getView().getContentPanel().getNodeById(neighbour).setLabel(newDist+"");
                        }else{
                            pC.getView().getContentPanel().getNodeById(neighbour).setLabel(dists.get(neighbour)+"");
                        }

                        {
                            boolean deadEnd = true;
                            //System.out.println("\n checking if "+ e.getNode().getId()+ " is deadEnd");
                            for (Edge check:pC.getModel().getGraph().getOutEdges(neighbour) ){
                                if(!done.contains(check.getNode().getId()) && check.getNode().getId()!=cur){
                                    //System.out.println(e.getNode().getId() + " wegen " + check.getNode().getId() + " kein deadEnd");
                                    deadEnd = false;
                                    break;
                                }
                            }
                            //System.out.println(e.getNode().getId()+" deadend "+deadEnd);
                            if(deadEnd){
                                pC.getView().getContentPanel().getNodeById(neighbour).setColor(Color.GREEN);
                                done.add(neighbour);
                            }
                        }

                        //updateViz(1d);
                        if(!done.contains(neighbour)&&!toDo.contains(neighbour)){
                            toDo.add(neighbour);
                        }
                    }

                }

                done.add(cur);
                pC.getView().getContentPanel().getNodeById(cur).setColor(Color.GREEN);
                pC.getView().getContentPanel().getNodeById(cur).setMarked(false);
            }

            return dists;
        }

        private void updateViz(double pDuration){
            if(this.delayMod == 0)return;
            this.controller.getView().redraw(RedrawModes.RESCALE);
            try {
                Thread.sleep((int)(pDuration*this.delayMod));
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }
        }

    }
}