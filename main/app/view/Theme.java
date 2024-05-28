package app.view;

import java.awt.*;

/**
 * Klasse zur Bestimmung des Designs vom Userinterface 
 */
public class Theme {

////////////////////////////////////////////////////////////////////////////////////////////////////attribute
    /**
     * Name des Themes
     */
    private final String name;

    /**
     * Hintergrundfarbe
     */
    private final Color backgroundColor;
    
    /**
     * Farbe, die sich von Hintergrundfarbe abhebt
     */
    private final Color writingOnBackgroundColor;


    /**
     * Menüfarbe
     */
    private final Color menuColor;

    /**
     * Farbe, die sich von Menüfarbe abhebt
     */
    private final Color writingOnMenuColor;


    /**
     * Standardknotenfarbe
     */
    private final Color defaultNodeColor;

    /**
     * Farbe, die sich von Standardknotenfarbe abhebt
     */
    private final Color writingOnDefaultNodeColor;


    /**
     * Farbe, zum markieren einzelner Komponente
     */
    private final Color markingColor;

    /**
     * Hintergrundannotationsfarbe
     */
    private final Color backgroundHintsColor;


    /**
     * Auswahlfarbe
     */
    private final Color selectionColor;


    /**
     * Schriftart
     */
    private final String font;

    /**
     * Autor des Themes
     */
    private final String author;
    
////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    public String getName() {
        return name;
    }

    public String getFont(){
        return this.font;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public Color getWritingOnBackgroundColor() {
        return writingOnBackgroundColor;
    }

    public Color getMenuColor() {
        return menuColor;
    }
    public Color getWritingOnMenuColor() {
        return writingOnMenuColor;
    }

    public Color getDefaultNodeColor() {
        return defaultNodeColor;
    }
    public Color getWritingOnDefaultNodeColor() {
        return writingOnDefaultNodeColor;
    }

    public Color getMarkingColor() {
        return markingColor;
    }
    public Color getBackgroundHintsColor() {
        return backgroundHintsColor;
    }

    public Color getSelectionColor() {
        return selectionColor;
    }

    public String getAuthor(){
        return this.author;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////cons

    /**
     * Erstellt ein Theme mit gegebenen Parametern
     * @param pName
     * @param pFont
     * @param pBgCol
     * @param pBgWritingCol
     * @param pMenuCol
     * @param pMenuWritingCol
     * @param pDefaultNodeCol
     * @param pDefaultNodeWritingCol
     * @param pMarkCol
     * @param pBgHintCol
     * @param pSelectionColor
     * @param pAuthor
     */
    public Theme(   String pName, String pFont,
                    String pBgCol, String pBgWritingCol,
                    String pMenuCol, String pMenuWritingCol,
                    String pDefaultNodeCol, String pDefaultNodeWritingCol,
                    String pMarkCol, String pBgHintCol,
                    String pSelectionColor, String pAuthor){

        this.name = pName;
        this.font = pFont;

        this.backgroundColor = decodeColor(pBgCol);
        this.writingOnBackgroundColor = decodeColor(pBgWritingCol);


        this.menuColor = decodeColor(pMenuCol);
        this.writingOnMenuColor = decodeColor(pMenuWritingCol);

        this.defaultNodeColor = decodeColor(pDefaultNodeCol);
        this.writingOnDefaultNodeColor = decodeColor(pDefaultNodeWritingCol);

        this.markingColor = decodeColor(pMarkCol);
        this.backgroundHintsColor = decodeColor(pBgHintCol);

        this.selectionColor = decodeColor(pSelectionColor);
        this.author = pAuthor;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Decoded einen eine Farbe beschreibenden String zu awt.Color Objekt
     * @param pHex Hexcode RGBA
     * @return Farbobjekt
     */
    public static Color decodeColor(String pHex){
        Color ret;

        if(pHex.charAt(0)!=('#')){
            System.err.println("invalid color ["+pHex+"], add '#'");
            return Color.decode("#ff00dc");//def
        }
        try {
            if(pHex.length()==7){
                int r = Integer.parseInt(pHex.substring(1, 3), 16);
                int g = Integer.parseInt(pHex.substring(3, 5), 16);
                int b = Integer.parseInt(pHex.substring(5, 7), 16);
                
                //System.out.println(r+" "+g+" "+b);
                ret = new Color(r, g, b);
            }else if(pHex.length()==9){
                int r = Integer.parseInt(pHex.substring(1, 3), 16);
                int g = Integer.parseInt(pHex.substring(3, 5), 16);
                int b = Integer.parseInt(pHex.substring(5, 7), 16);
                int a = Integer.parseInt(pHex.substring(7, 9), 16);
    
                ret = new Color(r, g, b, a);
            }else{
                System.err.println("invalid color ["+pHex+"]");
                ret = new Color(255,0,220);
            }
        } catch (Exception e) {
            ret = new Color(255,0,220);
        }
        

        return ret;
    }

    /**
     * Encoded eine awt.Color Farbe in einen String
     * @param pCol
     * @return String in Hexadezimal RGBA
     */
    public static String encodeColor(Color pCol){
        int r = pCol.getRed();
        int g = pCol.getGreen();
        int b = pCol.getBlue();

        int a = pCol.getAlpha();

        String ret = "#";
        ret += toHex(r);
        ret += toHex(g);
        ret += toHex(b);
        ret += toHex(a);

        //System.out.println(ret);
        return ret;
    }

    /**
     * Konvertiert Zahl zu Hex Repräsentation mit immer mindestens 2 Ziffern
     * @param pNum
     * @return
     */
    private static String toHex(int pNum){
        String ret = Integer.toHexString(pNum);
        if(ret.length()<2){
            ret = 0+ret;
        }
        return ret;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }
}
