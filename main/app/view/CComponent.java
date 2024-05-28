package app.view;

import java.awt.*;

/**
 * Interface zur Definition von Komponenten im Kontext des ContentPanels
 */
public interface CComponent {
    
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ViewState getViewState();

    Rectangle getBounds();

    /**
     * Aufgerufen für repaint
     * @param g2d Graphics-Objekt
     * @param pR Bounds des Fensters
     */
    void paintComp(Graphics2D g2d, Rectangle pR);

    /**
     * Aufgerufen für repaint nach Bewegung
     * @param g2d Graphics-Objekt
     * @param pR Bounds des Fensters
     */
    void paintCompAM(Graphics2D g2d, Rectangle pR);

    /**
     * Aufgerufen für repaint nach Zoom
     * @param g2d Graphics-Objekt
     * @param pR Bounds des Fensters
     */
    void paintCompAZ(Graphics2D g2d, Rectangle pR);
}
