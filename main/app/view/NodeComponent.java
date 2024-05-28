package app.view;

import app.view.point.ActualPoint;
import app.view.point.InSysPoint;

import java.awt.*;
import java.util.Objects;

/**
 * Definiert Darstellung von Knoten innerhalb des ContentPanels
 */
public class NodeComponent implements CComponent{

////////////////////////////////////////////////////////////////////////////////////////////////////attribute

    /**
     * viewstate der Darstellung
     */
    private ViewState viewState;

    /**
     * Id des Knotens
     */
    private int id;

    /**
     * Label des Knotens
     */
    private String label;

    /**
     * Zwischenspeicher für die Zeilen des Labels des Knotens
     */
    private String[] lines;

    /**
     * Zwischenspeicher für die Offsets der Zeilen des Labels des Knotens
     */
    private double[] lOffsets;

    /**
     * Zwischenspeicher für Font
     */
    private Font font;

    /**
     * Zwischenspeicher für aktuellen Durchmesser der Darstellung
     */
    private double currDiameter;

    /**
     * Farbe des Knotens
     */
    private Color color;

    /**
     * PosX im System
     */
    private double positionX;
    /**
     * PosY im System
     */
    private double positionY;

    /**
     * PosX auf Bildschirm
     */
    private int acPosX;
    /**
     * PosY auf Bildschirm
     */
    private int acPosY;

    //kann da noch was o wha; agar io????
    /**
     * Gröé des Knotens
     */
    private double circleSize = 1;

