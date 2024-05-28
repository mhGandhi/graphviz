package app.view;

import app.view.point.ActualPoint;
import app.view.point.InSysPoint;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * Definiert Darstellung von Kanten innerhalb des ContentPanels (edging hard)
 */
public class EdgeComponent implements CComponent{

////////////////////////////////////////////////////////////////////////////////////////////////////attribute

    /**
     * viewstate der Darstellung
     */
    private ViewState viewState;

    /**
     * Knoten, von dem die Kante ausgeht
     */
    private NodeComponent from;

    /**
     * Knoten, auf den die Kante gerichtet ist
     */
    private NodeComponent to;

    /**
     * Zwischenspeicher für den Punkt des Knotens, von dem die Kante ausgeht
     */
    private ActualPoint fromP;

    /**
     * Zwischenspeicher für den Punkt des Knotens, auf den die Kante gerichtet ist
     */
    private ActualPoint toP;

    /**
     * Gewicht der Kante
     */
    private int weight;

    /**
     * Ob die Kante in beide Richtungen mit dem selben Gewicht verläuft
     */
    private boolean bidirect;

    /**
     * Ob die Kante markiert ist
     */
    private boolean marked;

    /**
     * Grenzen des Gewichtungslabels
     */
    private Rectangle weightBoxBounds;

////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    @Override
    public ViewState getViewState() {
        return this.viewState;
    }

    public boolean isBidirect() {
        return bidirect;
    }

    public void setBidirect(boolean bidirect) {
        this.bidirect = bidirect;
    }

    public ActualPoint getFromP() {
        return fromP;
    }

    public void setFromP(ActualPoint fromP) {
        this.fromP = fromP;
    }

    public ActualPoint getToP() {
        return toP;
    }

    public void setToP(ActualPoint toP) {
        this.toP = toP;
    }

    public NodeComponent getFrom() {
        return from;
    }

    public void setFrom(NodeComponent from) {
        this.from = from;
    }

    public NodeComponent getTo() {
        return to;
    }

