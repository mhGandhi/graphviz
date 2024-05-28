package app.view.point;

import java.awt.event.MouseEvent;

/**
 * Stellt einen Punkt auf dem Fenster dar
 */
public class ActualPoint extends Vec2<Integer>{

////////////////////////////////////////////////////////////////////////////////////////////////////cons

    /**
     * Verortung des Punktes nach Pixel des Fensters
     * @param pX
     * @param pY
     */
    public ActualPoint(int pX, int pY){
        super(pX, pY);
    }

    /**
     * erstellt einen Actualpoint an den x bzw. y Koordinaten des Mouseevents e
     * @param e
     */
    public ActualPoint(MouseEvent e){
        super(e.getX(), e.getY());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Kalkuliert die Distanz in Pixeln zwischen objekt und ausgewählten Punkt
     * @param pX
     * @param pY
     * @return Distanz (Pixel)
     */
    public double distTo(int pX, int pY){
        int distX = pX-this.getX();
        int distY = pY-this.getY();
        return Math.sqrt(distX*distX+distY*distY);
    }
}
