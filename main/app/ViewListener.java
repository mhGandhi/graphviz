package app;

import app.model.NodePresentation;
import app.view.NodeComponent;
import app.view.ViewState;
import app.view.point.ActualPoint;
import app.view.point.InSysPoint;

import java.awt.event.*;

/**
 * Verarbeitet und verteilt sämtliche Eingaben durch Peripherie und Buttonns des Benutzers
 */
public class ViewListener implements ActionListener, KeyListener, MouseListener, MouseWheelListener, MouseMotionListener, WindowListener{

////////////////////////////////////////////////////////////////////////////////////////////////////attributes
    /**
     * ViewState des Programmes
     */
    private ViewState viewState;

    /**
     * Actionhandler, an den die Eingaben weitergegeben werden
     */
    private ActionHandler actionHandler;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    private ViewState getViewState(){
        return this.viewState;
    }

    private ActionHandler getActionHandler(){
        return this.actionHandler;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////cons
    public ViewListener(ViewState pVs, ActionHandler pAh){
        viewState = pVs;
        actionHandler = pAh;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e){
        
        String action = e.getActionCommand();
        
        if(action.startsWith("TOOL_")){
            getActionHandler().useTool(action, pressPos);
        }else{
            int[] n = new int[1];
            n[0] = getActionHandler().getNodeAtPoint(lastMousePos);

            switch(action){
                case "rmKeyMods":
                    rmKeymods();
                    break;
                //file
                case "file_new":
                    getActionHandler().newGraph();
                    break;
                case "file_save":
                    getActionHandler().saveGraphToSavedPath(true);
                    break;
                case "file_saveas":
                    getActionHandler().saveGraph(true);
                    break;
                case "file_load":
                    getActionHandler().loadGraph();
                    break;
                case "file_export":
                    getActionHandler().exporte();
                    break;
                case "file_import":
                    getActionHandler().importe();
                    break;
                case "settings":
                    getActionHandler().settings();
                    break;
                //view
                case "toggleState_showGrid":
                    getViewState().setShowGrid(!getViewState().isShowGrid());
                    getActionHandler().redraw_None();
                    break;
                case "toggleState_showBounds":
                    getViewState().setShowBounds(!getViewState().isShowBounds());
                    getActionHandler().redraw_None();
                    break;
                case "view_reset":
                    getViewState().resetCam();
                    getActionHandler().modelToComponentList();
                    getActionHandler().redraw_Rescale();
                    break;
                case "view_removeMarkings":
                    getActionHandler().removeMarkings();
                    break;
                //nodeaction
                case "nodeAction_recolour":
                    getActionHandler().recolourNode(n[0]);
                    break;
                case "nodeAction_rename":
                    getActionHandler().renameNodes(n);
                    break;
                case "nodeAction_delete":
                    getActionHandler().deleteNodes(n);
                    break;
                case "nodeAction_removeEdges":
                    getActionHandler().removeEdges(n);
                    break;
                case "start":
                    System.out.println("meddl_");
                    getActionHandler().loadGraphFromSavedPath();
                    break;
                case "action_newNode":
                    getActionHandler().newNode(pressPos);
                    break;
                default:
                    System.err.println("[ACTIONUNDEFINED]->["+action+"]");
                    break;
            }
        }
    }

///////keylistening

    @Override
    public void keyTyped(KeyEvent e) {
       
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case 27: //esc
                getActionHandler().cancelSelection();            
                break;
            case 525: //das ding rechts neben alt gr
                getActionHandler().freePopUp(new ActualPoint(0,0));
                break;
            case 38: //up
                getViewState().setZoom(getViewState().getZoom()+1);
                break;
            case 40: //down
                getViewState().setZoom(getViewState().getZoom()-1);
                break;
            case 32: //space
                
                break;
            case 82: //r
                if(keyMod_strg){
                    getViewState().resetCam();
                    getActionHandler().modelToComponentList();
                    getActionHandler().redraw_Rescale();
                }
                break;
            case 87: //w
                getViewState().setOffsetY(getViewState().getOffsetY()-1);
                break;
            case 65: //a
                getViewState().setOffsetX(getViewState().getOffsetX()-1);
                break;
            case 83: //s
                if(keyMod_strg){
                    getActionHandler().saveGraphToSavedPath(true);
                }else{
                    getViewState().setOffsetY(getViewState().getOffsetY()+1);
                }
                if(keyMod_strg&&keyMod_shift){
                    getActionHandler().saveGraph(true);
                }
                break;
            case 68: //d
                getViewState().setOffsetX(getViewState().getOffsetX()+1);
                break;
            case 78: //n
                if(keyMod_strg){
                    getActionHandler().newGraph();
                }
                break;
            case 17: //strg
                keyMod_strg = true;
                break;
            case 16: //shift
                keyMod_shift = true;
                getViewState().setDisplayWeightBlobs(true);
                getActionHandler().redraw_None();
                break;
            case 113: //f2
                getActionHandler().renameNodes(getActionHandler().getSelectedNodes());
                break;
            case 127: //entf
                getActionHandler().deleteNodes(getActionHandler().getSelectedNodes());
                break;
            case 521: //+
            case 107: //num+
                getViewState().setZoom(getViewState().getZoom()+10);
                getActionHandler().redraw_Rescale();
                break;
            case 45: //-
            case 109: //num-
                getViewState().setZoom(getViewState().getZoom()-10);
                getActionHandler().redraw_Rescale();
                break;
            case 76://l
                if(keyMod_strg){
                    getActionHandler().loadGraph();
                }
                break;
            default:
                System.out.println("[KEY] char "+e.getKeyChar()+" code "+e.getKeyCode());
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent e) {
        
        switch (e.getKeyCode()) {
            case 17: //strg
                keyMod_strg = false;
                break;
            case 16: //shift
                keyMod_shift = false;
                getViewState().setDisplayWeightBlobs(false);
                getActionHandler().redraw_None();
                break;
            default:
                break;
        }
    }

///////mouselistening
    /**
     * gibt an ob Taste strg gedrückt ist
     */
    private boolean keyMod_strg = false;
    
    /**
     * gibt an ob Taste shift gedrückt ist
     */
    private boolean keyMod_shift = false;

    /**
     * gibt an ob Taste strg bei Klick gedrückt war
     */
    private boolean keyMod_strg_AP = false;
    
    /**
     * gibt an ob Taste shift bei Klick gedrückt war
     */
    private boolean keyMod_shift_AP = false;

    /**
     * Letzter bekannter Punkt der Maus
     */
    private ActualPoint lastMousePos = new ActualPoint(0,0);

    /**
     * Liste der Ids der aktuell ausgewählten Punkte (nicht zwingend gleich denen der View)
     */
    private int[] selectedNodes;

    /**
     * Postition, an der die die Maus gedrückt wurde
     */
    private ActualPoint pressPos = new ActualPoint(0,0);

    /**
     * Maustaste, die gedrückt wurde
     * 1 = lClick; 2 = mClick; 3 = rClick
     */
    private int pressButt = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int nodeAtPoint = getActionHandler().getNodeAtPoint(new ActualPoint(e));
        if(nodeAtPoint>=0){
            switch (e.getButton()) {
                case 3:
                    getActionHandler().nodePopUp(new ActualPoint(e));
                    break;
                case 1:
                    if(keyMod_strg){
                        getActionHandler().switchSelection(nodeAtPoint);
                    }else{
                        getActionHandler().cancelSelection();
                        //getActionHandler().nodeToFront(nodeAtPoint);
                    }
                    if(e.getClickCount()==2){
                        if(keyMod_shift&&getActionHandler().edgeBoxClick(pressPos)){
                            getActionHandler().redraw_None();
                            break;
                        }else{
                            if(!keyMod_strg){
                                int[] n = {nodeAtPoint};
                                getActionHandler().renameNodes(n);
                                getActionHandler().redraw_Rescale();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }else{
            switch (e.getButton()) {
                case 3:
                    getActionHandler().freePopUp(new ActualPoint(e));
                    break;
                case 1:
                    getActionHandler().cancelSelection();
                    if(e.getClickCount()==2){
                        if(!getActionHandler().edgeBoxClick(pressPos)){
                            getActionHandler().newNode(new ActualPoint(e));//todo müsste in jeder sprache noch erwähnt werden
                        }
                        getActionHandler().redraw_Rescale();
                    }
                    break;
                default:
                    break;
            }
            
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e){
        keyMod_shift_AP = keyMod_shift;
        keyMod_strg_AP = keyMod_strg;
        pressPos = new ActualPoint(e);
        lastMousePos = new ActualPoint(e);
        pressButt = e.getButton();
        int nodeAtPoint = getActionHandler().getNodeAtPoint(new ActualPoint(e));
        if (nodeAtPoint>=0){
            if(getActionHandler().getSelectedNodes()==null){
                selectedNodes = new int[1];
                selectedNodes[0] = nodeAtPoint;
            }else{
                selectedNodes = getActionHandler().getSelectedNodes();
            }
        }else{
            selectedNodes = getActionHandler().getSelectedNodes();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //wenn keine Knoten in mehrfachselektion dann aktuelle Selektion leeren
        if(getActionHandler().getSelectedNodes()==null){
            selectedNodes = null;
        }
        int nodeAP = getActionHandler().getNodeAtPoint(pressPos);//Knoten auf Klickpunkt
        int nodeAR = getActionHandler().getNodeAtPoint(new ActualPoint(e));//Knoten auf Loslasspunkt
        switch (pressButt){
            case 3://wenn mit rechter Maustaste gezogen
                int[] ndsInSl = getActionHandler().getNdsInSlRect();
                if(ndsInSl.length>=1){
                    if (keyMod_shift_AP) {
                        getActionHandler().deselectNode(ndsInSl);
                    }else if(keyMod_strg_AP){
                        getActionHandler().selectNode(ndsInSl);
                    }else{
                        getActionHandler().cancelSelection();
                        getActionHandler().selectNode(ndsInSl);
                    }
                }

                ActualPoint[] sl = getActionHandler().getSlRectPts();
                sl[0] = null;
                sl[1] = null;
                ActualPoint[] ep = getActionHandler().getEdgePreview();
                ep[0] = null;
                ep[1] = null;
                
                if(nodeAP>=0 && nodeAR>=0 && (nodeAP!=nodeAR)){
                    int weight = 1;
                    if(keyMod_shift_AP){
                        if(!getActionHandler().hasEdgeTo(nodeAP, nodeAR)){
                            weight = getActionHandler().intDialog(1);
                        }
                    }
                    if(keyMod_strg_AP){
                        if (getActionHandler().hasEdgeTo(nodeAP, nodeAR)) {
                            getActionHandler().removeDirEdge(nodeAP, nodeAR);
                        }else{
                            getActionHandler().addDirEdge(nodeAP, nodeAR, weight);
                        }
                        
                    }else{
                        if(getActionHandler().isEdgeBetween(nodeAP, nodeAR)) {
                            getActionHandler().removeEdge(nodeAP, nodeAR);
                        }else{
                            getActionHandler().addEdge(nodeAP, nodeAR, weight);
                        }
                    }
                    //System.out.println(nodeAP+"-"+nodeAR);

                }
                getActionHandler().redraw_None();
                break;
            case 1://wenn mit linker Maustaste gezogen
                if((selectedNodes!=null||nodeAR>=0)&&1<=pressPos.distTo(e.getX(), e.getY())){
                    //getActionHandler().syncPosToModel(nodeAR);
                    if(selectedNodes!=null){
                        for (int node: selectedNodes){
                            NodePresentation np = getActionHandler().getController().getModel().getNodePresentation(node);
                            NodeComponent nc = getActionHandler().getNodeById(node);
                            np.setPos(nc.getPositionX(), nc.getPositionY());
                        }
                    }else{
                        NodePresentation np = getActionHandler().getController().getModel().getNodePresentation(nodeAR);
                        NodeComponent nc = getActionHandler().getNodeById(nodeAR);
                        np.setPos(nc.getPositionX(), nc.getPositionY());
                    }

                    getActionHandler().redraw_Rescale();
                }
                break;
            default:
                break;
        }
            
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //assert(viewState!=null);
        //MouseEvent me = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    /**
     * speichert Zeitpunkt der letzten Maus-Scroll-Betätigung
     */
    private long lastMwe = System.currentTimeMillis();

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        long currMwe = System.currentTimeMillis();
        long timeSinceLastMwe = currMwe-lastMwe;//geschw 1step/tslm
        lastMwe = currMwe;
        int scrollSpeedMod = Math.min(1+(int)Math.round(500/Math.max((((int)timeSinceLastMwe)),1)),10);//500 const

        //zoom überarbeiten?
        if(keyMod_strg){
            double zoomInc = -0.1 * e.getWheelRotation()*Math.max(scrollSpeedMod/20,1);//20 const
            double zoomFac = 1 + zoomInc;
            double totalZoomFac = getViewState().getZoom() * zoomFac;

        
            //wenn zoomwert out of bounds gehen würde, wird der andre kak direkt geskippt
            if ((getViewState().getZoom()==10&&zoomFac<1)||(getViewState().getZoom()>500&&zoomFac>1)){
                return;
            }

            //enden schön rund machen
            if (totalZoomFac > 500)totalZoomFac = 500;
            if (totalZoomFac < 10)totalZoomFac = 10;
        
            //Dumme kacke arschritze 3h meines Lebens und doch nix geworden nichtmal Ki degah ich scheiß mir ein wär ich mal schlafen gegangen logischster kack überhaupt kann nicht verstehen warum das so lange gedauert hat ich schwöre ich nehme mir das Leben Amina Koyim
            ActualPoint ap = new ActualPoint(e.getX(), e.getY());
       
            InSysPoint mousePointInSystemBeforeZoom = getViewState().getPointInSystem(ap);
            getViewState().setZoom(totalZoomFac);
            InSysPoint mousePointInSystemAfterZoom = getViewState().getPointInSystem(ap);

            double offX = -mousePointInSystemAfterZoom.getX()+mousePointInSystemBeforeZoom.getX();
            double offY = -mousePointInSystemAfterZoom.getY()+mousePointInSystemBeforeZoom.getY(); 
            getViewState().setOffsetX((int)Math.round(getViewState().getOffsetX()+offX*getViewState().getZoom()));
            getViewState().setOffsetY((int)Math.round(getViewState().getOffsetY()+offY*getViewState().getZoom()));

            getActionHandler().redraw_Rescale();
        }else if(keyMod_shift) {
            getViewState().setOffsetX(getViewState().getOffsetX()+e.getWheelRotation()*scrollSpeedMod);
            getActionHandler().redraw_Move();
        }else{
            getViewState().setOffsetY(getViewState().getOffsetY()+e.getWheelRotation()*scrollSpeedMod);
            getActionHandler().redraw_Move();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        ActualPoint dragTo_AP = new ActualPoint(e.getX(), e.getY()); //Puntk an den die Maus gezogen wurde
        
        //Versatz um den gezogen wurde
        int dragX = dragTo_AP.getX() - lastMousePos.getX(); int dragY = dragTo_AP.getY() - lastMousePos.getY();
        switch (pressButt) {
            case 1://linke Maustaste
                if(selectedNodes == null){//wenn nicht von einem Knoten gezogen wird dann Kamera bewegen
                    getViewState().setOffsetX(getViewState().getOffsetX()-dragX); getViewState().setOffsetY(getViewState().getOffsetY()-dragY);
                    getActionHandler().redraw_Move();
                }else{//sonst die ausgewählten Knoten verschieben
                    for (int i : selectedNodes) {
                        getActionHandler().nodeToFront(i);
                        NodeComponent nc = getActionHandler().getNodeById(i);
    
                        InSysPoint newPos = getViewState().getPointInSystem(new ActualPoint(nc.getAcPosX()+dragX,nc.getAcPosY()+dragY));
                        nc.setPositionX(newPos.getX()); nc.setPositionY(newPos.getY());
                    }
                    getActionHandler().redraw_Move();
                    getActionHandler().setHasUnsavedChanges();
                }
                break;
            case 3://rechte Maustaste
                int nodeAP = getActionHandler().getNodeAtPoint(pressPos);
                if(nodeAP>=0){//wenn von einem Knoten aus gezogen wird, ziehe neue Kante
                        ActualPoint[] prev = getActionHandler().getEdgePreview();
                        prev[0] = new ActualPoint(getActionHandler().getNodeById(nodeAP).getAcPosX(),getActionHandler().getNodeById(nodeAP).getAcPosY());
                        prev[1] = dragTo_AP;
                }else{//sonst zeichne Auswahlrechteck
                ActualPoint[] sl = getActionHandler().getSlRectPts();
                sl[0] = pressPos;
                sl[1] = dragTo_AP;
            }
                getActionHandler().redraw_None();
                break;
            default:
                break;
        }
        
        lastMousePos.setX(dragTo_AP.getX()); lastMousePos.setY(dragTo_AP.getY());
    }

    /**
     * Setzt Tastenmodifikatoren auf false
     */
    public void rmKeymods(){
        keyMod_strg = false;
        keyMod_shift = false;
        getViewState().setDisplayWeightBlobs(false);
        getActionHandler().redraw_None();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }


    //window
    @Override
    public void windowOpened(WindowEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(WindowEvent e) {
        getActionHandler().closeWindow();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
