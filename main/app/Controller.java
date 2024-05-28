package app;

import app.model.Edge;
import app.model.Model;
import app.model.NodePresentation;
import app.view.*;
import app.view.point.InSysPoint;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Stellt im MVC-Modell den Controller dar
 */
public class Controller {

////////////////////////////////////////////////////////////////////////////////////////////////////attributes
    /**
     * Modell des MVC
     */
    private Model model;

    /**
     * View des MVC
     */
    private View view;

    /**
     * propis hihi
     */
    private Properties properties;

    /**
     * liegen ungespeicherte Änderungen vor?
     */
    private boolean unsavedChanges;

    /**
     * alle Tools
     */
    private Tool[] tools;

    /**
     * Actionhandler zur Nutzung enthaltener Methoden
     */
    private ActionHandler actionHandler;//todo stattdessen mehr in controller

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public Model getModel() {
        return model;
    }

    public void setModel(Model pModel){
        this.model = pModel;
    }

    public View getView() {
        return view;
    }

    public Properties getProperties(){
        return this.properties;
    }

    public boolean isUnsavedChanges() {
        return unsavedChanges;
    }

    public void setUnsavedChanges(boolean unsavedChanges) {
        this.unsavedChanges = unsavedChanges;
    }

    public Tool[] getTools(){
        return this.tools;
    }

    public ActionHandler getActionHandler(){
        return this.actionHandler;
    }

