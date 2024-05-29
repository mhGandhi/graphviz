package app.view;

import app.view.point.ActualPoint;
import app.view.point.InSysPoint;

import java.awt.event.MouseEvent;
/**
 * Definiert aktuellen zustand der Anzeige (state of the view)
 */
public class ViewState {

////////////////////////////////////////////////////////////////////////////////////////////////////attribute
    
    /**
     * Versatz der Kamera auf der X Achse
     */
    private int offsetX;

    /**
     * Versatz der Kamera auf der Y Achse
     */
    private int offsetY;

    /**
     * Zoom der Kamera
     */
    private double zoom;

    
    /**
     * ob das Grid im Hintergrund angezeigt werden soll
     */
    private boolean showGrid;

    /**
     * ob die Grenzen der Knoten angezeigt werden sollen
     */
    private boolean showBounds;

    /**
     * ob die Kantengewichtungen hervorgehoben werden sollen
     */
    private boolean displayWeightBlobs;

    /**
     * ob die Kantengewichtungen angezeigt werden sollen
     */
    private boolean showWeight;


    /**
     * Theme
     */
    private Theme theme;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    public Theme getTheme() {
        return theme;
    }
    
    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowBounds() {
        return showBounds;
    }

    public void setShowBounds(boolean showBounds) {
        this.showBounds = showBounds;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double pZoom) {
        if (pZoom<5)pZoom = 5;
        if (pZoom>800) pZoom = 800;
        this.zoom = pZoom;
    }

    public boolean isDisplayWeightBlobs() {
        return displayWeightBlobs;
    }

    public void setDisplayWeightBlobs(boolean displayWeightBlobs) {
        this.displayWeightBlobs = displayWeightBlobs;
    }

    public boolean isShowWeight() {
        return showWeight;
    }

    public void setShowWeight(boolean showWeight) {
        this.showWeight = showWeight;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////cons

    /**
     * initialisiert den Viewstate
     * @param pT Theme
     */
    public ViewState(Theme pT){
        offsetX = 0;
        offsetY = 0;

        zoom = 50.0;

        showGrid = true;
        showBounds = false;
        
        this.theme = pT;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Konvertiert Punkt eines MouseEvents zu Punkt im System
     * @param e
     * @return 
     */
    public InSysPoint getPointInSystem(MouseEvent e){
        return getPointInSystem(new ActualPoint(e.getX(), e.getY()));
    }

    /**
     * Konvertiert Punkt auf Bildschirm zu Punkt im System
     * @param pAP
     * @return Punkt im System
     */
    public InSysPoint getPointInSystem(ActualPoint pAP){
        assert(pAP!=null);

        double isX = (pAP.getX()+getOffsetX())/getZoom();
        double isY = (pAP.getY()+getOffsetY())/getZoom();
        /*
        double isX = pAP.getX()/getZoom()+getOffsetX();
        double isY = pAP.getY()/getZoom()+getOffsetY();
        */
        return new InSysPoint(isX, isY);
    }

    /**
     * Konvertiert System Punkt in Anzeige Punkt
     * @param pIS
     * @return Punkt in der Anzeige
     */
    public ActualPoint getActualPoint(InSysPoint pIS){
        assert(pIS!=null);

        
        int apX = (int)Math.round(pIS.getX()*getZoom()-getOffsetX());
        int apY = (int)Math.round(pIS.getY()*getZoom()-getOffsetY());

        return new ActualPoint(apX, apY);
    }

    /**
     * setzt die Kamera zurück
     */
    public void resetCam(){
        this.offsetX = 0;
        this.offsetY = 0;
        this.zoom = 50;
    }
}
