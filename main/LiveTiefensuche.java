import app.Controller;
import app.Tool;
import app.model.Edge;
import app.view.RedrawModes;
import app.view.Theme;
import app.view.point.InSysPoint;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LiveTiefensuche implements Tool{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(){
        return "aa:LiveTiefensuche";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(){
        return "Live-Tiefensuche";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return "Traversiert Graph Live (Tiefensuche)";
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
        pC.removeMarkings();
        pC.getView().getContentPanel().getNodeById(pNodeId).setLabel("START");
        pC.getView().redraw(RedrawModes.RESCALE);
        DepthSearch dp = new DepthSearch(pNodeId, pC);
        dp.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {
   
    }

    private static class DepthSearch extends Thread{
        private final int startNode;
        private final Controller controller;

        public DepthSearch(int pStart, Controller pC){
            this.startNode = pStart;
            this.controller = pC;
        }

        @Override
        public void run() {
            depthSearch(startNode, controller);
        }

        /**
         *
         * @param pStartNode Anfangsknoten
         * @param pC Controller
         */
        private void depthSearch(int pStartNode, Controller pC){
            Collection<Integer> visitedNodes = new LinkedList<Integer>();
            depthSearchRec(pStartNode, pC, visitedNodes);

            //schick blinken
            {
                Map<Integer, Color> markCol = new TreeMap<>();
                Map<Integer, String> col = new TreeMap<>();
                for (int n : visitedNodes) {
                    markCol.put(n, pC.getView().getContentPanel().getNodeById(n).getColor());
                    col.put(n, pC.getModel().getNodePresentation(n).getColorCode());
                }
                for (int i = 0; i < 4; i++) {
                    for (int n : visitedNodes) {
                        Color color = null;
                        if (col.get(n) != null) {
                            color = Theme.decodeColor(col.get(n));
                        } else {
                            color = pC.getView().getViewState().getTheme().getDefaultNodeColor();
                        }
                        pC.getView().getContentPanel().getNodeById(n).setColor(color);
                    }
                    pC.getView().redraw(RedrawModes.NONE);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        //throw new RuntimeException(e);
                    }
                    for (int n : visitedNodes) {
                        pC.getView().getContentPanel().getNodeById(n).setColor(markCol.get(n));
                    }
                    pC.getView().redraw(RedrawModes.NONE);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        //throw new RuntimeException(e);
                    }
                }
            }
        }

        /**
         *
         * @param pCurrentNode aktueller Knoten
         * @param pC Controller
         * @param pVisitedNodes Liste besuchter Knoten
         */
        private void depthSearchRec(int pCurrentNode, Controller pC, Collection<Integer> pVisitedNodes){
            pVisitedNodes.add(pCurrentNode);
            pC.getView().getContentPanel().getNodeById(pCurrentNode).setColor(Color.GREEN);
            pC.getView().getContentPanel().getNodeById(pCurrentNode).setMarked(true);
            List<Edge> outEdges = pC.getModel().getGraph().getOutEdges(pCurrentNode);
            pC.getView().redraw(RedrawModes.NONE);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }

            outEdges.sort(Comparator.comparingInt(Edge::getWeight));

            boolean end = true;
            for(Edge e: outEdges){
                if(!pVisitedNodes.contains(e.getNode().getId())){
                    end = false;
                    pC.getView().getContentPanel().setEdgeMarked(pCurrentNode, e.getNode().getId(), true);

                    depthSearchRec(e.getNode().getId(), pC, pVisitedNodes);
                }
            }
            if(end){
                pC.getView().getContentPanel().getNodeById(pCurrentNode).setColor(Color.RED);
            }
        }
    }
}