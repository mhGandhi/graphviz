package app.view;

/**
 * Repräsentiert Tool in der View
 */
public class ToolMenuItemSpecs {
    /**
     * Name des assoziierten Tools
     */
    private final String name;

    /**
     * Id des assoziierten Tools
     */
    private final String id;

    /**
     * ob das assoziierte Tool von der Menüleiste aus ausgeführt werden kann
     */
    private final boolean runnableFromMenuBar;
    
    /**
     * ob das assoziierte Tool von Knoten aus ausgeführt werden kann
     */
    private final boolean runnableFromNode;

    /**
     * ob das assoziierte Tool von Punkten aus ausgeführt werden kann
     */
    private final boolean runnableFromSpot;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public boolean isRunnableFromMenuBar() {
        return runnableFromMenuBar;
    }
    public boolean isRunnableFromNode() {
        return runnableFromNode;
    }
    public boolean isRunnableFromSpot() {
        return runnableFromSpot;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons
    /**
     * 
     * @param pName         Name des assoziierten Tools
     * @param pId           Id des assoziierten Tools
     * @param pRFMenuBar    ob das assoziierte Tool von der Menüleiste aus ausgeführt werden kann
     * @param pRFNode       ob das assoziierte Tool von Knoten aus ausgeführt werden kann
     * @param pRFSpot       ob das assoziierte Tool von Punkten aus ausgeführt werden kann
     */
    public ToolMenuItemSpecs(String pName, String pId, boolean pRFMenuBar, boolean pRFNode, boolean pRFSpot){
        this.name = pName;
        this.id = pId;
        this.runnableFromMenuBar = pRFMenuBar;
        this.runnableFromNode = pRFNode;
        this.runnableFromSpot = pRFSpot;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
}
