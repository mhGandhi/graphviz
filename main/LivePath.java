import app.Controller;
import app.Tool;
import app.model.Edge;
import app.view.NodeComponent;
import app.view.RedrawModes;
import app.view.Theme;
import app.view.point.InSysPoint;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LivePath implements Tool{

    /**
     * Modifikator, um welchen die Verzögerungen in der Animation verändert werden.
     * 0 um Animation auszuschalten.
     */
    private final int delay = 50;

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
        return "Sucht Live weg zwischen 2 Knoten (mit Dijkstra-Algorithmus)." +
                "\nGeschwindigkeit ist anpassbar; aktuell "+delay+"%ige Verzögerung zwischen Schritten";
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
        if(pC.getModel().getGraph().containsNegativeEdges()){
            pC.getView().nativeNotification("Dijkstra funktioniert nicht mit negativen Kanten");
            return;
        }

        int destination = pC.getView().nodeDialog();
        if(destination<0){
            pC.getView().nativeNotification("Kein Zielknoten ausgewählt");
            return;
        }
        pC.removeMarkings();
        Path pt = new Path(pNodeId, pC, destination, delay);
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
            Map<Integer,Integer> dists = dijkDists(startNode, controller);

            if(dists.get(destinationNode)==null){
                controller.getView().nativeNotification("Kein Pfad zum Ziel");
                return;
            }

            {//schick blinken
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
            }

            Stack<Integer> path = new Stack<Integer>();
            {//Weg finden
                int cur = destinationNode;
                path.add(cur);
                {//MARK
                    controller.getView().getContentPanel().getNodeById(cur).setMarked(true);
                    updateViz(2.5d);
                }

                int last = -1;
                while(cur != startNode){
                    List<Edge> revInEdges = controller.getModel().getGraph().getRevInEdges(cur);
                    revInEdges.sort(Comparator.comparingInt(Edge::getWeight).reversed());

                    for(Edge ie: revInEdges){
                        if(ie.getNode().getId()!=last && dists.get(ie.getNode().getId())<=dists.get(cur)){
                            {//MARK
                                controller.getView().getContentPanel().setEdgeMarked(ie.getNode().getId(), cur, true);
                                controller.getView().getContentPanel().getNodeById(ie.getNode().getId()).setMarked(true);
                            }
                            if(dists.get(ie.getNode().getId()) + ie.getWeight() == dists.get(cur)){
                                {//MARK
                                    controller.getView().getContentPanel().getNodeById(ie.getNode().getId()).setLabel(
                                            dists.get(ie.getNode().getId())+" + "+ie.getWeight()+" = "+dists.get(cur)
                                    );
                                    updateViz(2.5d);
                                }
                                last = cur;
                                cur = ie.getNode().getId();
                                path.add(cur);
                                break;
                            }else{
                                {//MARK
                                    controller.getView().getContentPanel().getNodeById(ie.getNode().getId()).setLabel(
                                            dists.get(ie.getNode().getId())+" + "+ie.getWeight()+" ≠ "+dists.get(cur)
                                    );
                                    updateViz(2.5d);
                                    controller.getView().getContentPanel().setEdgeMarked(ie.getNode().getId(), cur,false);
                                    controller.getView().getContentPanel().getNodeById(ie.getNode().getId()).setMarked(false);
                                }
                            }
                        }
                    }
                }
            }

            controller.modelToComponentList();

            {//Pfad markieren
                int lastNode = -1;
                while(!path.isEmpty()){
                    int currentNode = path.pop();
                    if(lastNode>=0){
                        controller.getView().getContentPanel().setEdgeMarked(lastNode, currentNode,true);
                    }
                    controller.getView().getContentPanel().getNodeById(currentNode).setMarked(true);
                    lastNode = currentNode;
                    updateViz(1.0d);
                }
            }

            controller.getView().redraw(RedrawModes.RESCALE);
        }

        /**
         * Errechnet mindeste Distanz zum Startknoten mithilfe des Dijkstra-Algorithms
         * @param pStartNode Anfangsknoten
         * @param pC Controller
         * @return Zuordnung der Knoten-Ids zu ihren entsprechenden Distanzen
         */
        private Map<Integer,Integer> dijkDists(int pStartNode, Controller pC){//todo modus der nur mindest erforderliche distanzen errechnet???
            Map<Integer,Integer> dists = new TreeMap<Integer,Integer>();
            Collection<Integer> done = new LinkedList<Integer>();
            List<Integer> toDo = new LinkedList<Integer>();

            toDo.add(pStartNode);
            dists.put(pStartNode, 0);

            while(!toDo.isEmpty()){
                int cur = toDo.get(0);
                {//setze cur auf den Knoten mit der geringsten Distanz zum Start
                    int min = Integer.MAX_VALUE;
                    for(int id:toDo){
                        int dist = Objects.requireNonNullElse(dists.get(id), Integer.MAX_VALUE);
                        if(dist<min && !done.contains(id)){
                            cur = id;
                            min = dist;
                        }
                    }
                    toDo.remove(Integer.valueOf(cur));
                    if(done.contains(cur)){//wenn alle fertig abbruch
                        break;
                    }
                }

                {//MARK aktuell bearbeiteten Knoten markieren
                    pC.getView().getContentPanel().getNodeById(cur).setColor(Color.WHITE);
                    pC.getView().getContentPanel().getNodeById(cur).setMarked(true);
                    updateViz(2d);
                }

                List<Edge> outEdges = pC.getModel().getGraph().getOutEdges(cur);
                outEdges.sort(Comparator.comparingInt(Edge::getWeight));
                for(Edge e : outEdges){//für jede ausgehende Kante des aktuellen Knotens (aufsteigend nach Gewicht)
                    int neighbour = e.getNode().getId();

                    if(!done.contains(neighbour)){//insofern noch nicht bearbeitet
                        //Distanz des Nachbarn zum Start, wenn über aktuellen Knoten gegangen wird
                        int newDist = e.getWeight()+dists.get(cur);

                        {//MARK aktuell untersuchten Nachbar markieren
                            pC.getView().getContentPanel().setEdgeMarked(cur, neighbour, true);
                            pC.getView().getContentPanel().getNodeById(neighbour).setMarked(true);
                            int dist = Objects.requireNonNullElse(dists.get(neighbour), Integer.MAX_VALUE);
                            pC.getView().getContentPanel().getNodeById(neighbour).setLabel(
                                    newDist+" < "+ (dist==Integer.MAX_VALUE ? "∞" : dist) +" ?"
                            );
                            updateViz(1.5d);
                            //Markierungen schonmal wieder entfernen
                            pC.getView().getContentPanel().setEdgeMarked(cur, neighbour, false);
                            pC.getView().getContentPanel().getNodeById(neighbour).setMarked(false);
                        }

                        //wenn neue Distanz kleiner als bisher berechnete
                        if(newDist < Objects.requireNonNullElse(dists.get(neighbour), Integer.MAX_VALUE)){
                            dists.put(neighbour, newDist);//Distanz aktualisieren
                            {//MARK mit neuer Distanz markieren
                                pC.getView().getContentPanel().getNodeById(neighbour).setColor(Color.BLUE);
                                pC.getView().getContentPanel().getNodeById(neighbour).setLabel(newDist+"");
                            }
                        }else{
                            {//MARK sonst wieder mit bisheriger Distanz markieren
                                pC.getView().getContentPanel().getNodeById(neighbour).setLabel(dists.get(neighbour)+"");
                            }
                        }

                        {//überprüfen, ob es sich bei aktuell untersuchtem Nachbar um Sackgasse handelt
                            boolean deadEnd = true;

                            //neue Liste um Interne nicht zu modifizieren //todo dagegen schützen vtl
                            Collection<Edge> toCheck = new LinkedList<Edge>(pC.getModel().getGraph().getOutEdges(neighbour));
                            for(Edge ie:pC.getModel().getGraph().getRevInEdges(neighbour)){
                                //falls zu betroffenem Knoten noch keine Kante ausgeht
                                boolean alreadyOut = false;
                                for(Edge out: toCheck){
                                    if(ie.getNode().getId()==out.getNode().getId()){
                                        alreadyOut = true;
                                        break;
                                    }
                                }
                                if(!alreadyOut){
                                    toCheck.add(ie);
                                }
                            }

                            //für alle anliegenden Kanten (priorisiert ausgehende)
                            for (Edge check: toCheck){
                                //wenn weder fertig, noch der Knoten von dem die Untersuchung ausging
                                if(!done.contains(check.getNode().getId()) && check.getNode().getId()!=cur){
                                    deadEnd = false;
                                    break;
                                }
                            }
                            if(deadEnd){
                                {//MARK fertigen Knoten entsprechend markieren
                                    pC.getView().getContentPanel().getNodeById(neighbour).setColor(Color.GREEN);
                                }
                                //Knoten als fertig deklarieren
                                done.add(neighbour);
                            }
                        }

                        //wenn untersuchter Nachbar weder fertig, noch bereits in To-do-Liste enthalten
                        if(!done.contains(neighbour)&&!toDo.contains(neighbour)){
                            toDo.add(neighbour);
                        }
                    }
                }

                done.add(cur);
                {//MARK fertigen Knoten als solchen markieren
                    pC.getView().getContentPanel().getNodeById(cur).setColor(Color.GREEN);
                    pC.getView().getContentPanel().getNodeById(cur).setMarked(false);
                }
            }

            updateViz(1.5d);
            return dists;
        }

        /**
         * fügt eine (durch delay Attribut beeinflusste) Verzögerung ein und ruft einen Redraw auf
         * @param pDuration Verzögerung in ms / delay Attribut
         */
        private void updateViz(double pDuration){
            if(this.delayMod == 0)return;//wenn Verzögerung auf 0, abbrechen

            this.controller.getView().redraw(RedrawModes.RESCALE);
            try {
                Thread.sleep((int)(pDuration*this.delayMod));
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }
        }

    }
}