    /**
     * Gibt Objekt von Tool mit bestimmter Id zurück
     * @param pId Id des gesuchten Tools
     * @return
     */
    public Tool getTool(String pId){
        for(Tool tool : getTools()){
            if(tool.getId().equals(pId)){
                return tool;
            }
        }
        System.err.println("Tool with id ["+pId+"] not found");
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////cons
    /**
     * @param pTools Toolset
     */
    public Controller(Tool[] pTools){

        this.properties = new Properties();
        loadProperties();
        
        //initialisiert Standardspeicherverzeichnis falls keines festgelegt
        String defSP = getProperties().getProperty("defaultSavePath");
        if(defSP==null||defSP.isBlank()){
            getProperties().setProperty("defaultSavePath", 
                                        FileSystemView.getFileSystemView().getDefaultDirectory().getPath()+"\\GraphVisualizer");
        }
        String loc = Locale.getDefault().toString();
        
        //läd Theme aus Datei
        Theme theme;
        try {
            theme = ThemeFactory.fromFile(getProperties().getProperty("slTheme"));
        } catch (Exception e) {
            System.err.println("Selected Theme seems to be invalid, reverting to Default.");
            getProperties().setProperty("slTheme", "");
            theme = ThemeFactory.defaultTheme();
        }
        saveProperties();


        ViewState viewState = new ViewState(theme);

        this.actionHandler = new ActionHandler(this);
        ViewListener viewListener = new ViewListener(viewState, actionHandler);

        //überträgt Toolspezifikationen auf ToolMenuItemSpecs
        List<ToolMenuItemSpecs> tmpTMIS = new LinkedList<ToolMenuItemSpecs>();
        for (int i = 0; i < pTools.length; i++){
            if(pTools[i].showInMenu()){
                tmpTMIS.add(new ToolMenuItemSpecs(pTools[i].getName(), pTools[i].getId(), pTools[i].canBeRanFromMenuBar(), 
                                                    pTools[i].canBeRanFromNode(), pTools[i].canBeRanFromSpot()));
            }
        }
        ToolMenuItemSpecs[] toolMIs = new ToolMenuItemSpecs[tmpTMIS.size()];
        for (int i = 0; i < toolMIs.length; i++) {
            toolMIs[i] = tmpTMIS.get(i);
        }

        this.tools = pTools;
        this.model = new Model();


        //sucht gültige Referenz auf eine verfügbare Sprache
        System.out.println("init lang");
        String selectedLang = getProperties().getProperty("slLang");
        ResourceBundle res;
        if(selectedLang.isBlank()){
            selectedLang = Locale.getDefault().getLanguage();
            System.out.println("\tno lang selected, reverting to "+selectedLang);
            getProperties().setProperty("slLang", selectedLang);
        }

        res = Controller.getBundleFromFile(getProperties().getProperty("slLang"));
        if(res == null){
            System.out.println("\tno path");
            res = Controller.getBundleForLanguage( Locale.forLanguageTag(selectedLang) );
            if(res==null) {
                System.out.println("\tno accepted tag");
                res = Controller.getBundleFromFile("lgs/lang.properties");
                getProperties().setProperty("slLang", "lgs/lang.properties");
            }
        }
        //check if language was applied
        if(res == null){
            System.err.printf("no language found, add file lgs/lang.properties");
            exit();
        }


        this.view = new View(viewState,viewListener,viewListener,viewListener,viewListener,viewListener,viewListener,toolMIs,res);

        viewListener.actionPerformed(new ActionEvent(this, 0, "start"));//Initialisierungsbefehl
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * läd Properties aus pref.config in Properties
     */
    public void loadProperties(){
        try(FileInputStream fis = new FileInputStream("pref.config")){
            getProperties().load(fis);
            fis.close();
        }catch(FileNotFoundException e){
            try {
                FileWriter writer = new FileWriter("pref.config");
                writer.write(
                    "#last save:\n#\ncurrentSavePath=\ndefaultSavePath=\nslLang=\nslTheme=");
                writer.close();
                loadProperties();
            } catch (Exception ex) {
                
            }
        }catch(IOException e){

        }
    }

    /**
     * läd Properties aus Properies in prep.config
     */
    public void saveProperties(){
        try(FileOutputStream fos = new FileOutputStream("pref.config")){
            getProperties().store(fos, "last save:");
            fos.close();
        }catch(IOException e){

        }
    }

    /**
     * Übersetzt die Knoten und Kanten aus dem Modell in Komponente für die View und fügt sie in die Knoten- und Kanten-Komponentenliste der View ein
     * Entfernt auch alle Markierungen
     */
    public void modelToComponentList(){
        List<Integer> nIds = getModel().getIds();
        List<NodeComponent> nLs = new LinkedList<NodeComponent>();

        for (Integer nodeId : nIds){
            NodePresentation np = getModel().getNodePresentation(Integer.valueOf(nodeId));

            NodeComponent nc = new NodeComponent(np.getAssNode(), np.getPosX(), np.getPosY(), np.getColorCode(), np.getLabel(), getView().getViewState());

            nLs.add(nc);
        }

        getView().getContentPanel().setNodes(nLs);

        syncEdgesToView();
    }

    /**
     * Übersetzt due Kanten aus dem Modell fpr die View und fügt sie un dei Kanten-Komponentenliste der View ein
     * Entfernt auch alle Kantenmarkierungen
     */
    public void syncEdgesToView(){
        List<Integer> nIds = getModel().getIds();
        List<EdgeComponent> eLs = new LinkedList<EdgeComponent>();
        
        for (Integer n : nIds) {

            List<Edge> edges = getModel().getGraph().getOutEdges(n);
            
            for(Edge e : edges){
                eLs.add(new EdgeComponent(getView().getContentPanel().getNodeById(n),
                                        getView().getContentPanel().getNodeById(e.getNode().getId()),
                                        e.getWeight(),
                                        getView().getViewState()));
            }
        }
        
        getView().getContentPanel().setEdges(eLs);
        validateEdges();
    }

    /**
     * Überträgt Knotenkomponente in Modell
     * @param pId Id des zu übertragenden Knotens
     */
    @Deprecated
    public void nodeComponentToModel(int pId){
        NodeComponent nc = getView().getContentPanel().getNodeById(pId);
        String col;

        try {
            col = Theme.encodeColor(nc.getColor());
        } catch (Exception e) {
            col = null;
        }

        getModel().saveNodePres(pId, nc.getPositionX(), nc.getPositionY(), nc.getLabel(), col);
    }

    /**
     * Überträgt Position einer Knotenkomponente in Modell
     * @param pId Id des Knotens
     */
    /*goof
    public void syncPosToModel(int pId){
        NodeComponent nc = getView().getContentPanel().getNodeById(pId);

        getModel().syncNodePos(pId, nc.getPositionX(), nc.getPositionY());
    }
     */

    /**
     * Löscht Knoten aus Model und Komponentenliste
     * @param pId Id des zu entfernenden Knoten
     */
    public void removeNode(int pId){
        getModel().removeNode(pId);

        modelToComponentList();
    }

    /**
     * Entfernt Kanten von bestimmtem Knoten
     * @param pId Id des bestimmten Knotens
     */
    public void clearEdges(int pId){
        getModel().removeEdges(pId);

        syncEdgesToView();
    }

    /**
     * Fügt neuen Knoten in Model und View ein
     * @param pPt Position des neuen Knotens
     */
    public void addNode(InSysPoint pPt){
        int nNdId = getModel().addNode();

        getModel().getNodePresentation(nNdId).setPos(pPt.getX(),pPt.getY());
        
        modelToComponentList();
    }

    /**
     * Fügt neue gerichtete Kante hinzu
     * @param pIdFrom Id of first Node
     * @param pIdTo Id of seconde Node
     * @param pWeight Gewicht der Kante
     */
    public void addDirEdge(int pIdFrom, int pIdTo, int pWeight){
        getModel().getGraph().addDirectedEdge(pIdFrom, pIdTo, pWeight);

        syncEdgesToView();
    }

    /**
     * Fügt neue ungerichtete Kante hinzu
     * @param pIdFrom Id of first Node
     * @param pIdTo Id of seconde Node
     * @param pWeight Gewicht der Kante
     */
    public void addEdge(int pIdFrom, int pIdTo, int pWeight){
        getModel().getGraph().addEdge(pIdTo, pIdFrom, pWeight);

        syncEdgesToView();
    }

    /*
     * Berechnet für alle Kantendarstellungen ob sie bidirektional sind und ob die Gewichte angezeigt werden sollen
     */
    public void validateEdges(){

        getView().getViewState().setShowWeight(!getModel().isWeighted());//ob der Graph gewichtet sein soll

        List<EdgeComponent> edgeCompList = getView().getContentPanel().getEdges();
        if(edgeCompList.isEmpty())return;

        for (EdgeComponent edgeComponent : edgeCompList){
            /*
            Node to = getModel().getGraph().getAdjNodes(edgeComponent.getTo().getId()).get(new Node(edgeComponent.getFrom().getId()));
            if(getModel().getGraph().getAdjNodes(edgeComponent.getTo().getId()).contains(new Node())){}
            */
            //todo alles reworken mit Model oder in EdgeComponent verlagern
            EdgeComponent otherDirShape = new EdgeComponent(edgeComponent.getTo(), edgeComponent.getFrom(), 1, null);
            //wenn Kante in andere Richtung besteht setze Werte entsprechend
            if(edgeCompList.contains(otherDirShape)){
                EdgeComponent otherDir = otherDirShape;
                for (EdgeComponent edgeComponent2 : edgeCompList) {
                    if(edgeComponent2.equals(otherDirShape)){
                        otherDir = edgeComponent2;
                        break;
                    }
                }

                if (edgeComponent.getWeight()==otherDir.getWeight()) {
                    edgeComponent.setBidirect(true);
                    otherDir.setBidirect(true);
                }else{
                    edgeComponent.setBidirect(false);
                    otherDir.setBidirect(false);
                }
            }else{
                edgeComponent.setBidirect(false);
            }
        }
    }
    
    /**
     * Startet Tool von MenuBar aus
     * @param pToolId Id des Tools
     */
    public void toolFromMenuBar(String pToolId) {
        try {
            getTool(pToolId).runFromMenuBar(this);
        } catch (Exception e) {
            
        }
    }
    
    /**
     * Startet Tool von bestimmtem Knoten aus
     * @param pToolId       Id des Tools
     * @param pNodeAtPoint  Id des bestimmten Knoten
     */
    public void toolFromNode(String pToolId, int pNodeAtPoint) {
        try {
            getTool(pToolId).runOnNode(this, pNodeAtPoint);
        } catch (Exception e) {
            
        }
    }

    /**
     * Startet Tool von bestimmter Position aus
     * @param pToolId   Id des Tools
     * @param pPressPos bestimmte Position
     */
    public void toolFromSpot(String pToolId, InSysPoint pPressPos) {
        try {
            getTool(pToolId).runAtSpot(this, pPressPos);
        } catch (Exception e) {
            
        }   
    }

    /**
     * öffnet Einstellungen
     */
    public void settings(){
        SettingsDialog.showDialog(getView().getWindow(), getProperties(), getView().getResourceBundle(), getView().getViewState().getTheme(), getTools(),this);
    }

    /**
     * beendet Programm
     */
    public void exit(){
        System.exit(0);
    }

    /**
     * Speichert Graphmodell in Datei in gegebenem Pfad
     * @param pPath Pfad, unter dem die Datei zu speichern ist
     * @param pSetPath ob der aktuelle Speicherpfad entsprechend angepasst werden soll
     * @return Erfolg
     */
    public boolean saveGraph(String pPath, boolean pSetPath){
        if(pPath==null)return false;

        Model gm = getModel();
        boolean suc = FileSS.saveToFile(gm,pPath);

        //Test, ob Geladener Graph aus Speicherpfad dem gespeicherten gleicht
        Model testGr = new Model();
        try {
            testGr = (Model)FileSS.loadFromFile(pPath);
        } catch (StreamCorruptedException e) {
            suc = false;
        } catch (Exception e){
            suc = false;
        }
        suc = gm.equals(testGr);


        if(pSetPath){
            getProperties().setProperty("currentSavePath", pPath);
        }
        saveProperties();
        getView().setTitleAppend(pPath);
        setUnsavedChanges(!suc);
        return suc;
    }

    /**
     * Lädt Graphmodell aus Datei in gegebenem Pfad
     * @param pPath Pfad, aus dem die Datei zu laden ist
     * @return Erfolg
     */
    public boolean loadGraph(String pPath){
        if (pPath==null)return false;
        Model gm;
        try {
            gm = (Model)FileSS.loadFromFile(pPath);
        } catch (StreamCorruptedException e) {
            getView().notification("notif_couldntLoadFile");
            return false;
        } catch (Exception e){
            getView().notification("notif_couldntLoadFile");
            return false;
        }
        setModel(gm);

        modelToComponentList();
        getView().getViewState().resetCam();
        getView().setTitleAppend(pPath);

        getProperties().setProperty("currentSavePath", pPath);

        getView().redraw(RedrawModes.RESCALE);
        return true;
    }

    /**
     * Entfernt alle Markierungen
     */
    public void removeMarkings() {
        modelToComponentList();
        getView().redraw(RedrawModes.RESCALE);
    }

    /**
     * Gibt Sprache direkt aus Datei zurück.
     * @param filePath
     * @return
     */
    public static ResourceBundle getBundleFromFile(String filePath) {
        try (InputStream stream = new FileInputStream(filePath)) {
            return new PropertyResourceBundle(stream);
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Gibt Sprache der Locale zurück.
     * @param pL
     * @return
     */
    public static ResourceBundle getBundleForLanguage(Locale pL){
        return ResourceBundle.getBundle("lgs/lang",pL);
    }

    /**
     * Stellt sicher, dass der Graph gespeichert ist oder ungespeichert bleiben soll 
     * @return ob fortgefahren werden kann
     */
    public boolean assertGraphSaved(){
        if(isUnsavedChanges()){//wenn ungespeicherte Änderungen vorliegen starte Dialog
            int slOption = -1;
            {
                String msg = getView().r("notif_unsavedChange");
                String[] opts = {
                getView().r("option_saveFirst"),
                getView().r("option_dontSave"),
                getView().r("option_cancel")
                };
                slOption = getView().optionDialog(msg, opts);
                getView().getViewListener().actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
            }
            
            switch (slOption) {
                case 0:
                    if(getActionHandler().saveGraphToSavedPath(true)){
                        return true;//wenn der Graph erfolgreich gespeichert werden konnte
                    }else{
                        return false;
                    }
                case 1:
                    return true;
                default:
                    return false;
            }
            
            
        }else{
            return true;
        }
    }

}