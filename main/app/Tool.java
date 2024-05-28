package app;

import app.view.point.InSysPoint;

/**
 * Interface für die Interaktion mit UserTools
 */
public interface Tool {

    /**
     * Soll möglichst einzigartige Id zurückgeben
     * @return Id
     */
    public String getId();

    /**
     * Soll Sprachenunabhängig Namen zurückgeben
     * @return Name
     */
    public String getName();

    /**
     * Soll Sprachenunabhängig Beschreibung zurückgeben
     * @return Beschreibung
     */
    public String getDescription();

    /**
     * Soll Autor zurückgeben
     * @return Autor
     */
    public String getAuthor();

    /**
     * gibt zurück ob Tool im Menü angezeigt werden soll
     * @return ob Tool in Menü angezeigt werden soll
     */
    public boolean showInMenu();

    /**
     * gibt zurück ob Tool in Menüleiste angezeigt werden soll
     * @return ob Tool in Menüleiste angezeigt werden soll
     */
    public boolean canBeRanFromMenuBar();

    /**
     * gibt zurück ob Tool als Nodeaction angezeigt werden soll
     * @return ob Tool als Nodeaction angezeigt werden soll
     */
    public boolean canBeRanFromNode();

    /**
     * gibt zurück ob Tool als Backgroundaction angezeigt werden soll
     * @return ob Tool als Backgroundaction angezeigt werden soll
     */
    public boolean canBeRanFromSpot();

    /**
     * Methode die ausgeführt wird, wenn das Tool aus der Menüleiste gestartet wird
     * @param pC Controller des Programmes zur Ausführung von Befehlen
     */
    public void runFromMenuBar(Controller pC);

    /**
     * Methode die ausgeführt wird, wenn das Tool von einem Knoten aus gestartet wird
     * @param pC        Controller des Programmes zur Ausführung von Befehlen
     * @param pNodeId   Node, von der aus das Tool ausgeführt wird
     */
    public void runOnNode(Controller pC, int pNodeId);

    /**
     * Methode die ausgeführt wird, wenn das Tool von einem Punkt aus gestartet wird
     * @param pC        Controller des Programmes zur Ausführung von Befehlen
     * @param pPt       Punkt, von dem aus das Tool ausgeführt wird
     */
    public void runAtSpot(Controller pC, InSysPoint pPt);
}
