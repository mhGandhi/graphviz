package app.view;

import java.io.FileWriter;
import java.io.IOException;

import app.FileSS;

/**
 * Klasse zum einfachen erstellen von THemes
 */
public abstract class ThemeFactory {

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gibt hardgecoded Standardtheme zurück (für Notfälle)
     * @return
     */
    public static Theme defaultTheme(){
        Theme t = new Theme("Default", "Calibri",
                "#1f1f1f", "#FFFFFF",
                "#181818", "#d6d2ca",
                 "#d6d2ca", "#000000",
                 "#FF1230", "#989EA1",
                 "#357EC7", "Adrian Rainer Akipi");

        return t;
    }
    
    /**
     * extrahiert vom String die wichtigen Teil und fügt sie einem Array hinzu
     * @param pEigenschaften String mit Eigenschaften des Themes
     * @return String array mit den einzelnen Theme Werten
     */
    public static String[] extractFromString(String pEigenschaften){
    
      int row = 0;
      String tmp = "";
      String[] extracted = new String[12];
    
      for(int i = 0; i < pEigenschaften.length(); i++){
        if(pEigenschaften.substring(i, i + 1).equals("\"")){
          while(!pEigenschaften.substring(i + 1, i + 2).equals("\"")){
            tmp = tmp + pEigenschaften.substring(i + 1, i + 2);
            i++;
          }
          if(pEigenschaften.substring(i + 1, i + 2).equals("\"")){
            extracted[row] = tmp;
            row++;
            tmp = "";
            i++;
          }
        }
      }
      return extracted;
    }

    public static Theme fromFile(String pPath){
      String[] eigenschaften = extractFromString(FileSS.fileToString(pPath));

      Theme t = new Theme(eigenschaften[0], eigenschaften[1], 
              eigenschaften[2], eigenschaften[3], 
              eigenschaften[4], eigenschaften[5], 
              eigenschaften[6], eigenschaften[7], 
              eigenschaften[8], eigenschaften[9], 
              eigenschaften[10], eigenschaften[11]); 
      return t;
    }

    public static void createThemeFile(String pPath, Theme pTheme){
  try {
    FileWriter writer = new FileWriter("view/themes/" + pPath + ".txt");
    writer.write("Name: " + "\"" + pTheme.getName() + "\"" + "\n");
    writer.write("Font: " + "\"" + pTheme.getFont() + "\"" + "\n");
    writer.write("backgroundcol: " + "\"" + pTheme.getBackgroundColor() + "\"" + "\n");
    writer.write("Backgroundwritingcol: " + "\"" + pTheme.getWritingOnBackgroundColor() + "\"" + "\n");
    writer.write("Menucol: " + "\"" + pTheme.getMenuColor() + "\"" + "\n");
    writer.write("Menuwritingcol: " + "\"" + pTheme.getWritingOnMenuColor() + "\"" + "\n");
    writer.write("DefaultNodecol: " + "\"" + pTheme.getDefaultNodeColor() + "\"" + "\n");
    writer.write("DefaultNodewritingcol: " + "\"" + pTheme.getWritingOnDefaultNodeColor() + "\"" + "\n");
    writer.write("MarkingEdgecol: " + "\"" + pTheme.getMarkingColor() + "\"" + "\n");
    writer.write("Hintedgecol: " + "\"" + pTheme.getBackgroundHintsColor() + "\"" + "\n");
    writer.write("Selectioncol: " + "\"" + pTheme.getSelectionColor() + "\"" + "\n");
    writer.write("Author: " + "\"" + pTheme.getAuthor() + "\"" + "\n");
    writer.close();
  }catch (IOException e) {
    e.printStackTrace();
  }
}
}
