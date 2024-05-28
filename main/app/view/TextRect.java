package app.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

/**
 * utility Klasse im Texte in Rechtecke hinzuzufügen
 */
public abstract class TextRect {

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Zeichnet gegebenen Text anhand Offsets und Zeilen ein
     * @param g2d Graphics2d-Object zum bemalen
     * @param posX X Position
     * @param posY Y Position
     * @param pLines Zeilen 
     * @param pOffs Offsets
     */
    public static void paint(Graphics2D g2d, int posX, int posY, String[] pLines, double[] pOffs){
        //System.out.println(pLines.length);
        for (int i = 0; i < pLines.length; i++) {
            //System.out.println(pLines[i]+" off "+pOffs[i]);
            g2d.drawString(pLines[i], (int)Math.round(posX+pOffs[i]), (int)Math.round(posY+((i+1)*pOffs[pOffs.length-2])+pOffs[pOffs.length-1]));
        }
    }
    
    /**
     * Teilt den String passend in das Rechteck ein
     * @param pStr
     * @param g2d
     * @param pRect
     * @return String[]
     */
    public static String[] getFittingString(String pStr, Graphics2D g2d,Rectangle pRect){
        FontMetrics fm = g2d.getFontMetrics();
        double width = pRect.getWidth();
        double height = pRect.getHeight();

        Rectangle2D stringRect = fm.getStringBounds(pStr, g2d);
        double strWidth = stringRect.getWidth();
        double strHeight = stringRect.getHeight();

        if ((strWidth<=width&&strHeight<=height)||pStr.length()<=2){
            String[] orRet = new String[1];
            orRet[0] = pStr;
            return orRet;
        }

        if(strHeight>=height){
            return null;
        }

        char[] charAr = pStr.toCharArray();

        List<char[]> retL = new LinkedList<char[]>();
        

        int i = 0;
        while(i < charAr.length){
            //System.out.println(i+" "+charAr.length);
            List<Character> ln = new LinkedList<Character>();
            while(true){
                ln.add(charAr[i]);
                if(ln.get(0).equals(' '))ln.remove(0);
                if(fm.getStringBounds(charLsToString(ln), g2d).getWidth()<=width){
                    //System.out.println(charLsToString(ln)+"("+fm.getStringBounds(charLsToString(ln), g2d).getWidth()+") fit in "+ width);
                    i++;
                }else{
                    //System.out.println(charLsToString(ln)+"("+fm.getStringBounds(charLsToString(ln), g2d).getWidth()+") no fit in "+ width);
                    ln.remove(ln.size()-1);
                    break;
                }
                if(i>=charAr.length)break;
            }
            if(ln.get(ln.size()-1).equals(' '))ln.remove(ln.size()-1);
            //System.out.println("line: "+charLsToString(ln));
            char[] lnAr = new char[ln.size()];

            for (int j = 0; j < ln.size(); j++) {
                lnAr[j] = ln.get(j);
            }
            retL.add(lnAr);
        }

        if (fm.getStringBounds("ABCDEFG", g2d).getHeight()*retL.size()>height){
            return null;
        }

        String[] ret = new String[retL.size()];
        //System.out.println(ret.length+" "+retL.size());
        for(int j = 0; j < retL.size(); j++){
            ret[j] = charArrToString(retL.get(j));
        }
        return ret;
    }

    /**
     * Berechnet den Offset für die Zeilen Im Rechteck
     * @param pLines
     * @param g2d
     * @param pRect
     * @return Offset [letzter] = Zeilenhöhe, [vorletzter] = Vertikaler Offset der gesamten Box
     */
    public static double[] getLineOffsetsForRect(String[] pLines, Graphics2D g2d,Rectangle pRect){
        FontMetrics fm = g2d.getFontMetrics();
        if (pLines==null) {
            return null;
        }
        
        double width = pRect.getWidth();
        double height = pRect.getHeight();

        double[] ret = new double[pLines.length+2];

        for (int i = 0; i < pLines.length; i++) {
            double lineWidth = fm.getStringBounds(pLines[i], g2d).getWidth();
            //System.out.println(pLines[i]+" fills "+lineWidth+" of "+width);

            ret[i]=(width-lineWidth)/2;
            //System.out.println(pLines[i]+"| wide "+lineWidth+" space "+width);
        }
        double lineHeight = fm.getStringBounds("ABCDEFG", g2d).getHeight();
        ret[ret.length-2] = lineHeight;//Zeilenhöhe
        double linesHeight = pLines.length*lineHeight;
        ret[ret.length-1] = (height-linesHeight)/2-(0.25*lineHeight);//Vertikaler Offset

        return ret;
    }

    /**
     * Wandelt char liste zu String um
     * @param pLs
     * @return
     */
    private static String charLsToString(List<Character> pLs){
        String ret = "";

        for (int i = 0; i < pLs.size(); i++) {
            ret += pLs.get(i);
        }

        return ret;
    }

    /**
     * Konvertiert char Array zu String
     * @param pAr
     * @return String
     */
    private static String charArrToString(char[] pAr){
        String ret = "";

        for (int i = 0; i < pAr.length; i++) {
            ret += pAr[i];
        }

        return ret;
    }
}