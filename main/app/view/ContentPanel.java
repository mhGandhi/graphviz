package app.view;

import app.view.point.ActualPoint;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Panel zur Darstellung der Inhalte
 */
public class ContentPanel extends JPanel{

////////////////////////////////////////////////////////////////////////////////////////////////////attribute

    /**
     * viewstate der Darstellung
     */
    private ViewState viewState;
    
    /**
     * gibt an, in welchem Modus die nächste Aktualisierung statt zu finden hat
     */
    private RedrawModes nextRedraw;

    /**
     * Liste der Knoten-Komponente
     */
    private List<NodeComponent> nodes;

    /**
     * Liste der Kanten-Komponente
     */
    private List<EdgeComponent> edges;

    /**
     * Liste der Knoten die ausgewählt sind
     */
    private List<Integer> selectedNodes;

    /**
     * Eckpunkte des Auswahl-Rechteckes
     */
    private ActualPoint[] slRectPts;

    /**
     * Definiert Enden des Kantenzeichnungs-Previews
     */
    private ActualPoint[] edgePreview;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    public List<EdgeComponent> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeComponent> pEdg){
        this.edges = pEdg;
    }

    private RedrawModes getNextRedrawMode(){
        return this.nextRedraw;
    }

    public void setNextRedrawMode(RedrawModes pMode){
        this.nextRedraw = pMode;
    }

    private ViewState getViewState(){
        return this.viewState;
    }

    public List<NodeComponent> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeComponent> ls) {
        nodes = ls;
    }

    public List<Integer> getSelectedNodes() {
        return selectedNodes;
    }

    public ActualPoint[] getSlRectPts(){
        return this.slRectPts;
    }

    public ActualPoint[] getEdgePreview(){
        return this.edgePreview;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons
    /**
     * Initialisiert die Anzeige
     * @param pViewState
     */
    public ContentPanel(ViewState pViewState){
        this.viewState = pViewState;
        this.nextRedraw = RedrawModes.RESCALE;

        this.nodes = new LinkedList<NodeComponent>();
        this.selectedNodes = new LinkedList<Integer>();
        this.slRectPts = new ActualPoint[2];
        this.edgePreview = new ActualPoint[2];

        this.edges = new LinkedList<EdgeComponent>();

        setBackground(getViewState().getTheme().getBackgroundColor());
        setPreferredSize(new Dimension(720,500));
        setLayout(null);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gibt NodeComponent, welcher durch bestimmte Id definiert wird zurück
     * @param pId Id des Knotens, der zurückgegeben werden soll
     * @return NodeComponent mit gegebener Id
     */
    public NodeComponent getNodeById(int pId){
        for (NodeComponent nc : getNodes()) {
            if(nc.getId() == pId)return nc;
        }
        return null;
    }

    /**
     * Verschiebt Knoten mit bestimmter Id in die vorderste Ebene (#1 in CompList)
     * @param pId Id des zu verschiebenden Knotens
     */
    public void nodeToFront(int pId){
        int i = 0;
        for (i = 0; i < getNodes().size(); i++) {
            if(getNodes().get(i).getId()==pId)break;
        }

        for (int j = i; j > 0; j--) {
            swap(j,j-1);
        }
    }

    /**
     * Tauscht 2 Knoten in der Komponentenliste miteinander
     * @param pI1
     * @param pI2
     */
    private void swap(int pI1, int pI2){
        NodeComponent tmp = getNodes().get(pI1);
        getNodes().set(pI1,getNodes().get(pI2));
        getNodes().set(pI2,tmp);
    }

    /**
     * gibt Knoten an gegebenem Punkt zurück
     * @param pP Punkt, der überprüft werden soll
     * @return ID des Knotens an Punkt (-1 wenn keiner)
     */
    public int getFirstNodeAtPoint(ActualPoint pP){
        for (int i = 0; i < getNodes().size(); i++){
            if(getNodes().get(i).contains(pP))return getNodes().get(i).getId();
        }
        return -1;
    }

    /**
     * Gibt die Ids der Knoten im gegebenen Rechteck wieder
     * @param pRectangle
     * @return Array mit Ids der Knoten in dem Rechteck
     */
    public int[] getNodesInRect(Rectangle pRectangle){
        List<Integer> nds = new LinkedList<Integer>();

        for (int i = 0; i < getNodes().size(); i++) {
            NodeComponent node = getNodes().get(i);

            if( (node.getAcPosX()>=pRectangle.getX()&&node.getAcPosX()<=pRectangle.getX()+pRectangle.getWidth())&&
                (node.getAcPosY()>=pRectangle.getY()&&node.getAcPosY()<=pRectangle.getY()+pRectangle.getHeight())){
                    nds.add(node.getId());
            }
        }

        int[] ret = new int[nds.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = nds.get(i);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ////Hintergrund einzeichnen
        if(getViewState().isShowGrid()){
            Color bgHintsColOr = getViewState().getTheme().getBackgroundHintsColor();
            g2d.setColor(bgHintsColOr);
            g2d.setColor(new Color(g2d.getColor().getRed(),g2d.getColor().getGreen(),g2d.getColor().getBlue(), 64));
            for (int i = (int)Math.round((-viewState.getOffsetX())%(2*(viewState.getZoom()*1))); i < getBounds().getWidth(); i+=2*(int)Math.round(viewState.getZoom()*1)) {
                g2d.drawLine(i, 0, i, (int)getBounds().getHeight());
                if(viewState.getZoom()>=20&&i>20){
                    Font f = new Font(getViewState().getTheme().getFont(), Font.PLAIN, 20);
                    g2d.setFont(f);
                    g2d.drawString(""+(Math.round((viewState.getOffsetX()+i)/viewState.getZoom())), i+1, 20);
                }
            }
            for (int i = (int)Math.round((-viewState.getOffsetY())%(2*(viewState.getZoom()*1))); i < getBounds().getHeight(); i+=2*(int)Math.round(viewState.getZoom()*1)) {
                g2d.drawLine(0, i, (int)getBounds().getWidth(), i);
                if(viewState.getZoom()>=20&&i>20){
                    g2d.drawString(""+(Math.round((viewState.getOffsetY()+i)/viewState.getZoom())), 0, i-1);
                }
            }
        }

        Font font = new Font(getViewState().getTheme().getFont(), Font.PLAIN, (int)Math.round(viewState.getZoom()/7.5));
        g2d.setFont(font);

        ////Kantenpreview einzeichnen
        if(getEdgePreview()[0]!=null&&getEdgePreview()[1]!=null){
            ActualPoint start = getEdgePreview()[0];
            ActualPoint end = getEdgePreview()[1];
            g2d.setColor(viewState.getTheme().getWritingOnBackgroundColor());
            g2d.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        }

        ////Knoten einzeichnen
        if(!getNodes().isEmpty()){
            for (int i = getNodes().size()-1; i >= 0; i--){
                paintComp(getNodes().get(i),g2d);
            }
        }

        ////Kanten einzeichnen
        if(!getEdges().isEmpty()){
            for (int i = getEdges().size()-1; i >= 0; i--) {
                paintComp(getEdges().get(i),g2d);
            }
        }

        ////Selektion um Knoten einzeichnen
        g2d.setColor(getViewState().getTheme().getSelectionColor());
        for (int i = 0; i < getSelectedNodes().size(); i++){
            Rectangle bounds = getNodeById(getSelectedNodes().get(i)).getBounds();
            if(getVisibleRect().intersects(bounds)){
                g2d.drawRect((int)Math.round(bounds.getX()), (int)Math.round(bounds.getY()), (int)Math.round(bounds.getWidth()), (int)Math.round(bounds.getHeight()));
            }
        }
        ////Selektionsrechteck Einzeichnen
        Rectangle sl = getRect(getSlRectPts()[0], getSlRectPts()[1]);
        if (sl.getWidth()>0&&sl.getHeight()>0) {
            g2d.drawRect((int)Math.round(sl.getX()), (int)Math.round(sl.getY()), (int)Math.round(sl.getWidth()), (int)Math.round(sl.getHeight()));
            g2d.setColor(new Color(g2d.getColor().getRed(),g2d.getColor().getGreen(),g2d.getColor().getBlue(), 64));
            g2d.fillRect((int)Math.round(sl.getX()), (int)Math.round(sl.getY()), (int)Math.round(sl.getWidth()), (int)Math.round(sl.getHeight()));
        }
    }

    /**
     * Gibt durch 2 Eckpunkte definiertes Rechteck zurück
     * @param c1 Eckpunkt 1
     * @param c2 Eckpunkt 2
     * @return Resultierendes Rechteck
     */
    public Rectangle getRect(ActualPoint c1, ActualPoint c2){
        if(c1==null||c2==null)return new Rectangle();
        int tlX = Math.min(c1.getX(),c2.getX());
        int tlY = Math.min(c1.getY(),c2.getY());
        int brX = Math.max(c1.getX(),c2.getX());
        int brY = Math.max(c1.getY(),c2.getY());
        return new Rectangle(tlX,tlY,brX-tlX,brY-tlY);
    }

    /**
     * Zeichnet Komponente entprechend des durch das Attribut nextRedrawNode vorgegebenen Aktualisierungsmodus
     * @param pC zu Zeichnende Komponente
     * @param g2d Graphic-Objekt
     */
    private void paintComp(CComponent pC, Graphics2D g2d){
        switch (getNextRedrawMode()) {
            case RESCALE -> pC.paintCompAZ(g2d, getVisibleRect());
            case MOVE -> pC.paintCompAM(g2d, getVisibleRect());
            case NONE -> pC.paintComp(g2d, getVisibleRect());
            default -> pC.paintCompAZ(g2d, getVisibleRect());
        }
    }

    /**
     * Bestimmt Markierungsstatus der Kante zwischen 2 Knoten
     * @param pNode1Id Id des Ausgangsknotens
     * @param pNode2Id Id des Zielknotens
     * @param pMark ob die Kante markiert sein soll
     */
    public void setEdgeMarked(int pNode1Id, int pNode2Id, boolean pMark){
        EdgeComponent e = null;
        for (EdgeComponent ec : getEdges()) {
            if(ec.getFrom().getId()==pNode1Id && ec.getTo().getId()==pNode2Id){
                e = ec;
                break;
            }
        }
        if(e == null)return;
        if(e.isBidirect()){
            setDirEdgeMarked(pNode1Id, pNode2Id, pMark);
            setDirEdgeMarked(pNode2Id, pNode1Id, pMark);
        }else{
            setDirEdgeMarked(pNode1Id, pNode2Id, pMark);
        }
        
    }

    /**
     * Bestimmt Markierungsstatus der gerichteten Kante zwischen 2 Knoten
     * @param pNode1Id Id des Ausgangsknotens
     * @param pNode2Id Id des Zielknotens
     * @param pMark ob die Kante markiert sein soll
     */
    public void setDirEdgeMarked(int pNode1Id, int pNode2Id, boolean pMark){
        for (EdgeComponent ec : getEdges()) {
            if(ec.getFrom().getId()==pNode1Id && ec.getTo().getId()==pNode2Id){
                ec.setMarked(pMark);
                break;
            }
        }
    }
}