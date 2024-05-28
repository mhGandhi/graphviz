package app.model;

import java.io.Serializable;

/**
 * Die NodePresentation Klasse dient der Festlegung der Darstellung eines Knotens.
 * Es wird sowohl das Aussehen, wie auch die Position auf dem Feld des Knotens hier festgehalten.
 */
public class NodePresentation implements Serializable{

////////////////////////////////////////////////////////////////////////////////////////////////////attributes
    /**
     * Anzeigename für den Knoten, standardmäßig leer.
     */
    private String label;

    /**
     * Konkreter Farbcode für die Anzeige des Knotens.
     */
    private String colorCode;

    /**
     * Position der Visualisierung des Knotens auf der X Achse.
     */
    private double posX;

    /**
     * Position der Visualisierung des Knotens auf der Y Achse.
     */
    private double posY;

    /**
     * Arschknoten
     */
    private int assNode;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set
    public String getLabel(){
        return label;
    }

    /**
     * 
     * @param label wenn null dann Label = ""
     */
    public void setLabel(String pLabel){
        if(pLabel==null) pLabel = "";

        this.label = pLabel;
    }

    public String getColorCode(){
        return colorCode;
    }

    public void setColorCode(String colorCode){
        this.colorCode = colorCode;
    }

    public double getPosX(){
        return posX;
    }

    public void setPosX(double posX){
        this.posX = posX;
    }

    public double getPosY(){
        return posY;
    }

    public void setPosY(double posY){
        this.posY = posY;
    }

    public int getAssNode(){
        return this.assNode;
    }

    public void setPos(double pX, double pY) {
        setPosX(pX);
        setPosY(pY);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////cons
    /**
     * 
     * @param pAssNode Id des assozieerten Knotens
     */
    public NodePresentation(int pAssNode){
        this(pAssNode,"",null,0,0);
    }

    /**
     * @param pAssNode Id des assozieerten Knotens
     * @param pPosX Position der Präsentation X
     * @param pPosY Position der Präsentation Y
     */
    public NodePresentation(int pAssNode, int pPosX, int pPosY){
        this(pAssNode,"",null,pPosX,pPosY);
    }

    /**
     * @param pAssNode Id des assozieerten Knotens
     * @param pLabel Label des Knotens
     * @param pColorCode Farbe des Knotens
     * @param pPosX Position auf der X-Achse
     * @param pPosY Position auf der Y-Achse
     */
    public NodePresentation(int pAssNode, String pLabel, String pColorCode, int pPosX, int pPosY){
        this.assNode = pAssNode;

        if(pLabel==null) pLabel = "";

        this.label = pLabel;
        this.colorCode = pColorCode;
        this.posX = pPosX;
        this.posY = pPosY;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        NodePresentation np = (NodePresentation) obj;
        return this.getAssNode() == np.getAssNode();
    }
}