    /**
     * ob der Knoten markiert ist
     */
    private boolean marked;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    public double getCircleSize() {
        return circleSize;
    }
    public void setCircleSize(double circleSize) {
        this.circleSize = circleSize;
    }
    public int getAcPosX() {
        return acPosX;
    }
    public void setAcPosX(int acPosX) {
        this.acPosX = acPosX;
    }
    public int getAcPosY() {
        return acPosY;
    }
    public void setAcPosY(int acPosY) {
        this.acPosY = acPosY;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Font getFont() {
        return font;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public double getCurrDiameter() {
        return currDiameter;
    }
    public void setCurrDiameter(double currDiameter) {
        this.currDiameter = currDiameter;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public double getPositionX(){
        return this.positionX;
    }
    public void setPositionX(double pX){
        this.positionX = pX;
    }
    public double getPositionY(){
        return this.positionY;
    }
    public void setPositionY(double pY){
        this.positionY = pY;
    }

    public String[] getLines(){
        return this.lines;
    }
    public void setLines(String[] pL){
        this.lines = pL;
    }

    public double[] getLOffsets(){
        return this.lOffsets;
    }
    public void setLOffsets(double[] pLO){
        this.lOffsets = pLO;
    }

    public ViewState getViewState(){
        return this.viewState;
    }

    public boolean isMarked(){
        return this.marked;
    }

    public void setMarked(boolean pMarked){
        this.marked = pMarked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds(){
        return new Rectangle((int)Math.round(getAcPosX()-getCurrDiameter()/2),(int)Math.round(getAcPosY()-getCurrDiameter()/2),(int)Math.round(getCurrDiameter()),(int)Math.round(getCurrDiameter()));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons

    /**
     * Erstellt eine Node mit gegebenen Eigenschaften
     * @param pId       Id des Knotens
     * @param pX        X-Koordinate des Knotens
     * @param pY        Y-Koordinate des Knotens
     * @param pColHex   Farbcode für Farbe des Knotens
     * @param pLabel    Beschriftung des Knotens
     * @param pVS       viewstate der Darstellung
     */
    public NodeComponent(int pId, double pX, double pY, String pColHex, String pLabel, ViewState pVS){
        this.id = pId;
        this.positionX = pX;
        this.positionY = pY;

        this.viewState = pVS;

        if(pColHex!=null&&!pColHex.isBlank())
        try {
            this.color = Theme.decodeColor(pColHex);
        } catch (Exception e) {
            e.printStackTrace();
            this.color = null;
        }
 
        this.label = pLabel;

        calcAM();
        calcAZ();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gibt zurück, ob ein bestimmter Punkt innerhalb des Knotens liegt
     * @param pPt
     * @return
     */
    public boolean contains(ActualPoint pPt){
        if(pPt.distTo(getAcPosX(), getAcPosY())<=getCurrDiameter()/2)return true;
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComp(Graphics2D g2d, Rectangle pRec){
        if (!pRec.intersects(getBounds())) {
            return;
        }
        //System.out.println("pt node "+this);

        int diam = (int)Math.round(getCurrDiameter());

        int posX = (Math.round(getAcPosX()-diam/2));
        int posY = (Math.round(getAcPosY()-diam/2));
        
        /*
         * a²+b²=c²
         * a = b
         * 2a² = c²
         * 
         * a=sqrt(c²/2)
         */
        int innerSq = (int)Math.round(Math.sqrt((diam*diam)/2));
        int outToIn = (diam-innerSq)/2;

        Rectangle innerBounds = new Rectangle(posX+outToIn, posY+outToIn, innerSq, innerSq);

        
        boolean hasCusCol = (getColor()!=null);
        //-34 64 287
        //100 120 750
        if(hasCusCol){
            g2d.setColor(getColor());
            g2d.fillOval(posX,posY,diam,diam);
        }
        g2d.setColor(viewState.getTheme().getDefaultNodeColor());
        if(hasCusCol){
            g2d.drawOval(posX, posY, diam, diam);
        }else{
            g2d.fillOval(posX, posY, diam, diam);
        }

        if(isMarked()){
            g2d.setColor(viewState.getTheme().getMarkingColor());
            g2d.drawOval(posX,posY,diam,diam);
        }

        if(viewState.isShowBounds()){
            g2d.setColor(Color.GREEN);

            g2d.drawString("id "+getId(), (int)(getAcPosX()-getCurrDiameter()/2), (int)(getAcPosY()-getCurrDiameter()/2));
            g2d.drawLine(0, posY+diam/2, pRec.width, posY+diam/2); g2d.drawLine(posX+diam/2, 0, posX+diam/2, pRec.height);
            g2d.draw(getBounds());
            g2d.draw(innerBounds);
        }
        //g2d.setColor(Color.GREEN);  /*g2d.drawLine(getBounds().x, getBounds().y+diam/2, getBounds().x+diam, getBounds().y+diam/2); g2d.drawLine(getBounds().x+diam/2, getBounds().y, getBounds().x+diam/2, getBounds().y+diam);*/ g2d.setColor(Color.WHITE);

        //label
        if(pRec.intersects(innerBounds)&&g2d.getFont().getSize()>=5){
            
            if (hasCusCol) {
                double brightness = (299 * getColor().getRed() + 587 * getColor().getGreen() + 114 * getColor().getBlue()) / 1000; //heil dir Stackoverflow
                if(brightness>=128){
                    g2d.setColor(Color.black);
                }else{
                    g2d.setColor(Color.white);
                }
            }else{
                g2d.setColor(viewState.getTheme().getWritingOnDefaultNodeColor());
            }

            String[] fitStr = getLines();
            if(fitStr!=null){
                double[] lineOffs = getLOffsets();
                //g2d.setColor(Color.GREEN); g2d.drawRect(innerBounds.x,innerBounds.y,innerBounds.width,innerBounds.height); g2d.setColor(Color.BLACK);
                
                TextRect.paint(g2d, posX+outToIn, posY+outToIn, fitStr, lineOffs);
                /*
                for (int i = 0; i < fitStr.length; i++) {
                    System.out.println(pos.getX()+" "+outToIn+" "+Math.round(lineOffs[i]));
                    g2d.drawLine(innerBounds.x,innerBounds.y,(int)(innerBounds.x+lineOffs[i]),innerBounds.y);
                    g2d.drawString(fitStr[i], (int)Math.round(pos.getX()+outToIn+lineOffs[i]), (int)Math.round(pos.getY()+outToIn+(i+1)*lineOffs[lineOffs.length-2]+lineOffs[lineOffs.length-1]));
                }
                */
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintCompAM(Graphics2D g2d, Rectangle pRec){

        calcAM();

        //System.out.println("pt at "+getAcPosX()+" "+getAcPosY()+" w s "+getCurrDiameter());
        paintComp(g2d, pRec);
    }

    /**
     * Notwendige berechnungen nach Bewegung
     */
    public void calcAM(){
        ActualPoint actPos = getViewState().getActualPoint(new InSysPoint(getPositionX(), getPositionY()));

        int actPosX = actPos.getX();
        int actPosY = actPos.getY();
        
        setAcPosX(actPosX);
        setAcPosY(actPosY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintCompAZ(Graphics2D g2d, Rectangle pRec){
        calcAZ();
        int innerSq = (int)Math.round(Math.sqrt((getCurrDiameter()*getCurrDiameter())/2));
        double outToIn = (getCurrDiameter()-innerSq)/2;

        Rectangle innerBounds = new Rectangle((int)Math.round(getAcPosX()+outToIn), (int)Math.round(getAcPosY()+outToIn), innerSq, innerSq);

        setLines(TextRect.getFittingString(getLabel(),g2d,innerBounds));
        setLOffsets(TextRect.getLineOffsetsForRect(getLines(),g2d,innerBounds));
        paintCompAM(g2d, pRec);
    }

    /**
     * Notwendige Berechnungen nach Skalierung
     */
    public void calcAZ(){
        setCurrDiameter(getCircleSize()*getViewState().getZoom());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj){

        NodeComponent nd = (NodeComponent) obj;
        return getId() == nd.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return Objects.hash(getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return getId()+"-"+getLabel()+"-["+getPositionX()+"|"+getPositionY()+"]";
    }
}
