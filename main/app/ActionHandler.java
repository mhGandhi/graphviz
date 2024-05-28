package app;

import app.model.Model;
import app.view.EdgeComponent;
import app.view.NodeComponent;
import app.view.RedrawModes;
import app.view.point.ActualPoint;

import java.awt.*;
import java.util.List;

/**
 * Verarbeitet von ViewListener weitergegebene Eingaben und bietet viele Methoden für die Anwendung in Tools
 */
public class ActionHandler {

////////////////////////////////////////////////////////////////////////////////////////////////////attributes

    /**
     * Controller des Programmes zur Ausführung von Befehlen
     */
    Controller controller;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public Controller getController(){
        return this.controller;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons
    /**
     * Controller des Programmes zur Ausführung von Befehlen
     * @param pC
     */
    public ActionHandler(Controller pC){
        controller = pC;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Registriert, dass ungespeichterte Änderungen vorliegen
     */
    public void setHasUnsavedChanges(){
        //Exception e = new Exception(); e.printStackTrace();
        //wenn bisher keine ungespeicherten Änderungen wird dem Fenstertitel ein Stern angehangen
        if(!getController().isUnsavedChanges())getController().getView().titleAppendStar();
        getController().setUnsavedChanges(true);
    }

    /**
     * speichert Graphmodell in in Properties gespeichertem Pfad; wenn leer kommt Dialog
     * @param pSetPath ob der aktuelle Speicherpfad entsprechend angepasst werden soll
     * @return Erfolg
     */
    public boolean saveGraphToSavedPath(boolean pSetPath){
        String path = getController().getProperties().getProperty("currentSavePath");
        if(path.isBlank()){
            return saveGraph(pSetPath);
        }else{
            return getController().saveGraph(path, pSetPath);
        }
    }

    /**
     * lädt Graphmodell von in Properties gespeichertem Pfad (wenn möglich)
     */
    public void loadGraphFromSavedPath(){
        String path = getController().getProperties().getProperty("currentSavePath");
        getController().saveProperties();
        if(path!=null&&!path.isBlank()){
            if(!getController().loadGraph(path))newGraph();
            getController().setUnsavedChanges(false);
        }else{
            newGraph();
        }
    }

    /**
     * speichert Graphmodell in Pfad, der durch fileChooser Methode der View gegeben wird
     * @param pSetPath ob der aktuelle Speicherpfad entsprechend angepasst werden soll
     * @return Erfolg
     */
    public boolean saveGraph(boolean pSetPath){
        try{
            String path = getController().getView().fileChooser(1, getController().getProperties().getProperty("defaultSavePath"));
            return getController().saveGraph(path, pSetPath);
        }catch(Exception e){
            return false;
        }
    }

    /**
     * lädt Graphmodell von Pfad, der durch fileChooser Methode der View gegeben wird
     * @return Erfolg
     */
    public boolean loadGraph(){
        try{
            String path = getController().getView().fileChooser(0, getController().getProperties().getProperty("defaultSavePath"));
            return getController().loadGraph(path);
        }catch(Exception e){
            return false;
        }
    }

    /**
     * startet neuen Graphen
     */
    public void newGraph(){
        if(!getController().assertGraphSaved()){
            return;
        }

        getController().setModel(new Model());

        getController().modelToComponentList();
        getController().getView().getViewState().resetCam();

        getController().getProperties().setProperty("currentSavePath", "");
        getController().getView().setTitleAppend(null);

        getController().getView().getViewState().resetCam();

        getController().setUnsavedChanges(false);
        redraw_Rescale();
    }

    /**
     * leitet Beendigung des Programmes ein
     */
    public void closeWindow(){
        System.out.println("_meddl out");
        getController().saveProperties();
        if(getController().assertGraphSaved()){
            getController().exit();
        }
    }

    /**
     * öffnet Einstellungen
     */
    public void settings(){
        getController().settings();
    }

    /**
     * ruft redraw Methode der View mit Argument RedrawModes.RESCALE auf
     */
    public void redraw_Rescale(){
        getController().getView().redraw(RedrawModes.RESCALE);
    }

    /**
     * ruft redraw Methode der View mit Argument RedrawModes.MOVE auf
     */
    public void redraw_Move(){
        getController().getView().redraw(RedrawModes.MOVE);
    }

    /**
     * ruft redraw Methode der View mit Argument RedrawModes.NONE auf
     */
    public void redraw_None(){
        getController().getView().redraw(RedrawModes.NONE);
    }

    /**
     * Gibt Liste der in der View ausgewählten Knoten zurück
     * @return Array mit Ids der Punkte
     */
    public int[] getSelectedNodes(){
        List<Integer> nds = getController().getView().getContentPanel().getSelectedNodes();
        if(nds.isEmpty())return null;
        int[] ret = new int[nds.size()];
        for (int i = 0; i < nds.size(); i++) {
            ret[i] = nds.get(i);
        }

        return ret;
    }

    /**
     * gibt zurück ob Knoten ausgewählt ist
     * @param pN Id des zu überprüfenden Knoten
     * @return ob der gegebene Knoten ausgewählt ist
     */ 
    public boolean isSelected(int pN){
        List<Integer> nds = getController().getView().getContentPanel().getSelectedNodes();
        return nds.contains(pN);
    }

    /**
     * Wechselt Auswahlstatus eines Knotens
     * @param pN Id des zu wechselnden Knotens
     */
    public void switchSelection(int pN){
        if (isSelected(pN)) {
            deselectNode(pN);
        }else{
            selectNode(pN);
        }

        redraw_None();
    }

    /**
     * Fügt Knoten zur Selektion hinzu
     * @param pN Der Selektion hinzuzufügende Knoten
     */
    public void selectNode(int pN){
        List<Integer> nds = getController().getView().getContentPanel().getSelectedNodes();
        if (!isSelected(pN)) {
            nds.add(Integer.valueOf(pN));
        }
    }

    /**
     * Entfernt Knoten aus Selektion
     * @param pN Der aus Selektion zu entfernende Knoten
     */
    public void deselectNode(int pN){
        List<Integer> nds = getController().getView().getContentPanel().getSelectedNodes();
        if (isSelected(pN)) {
            nds.remove(Integer.valueOf(pN));
            //System.out.println("dsl "+pN);
        }
    }

    /**
     * Fügt Knoten zur Selektion hinzu
     * @param pNs Array der der Selektion hinzuzufügende Knoten
     */
    public void selectNode(int[] pNs){
        for (int i : pNs) {
            selectNode(i);
        }
    }

    /**
     * Entfernt Knoten aus Selektion
     * @param pNs Array der der aus Selektion zu entfernende Knoten
     */
    public void deselectNode(int[] pNs){
        for (int i : pNs) {
            deselectNode(i);
        }
    }

    /**
     * Entfernt die gesamte Auswahl
     */
    public void cancelSelection(){
        List<Integer> nds = getController().getView().getContentPanel().getSelectedNodes();
        nds.clear();
        redraw_None();
    }

    /**
     * Gibt obersten Knoten an gegebenem Punkt zurück
     * @param pP zu überprüfender Punkt
     * @return Id des Knotens an Punkt (-1 wenn keiner)
     */
    public int getNodeAtPoint(ActualPoint pP){
        return getController().getView().getContentPanel().getFirstNodeAtPoint(pP);
    }

    /**
     * gibt NodeComponent mit gegebener Id zurück
     * @param pId Id des Knotens
     * @return Knoten mit der Id
     */
    public NodeComponent getNodeById(int pId){
        return getController().getView().getContentPanel().getNodeById(pId);
    }

    /**
     * bewegt Knoten in vorderste Ebene
     * @param pId Id des nach vorne zu verschiebenden Knotens
     */
    public void nodeToFront(int pId){
        getController().getView().getContentPanel().nodeToFront(pId);
        redraw_None();
        setHasUnsavedChanges();
    }

    /**
     * gibt Eckpunkte des die Mausauswahl repräsentierenden Rechteckes zurück
     * @return Array, welches die beiden Eckpunkte enthält
     */
    public ActualPoint[] getSlRectPts(){
        return getController().getView().getContentPanel().getSlRectPts();
    }

    /**
     * gibt Knoten, deren Mittelpunkt innerhalb des Auswahlrechteckes liegen zurück
     * @return Array, welches Ids der Knoten enthält
     */
    public int[] getNdsInSlRect(){
        ActualPoint[] pts = getSlRectPts();
        Rectangle rect = getController().getView().getContentPanel().getRect(pts[0], pts[1]);

        return getController().getView().getContentPanel().getNodesInRect(rect);
    }

    /**
     * Ändert Label von Knoten
     * @param pN Array mit Ids der umzubennenenden Knoten
     */
    public void renameNodes(int[] pN){
        if(pN==null)return;//vtl aus der methode raus verlagern???
        String newName;
        try {
            if(pN.length==1){
                newName = getController().getView().stringDialog(getNodeById(pN[0]).getLabel());
            }else{
                newName = getController().getView().stringDialog("");
            }
            
        } catch (Exception e) {
            System.err.println("[Invalid Input]");
            return;
        }
        if(newName == null){
            return;
        }
        
        
        if(pN.length==1){
            String oldName = getNodeById(pN[0]).getLabel();
            if(newName.equals(oldName))
            return;
        }
        
        
        for (int i = 0; i < pN.length; i++) {
            NodeComponent nc = getController().getView().getContentPanel().getNodeById(pN[i]);
            nc.setLabel(newName);
            getController().nodeComponentToModel(pN[i]);
        }

        redraw_Rescale();
        setHasUnsavedChanges();
    }

    /**
     * Löscht Knoten
     * @param pN Array mit Ids der zu löschenden Knoten
     */
    public void deleteNodes(int[] pN){
        if(pN==null)return;
        for (int i = 0; i < pN.length; i++) {
            getController().removeNode(pN[i]);
        }

        deselectNode(pN);

        getController().setUnsavedChanges(!getController().getView().getContentPanel().getNodes().isEmpty());
        redraw_Rescale();
    }

    /**
     * Fügt neuen Knoten hinzu
     * @param pPt Punkt, an dem der Knoten erzeugt wird.
     */
    public void newNode(ActualPoint pPt){
        getController().addNode(getController().getView().getViewState().getPointInSystem(pPt));
        setHasUnsavedChanges();
        redraw_Rescale();
    }

    /**
     * Fügt gewichtete Kante zwischen 2 Knoten ein
     * @param pFrom     Id Knoten 1
     * @param pTo       Id Knoten 2
     * @param pWeight   Gewichtung
     */
    public void addEdge(int pFrom, int pTo, int pWeight){
        getController().addEdge(pFrom,pTo,pWeight);
        redraw_None();
        setHasUnsavedChanges();
    }

    /**
     * Fügt gerichtete, gewichtete Kante zwischen 2 Knoten ein
     * @param pFrom     Id Knoten 1
     * @param pTo       Id Knoten 2
     * @param pWeight   Gewichtung
     */
    public void addDirEdge(int pFrom, int pTo, int pWeight){
        getController().addDirEdge(pFrom,pTo,pWeight);
        redraw_None();
        setHasUnsavedChanges();
    }

    /**
     * Färbt Knoten
     * @param pN Id des zu färbenden Knoten
     */
    public void recolourNode(int pN){

        NodeComponent nc = getController().getView().getContentPanel().getNodeById(pN);
        Color oldCol = nc.getColor();
        Color newCol;

        //erfragung der Farbe
        try {
            newCol = getController().getView().colorDialog(nc.getColor());
            if(newCol.equals(getController().getView().getViewState().getTheme().getDefaultNodeColor())){
                newCol = null;
            }
        } catch(Exception e){
            newCol = null;
        }
        
        nc.setColor(newCol);

        getController().nodeComponentToModel(pN);

        if(
            (
                oldCol!=null
                &&
                !oldCol.equals(newCol)
            )
            ||
            (
                newCol!=null
                &&
                !newCol.equals(oldCol)
            )

        ){
            redraw_None();
            setHasUnsavedChanges();
        }
    }

    /**
     * Entfernt Kanten von Knoten
     * @param pN Array der Knoten
     */
    public void removeEdges(int[] pN){
        for (int node : pN) {
            controller.clearEdges(node);
        }
        redraw_None();
        setHasUnsavedChanges();
    }

    /**
     * ruft freePopUp()-Methode der View auf
     * @param pP Position des Popups
     */
    public void freePopUp(ActualPoint pP){
        getController().getView().freePopUp(pP);
    }

    /**
     * ruft nodePopUp()-Methode der View auf
     * @param pP Postion des Popups
     */
    public void nodePopUp(ActualPoint pP){
        getController().getView().nodePopUp(pP);
    }

    /**
     * gibt EdgePreview Punkte des ContentPanels weiter
     * @return
     */
    public ActualPoint[] getEdgePreview(){
        return getController().getView().getContentPanel().getEdgePreview();
    }

    /**
     * ruft validateEdges()-Methode des Controllers auf
     */
    public void validateEdges() {
        getController().validateEdges();
    }

    /**
     * ruft modelToComponentList()-Methode des Controllers auf
     */
    public void modelToComponentList() {
        getController().modelToComponentList();
    }

    /**
     * ruft syncPosToModel()-Methode des Controllers auf
     * @param pId
     */
    public void syncPosToModel(int pId){
        getController().syncPosToModel(pId);
    }

    /**
     * ruft hasEdgeTo()-Methode des Graphen auf
     * @param pN1
     * @param pN2
     * @return
     */
    public boolean hasEdgeTo(int pN1, int pN2){
        return getController().getModel().getGraph().hasEdgeTo(pN1,pN2);
    }

    /**
     * ruft isEdgeBetween()-Methode des Graphen auf
     * @param pN1
     * @param pN2
     * @return
     */
    public boolean isEdgeBetween(int pN1, int pN2){
        return getController().getModel().getGraph().isEdgeBetween(pN1,pN2);
    }

    /**
     * ruft removeDirEdge()-Methode des Graphen auf und überträgt die Änderungen auf die View
     * @param pN1
     * @param pN2
     */
    public void removeDirEdge(int pN1, int pN2) {
        getController().getModel().getGraph().removeDirEdge(pN1, pN2);
        getController().syncEdgesToView();
    }

    /**
     * ruft removeEdge()-Methode des Graphen auf und überträgt die Änderungen auf die View
     * @param pN1
     * @param pN2
     */
    public void removeEdge(int pN1, int pN2) {
        getController().getModel().getGraph().removeEdge(pN1, pN2);
        getController().syncEdgesToView();
    }

    /**
     * Startet ein Tool
     * @param pAction Toolausführungsbefehl tool_toolId_origin
     */
    public void useTool(String pAction, ActualPoint pPressPos){

        //sucht Anfang und Ende der ToolId
        int incIdStart = 0;
        for(int i = 0; i < pAction.length(); i++){
            if(pAction.charAt(i)=='_'){
                incIdStart = i+1;
                break;
            }
        }
        int exIdEnd = 0;
        for (int i = pAction.length()-1; i > -1; i--) {
            if(pAction.charAt(i)=='_'){
                exIdEnd = i;
                break;
            }
        }

        String toolId = pAction.substring(incIdStart, exIdEnd);
        //System.out.println(toolId);

        if(pAction.endsWith("_MENUBAR")){
            getController().toolFromMenuBar(toolId);
        }else if(pAction.endsWith("_NODE")){
            getController().toolFromNode(toolId,getNodeAtPoint(pPressPos));
        }else if(pAction.endsWith("_SPOT")){
            getController().toolFromSpot(toolId,getController().getView().getViewState().getPointInSystem(pPressPos));
        }
    }

    /**
     * überprüft ob Gewichtung auf Position liegt und ruft einen Dialog zur Gewichtsänderung auf
     * @param pAp   Position
     * @return      Gewichtung auf Position
     */
    public boolean edgeBoxClick(ActualPoint pAp){
        for (EdgeComponent ec:getController().getView().getContentPanel().getEdges()){
            if(ec.labelContains(pAp)){
                int oldWt = ec.getWeight();

                int in = getController().getView().intDialog(ec.getWeight());
                if(ec.isBidirect()){
                    getController().getModel().getGraph().addEdge(ec.getFrom().getId(), ec.getTo().getId(), in);
                }else{
                    getController().getModel().getGraph().addDirectedEdge(ec.getFrom().getId(), ec.getTo().getId(), in);
                }
                getController().syncEdgesToView();
                if(oldWt!=in){
                    setHasUnsavedChanges();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Ruft Dialog zur Eingabe einer Zahl auf
     * @return Eingegebene Zahl
     */
    public int intDialog(int pInitVal) {
        return getController().getView().intDialog(pInitVal);
    }

    /**
     * Entfernt jegliche Markierungen
     */
    public void removeMarkings() {
        getController().removeMarkings();
    }

    /**
     * Start Import Dialog
     */
    public void importe(){
        ImportExport.importGraph(this);
        modelToComponentList();
        redraw_Rescale();
    }

    /**
     * Start Export Dialog
     */
    public void exporte(){
        ImportExport.exportGraph(this);
    }
}