    public void setTo(NodeComponent to) {
        this.to = to;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public Rectangle getWeightBoxBounds(){
        return this.weightBoxBounds;
    }

    private void setWeightBoxBounds(Rectangle pR){
        this.weightBoxBounds = pR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds() {
        ActualPoint c1 = new ActualPoint(getFromP().getX(), getFromP().getY());
        ActualPoint c2 = new ActualPoint(getToP().getX(), getToP().getY()); 

        if(c1==null||c2==null)return new Rectangle();
        int tlX = Math.min(c1.getX(),c2.getX());
        int tlY = Math.min(c1.getY(),c2.getY());
        int brX = Math.max(c1.getX(),c2.getX());
        int brY = Math.max(c1.getY(),c2.getY());
        return new Rectangle(tlX,tlY,Math.max(brX-tlX,1),Math.max(brY-tlY,1));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons
    /**
     * 
     * @param pFrom     Knotenkomponente, von der die Kante ausgeht
     * @param pTo       Knotenkomponente, auf die die Kante zeigt
     * @param pWeight   Gewichtung
     * @param pVs       Viewstate
     */
    public EdgeComponent(NodeComponent pFrom, NodeComponent pTo, int pWeight, ViewState pVs){
        if (pFrom.equals(pTo)){
            throw new IllegalArgumentException("Points cannot be equal");
        }

        this.from = pFrom;
        this.to = pTo;
        this.weight = pWeight;
        this.viewState = pVs;

        this.bidirect = true;
        this.marked = false;

        this.weightBoxBounds = new Rectangle();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComp(Graphics2D g2d, Rectangle pR) {
        setFromP(getViewState().getActualPoint(new InSysPoint(getFrom().getPositionX(), getFrom().getPositionY())));
        setToP(getViewState().getActualPoint(new InSysPoint(getTo().getPositionX(), getTo().getPositionY())));
        if(!pR.intersects(getBounds()))return;

        setWeightBoxBounds(new Rectangle());

        //System.out.println("\n"+getFrom().getId());

        ActualPoint pM1 = getFromP();
        ActualPoint pM2 = getToP();
        
        double angle1to2 = angleNegYToPoint(pM1, pM2);
        double angle2to1 = angleNegYToPoint(pM2, pM1);
        //System.out.println(p1.toString()+" "+p2.toString());

        ActualPoint p1 = getPtWOffsetAtAngle(pM1, angle1to2, getTo().getCircleSize()/2);
        ActualPoint p2 = getPtWOffsetAtAngle(pM2, angle2to1, getTo().getCircleSize()/2);

        if(isMarked()){
            g2d.setColor(getViewState().getTheme().getMarkingColor());
        }else{
            g2d.setColor(getViewState().getTheme().getWritingOnBackgroundColor());
        }


        InSysPoint isP1 = new InSysPoint(getFrom().getPositionX(), getFrom().getPositionY());
        InSysPoint isP2 = new InSysPoint(getTo().getPositionX(), getTo().getPositionY());
        double diffX = isP2.getX() - isP1.getX();
        double diffY = isP2.getY() - isP1.getY();
        double isDist = Math.sqrt((diffX*diffX)+(diffY*diffY));
        //System.out.println(isDist);

        //draw connector wenn knoten überlappen
        if(getFrom().contains(p2)||(!isBidirect()&&isDist < 1.87)){
            ActualPoint p1_1 = getPtWOffsetAtAngle(pM1, angle1to2+90, getTo().getCircleSize()/2);
            ActualPoint p1_2 = getPtWOffsetAtAngle(pM1, angle1to2-90, getTo().getCircleSize()/2);

            ActualPoint p2_1 = getPtWOffsetAtAngle(pM2, angle2to1-90, getTo().getCircleSize()/2);
            ActualPoint p2_2 = getPtWOffsetAtAngle(pM2, angle2to1+90, getTo().getCircleSize()/2);

            if(isMarked()){
                g2d.setColor(getViewState().getTheme().getMarkingColor());
            }else{
                g2d.setColor(getViewState().getTheme().getDefaultNodeColor());
            }

            g2d.drawLine(p1_1.getX(), p1_1.getY(), p2_1.getX(), p2_1.getY());
            g2d.drawLine(p1_2.getX(), p1_2.getY(), p2_2.getX(), p2_2.getY());

            if(viewState.isDisplayWeightBlobs()){
                if(isBidirect()){
                    paintLabel(g2d, getWeight()+"", (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2);
                }else{
                    if(getFrom().contains(p2)){//zielgewicht immer näher an Zielknotenzentrum
                        paintLabel(g2d, getWeight()+"", p1.getX(), p1.getY());
                    }else{
                        paintLabel(g2d, getWeight()+"", p2.getX(), p2.getY());
                    }
                }
            }

            return;
        }

        //System.out.println("ibd "+isBidirect()+"  isw "+isBidirect());
        if(isBidirect()){
            if(getFrom().getId()<getTo().getId()){
                g2d.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                if(getViewState().isShowWeight()){
                    paintLabel(g2d, getWeight()+"", (p1.getX()+p2.getX())/2, (p1.getY()+p2.getY())/2);
                }
            }
        }else{ 
            g2d.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            String wd = "";
            if(getViewState().isShowWeight()){
                wd += getWeight();
            }
            paintArrow(g2d, p2.getX(), p2.getY(), angleNegYToPoint(p1, p2), wd);
            
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintCompAM(Graphics2D g2d, Rectangle pR) {
        paintComp(g2d, pR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintCompAZ(Graphics2D g2d, Rectangle pR) {
        paintCompAM(g2d, pR);
    }

    /**
     *
     * @param g2d   Graphics2D Objekt zum bemalen
     * @param pX    X-Koordinate der Spitze
     * @param pY    Y-Koordinate der Spitze
     * @param pDir  Richtung des Pfeiles
     * @param pLabel Beschriftung des Pfeiles
     */
    public void paintArrow(Graphics2D g2d, int pX, int pY, double pDir, String pLabel){
        ActualPoint tip = new ActualPoint(pX, pY);

        ActualPoint l = getPtWOffsetAtAngle(tip, pDir-150, 0.25);//dyn Größe
        ActualPoint r = getPtWOffsetAtAngle(tip, pDir-210, 0.25);

        g2d.drawLine(tip.getX(), tip.getY(), l.getX(), l.getY());
        g2d.drawLine(tip.getX(), tip.getY(), r.getX(), r.getY());

        if(!pLabel.isBlank()){
            paintLabel(g2d, pLabel, Math.round((r.getX()+l.getX())/2), Math.round((r.getY()+l.getY())/2));
        }
    }

    /**
     * Zeichnet Label
     * @param g2d       Graphics2D Objekt zum bemalen
     * @param pLabel    Text
     * @param pX        X-Koordinate der Spitze
     * @param pY        Y-Koordinate der Spitze
     */
    public void paintLabel(Graphics2D g2d, String pLabel, int pX, int pY){

        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D bd = fm.getStringBounds(pLabel, g2d);
        bd.setRect(0, 0, bd.getWidth(), bd.getHeight()-bd.getHeight()*0.4);//fontmetrics sagen string sei größer als er ist, folglich bissl oben abschneiden

        int ht = (int)bd.getHeight();
        int wd = (int)bd.getWidth();

        int x = pX - wd/2;
        int y = pY + ht/2;

        setWeightBoxBounds(new Rectangle(x, y-ht, wd, ht));

        if(viewState.isDisplayWeightBlobs()){
            Color col = (viewState.getTheme().getBackgroundHintsColor());
            g2d.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 255));
            g2d.fill(getWeightBoxBounds());
        }
        g2d.setColor(viewState.getTheme().getWritingOnBackgroundColor());
        g2d.drawString(pLabel, x, y);
    }

    /**
     * Gibt zurück ob Punkt auf Label liegt
     * @param pAp
     * @return
     */
    public boolean labelContains(ActualPoint pAp){
        return getWeightBoxBounds().contains(pAp.getX(),pAp.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        EdgeComponent ec = (EdgeComponent) obj;

        return (this.getFrom()==ec.getFrom())&&(this.getTo()==ec.getTo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return Objects.hash(getFrom().getId(),getTo().getId());
    }

    /**
     * berechnet den Winkel von Y-Achse an Punkt1 zu Punkt2
     * @param pFrom Punkt1
     * @param pTo Punkt2
     * @return Winkel in Grad, im Uhrzeigersinn
     */
    public double angleNegYToPoint(ActualPoint pFrom, ActualPoint pTo){
        if (pFrom.equals(pTo)){

            throw new IllegalArgumentException("Points cannot be equal");

        }

        double deltaY = pTo.getY() - pFrom.getY();

        double deltaX = pTo.getX() - pFrom.getX();


        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));


        if (angle < 0) {

            angle += 360;

        }
        return angle;
    }

    /**
     * Berechnet den Punkt auf einem Kreis, der die Spitze des Radius in einem gegebenen Winkel darstellt
     * @param pPt Mittelpunkt des Kreises
     * @param pAngle Winkel des Radius
     * @return Punkt auf dem Rand
     */
    private ActualPoint getPtWOffsetAtAngle(ActualPoint pPt, double pAngle, double pRad){

        if (pAngle < 0) {
            pAngle += 360;
        }        

        //System.out.println("angle "+getFrom().getId()+" to "+getTo().getId()+" "+angle1to2);        

        double[] pOff = getRadiusTipOffset(pAngle, getViewState().getZoom()*pRad);//mit Einbezug der Kreisgröße

        ActualPoint p = new ActualPoint(0,0);

        p.setX((int)Math.round(pPt.getX()-pOff[1]));
        p.setY((int)Math.round(pPt.getY()-pOff[0]));

        return p;
    }


    //Ausschnitt aus altem Artilleriesimulator; rewrite vtl? nuh uh keine Zeit
    /**
     * Berechnet den Offset, den die Spitze des Radius in einem gegebenen Winkel der Mitte eines Kreises gegenüber innehält
     * @param pAngle Winkel des Radius
     * @param pRad Betrag des Radius
     * @return Array mit Offset X und Y
     */
    public double[] getRadiusTipOffset(double pAngle, double pRad){
		double tipPos[] = new double[2];
		double angle = pAngle;
		double rad = pRad;
		
		if(angle==0){
			tipPos[0]=-0;
			tipPos[1]=-rad;
		}
		
		if(angle>0&&angle<90){
			
			angle = 90-angle;
			
			tipPos[0]=-calcAnkathete(angle,rad);
			tipPos[1]=calcGgKathete(angle,rad)*-1;
		}
		
		if(angle==90){
			tipPos[0]=-rad;
			tipPos[1]=0;
		}
		
		if(angle>90&&angle<180){
			
			angle = angle-90;
			
			tipPos[0]=-calcAnkathete(angle,rad);
			tipPos[1]=calcGgKathete(angle,rad);
		}
		
		if(angle==180){
			tipPos[0]=-0;
			tipPos[1]=rad;
		}
		
		if(angle>180&&angle<270){
			
			angle = 270-angle;
			
			tipPos[0]=-calcAnkathete(angle,rad)*-1;
			tipPos[1]=calcGgKathete(angle,rad);
		}
		
		if(angle==270){
			tipPos[0]=rad;
			tipPos[1]=0;
		}
		
		if(angle>270/*&&angle<360*/){
			
			angle = angle-270;
			
			tipPos[0]=-calcAnkathete(angle,rad)*-1;
			tipPos[1]=calcGgKathete(angle,rad)*-1;
		}
		
		return tipPos;
	}

    /**
     * Kalkuliert die Ankathete aus Winkel und Hypotenuse
     * @param pAngle Winkel
     * @param pHypo Hypotenuse
     * @return Länge der Ankathete
     */
    private double calcAnkathete(double pAngle, double pHypo){
        return (Math.cos(Math.toRadians(pAngle))*pHypo);
    }

    /**
    * Kalkuliert die Gegenkathete aus Winkel und Hypotenuse
     * @param pAngle Winkel
     * @param pHypo Hypotenuse
     * @return Länge der Gegenkathete
     */
    private double calcGgKathete(double pAngle, double pHypo){
        return (Math.sin(Math.toRadians(pAngle))*pHypo);
    }
}
