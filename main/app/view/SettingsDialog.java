package app.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import app.Controller;
import app.Tool;

/**
 * Definiert Einstellungs-Dialog
 */
public abstract class SettingsDialog{
    
    /**
     * Unterordner des Classpaths, in dem nach Themes gesucht wird
     */
    private static final String THEMEDIR = "themes";

    /**
     * Unterordner des Classpaths, in dem nach Sprachen gesucht wird
     */
    private static final String LOCALEDIR = "lgs";

    /**
     * Anerkannte Dateiendung für Themes
     */
    private static final String THEMESUFFIX = ".th";

    /**
     * Anerkannte Dateiendung für Sprachen (immer .properties)
     */
    private static final String LOCALESUFFIX = ".properties";

    /**
     * Anerkannter Dateinamenanfang für Sprachen
     */
    private static final String LOCALEPREFIX = "lang";

    /**
     * Zwischenspeicher für das Theme, welches am Ende angewandt werden soll
     */
    private static String themeToApply;

    /**
     * Zwischenspeicher für die Sprache, welche am Ende angewandt werden soll
     */
    private static String langToApply;


    /**
     * Labels, welche die Metadaten des aktuell angewählten Themes anzeigen
     */
    private static JLabel[] themeInfolabel;

    /**
     * Labels, welche die Metadaten der aktuell angewählten Sprache anzeigen
     */
    private static JLabel[] localeInfolabel;

    /**
     * TextPanel, welches die Metadaten des aktuell angewählten Tools anzeigt
     */
    private static JTextPane toolInfoTP;

    
    /**
     * Zwischenspeicher für die Pfade aller erkannten Themes
     */
    private static List<String> themePaths;

    /**
     * Zwischenspeicher für alle erkannten Themes
     */
    private static List<Theme> themes;


    /**
     * Zwischenspeicher für die Pfade aller erkannten Sprachen
     */
    private static List<String> localePaths;

    /**
     * Zwischenspeicher für alle erkannten Sprachen
     */
    private static List<ResourceBundle> locales;

    /**
     * aktuelles Theme
     */
    private static Theme theme;

    /**
     * aktuelle Sprache
     */
    private static ResourceBundle resB;
    //todo dank modality controller wieder raus?
    /**
     * zeigt Dialog an
     * @param pParentComponent  Übergeordnete Komponente
     * @param pProperties       zu Ändernde Einstellungen
     * @param pResourceBundle   Anzeigesprache
     * @param pTheme            Anzeigetheme
     * @param pTools            Aufzulistende tools
     * @param pCon              Controller zum Speichern
     */
    public static void showDialog(JFrame pParentComponent, Properties pProperties, ResourceBundle pResourceBundle, Theme pTheme, Tool[] pTools, Controller pCon){

        theme = pTheme;
        resB = pResourceBundle;

        JDialog win = new JDialog(pParentComponent, r("settingsTitle"), true);

        win.setLocation(pParentComponent.getLocation());
        win.setBackground(pTheme.getBackgroundColor());

        themeToApply = pProperties.getProperty("slTheme");
        langToApply = pProperties.getProperty("slLang");

        themePaths = getFilesInDir(THEMEDIR);
        themes = getAssThemes(themePaths);
        
        localePaths = getFilesInDir(LOCALEDIR);
        locales = getAssLocales(localePaths);
        
        //ungültige Themes rausfiltern
        for(int i = 0; i < themes.size(); i++){
            if(themes.get(i)==null){
                //System.out.println(i+" "+themePaths.get(i)+" no theme");
                themes.remove(i);
                themePaths.remove(i);
            }else{
                //System.out.println(i+" "+themePaths.get(i)+" "+themes.get(i).getName());
            }
        }
        

        //ungültige Sprachen rausfiltern
        for(int i = 0; i < locales.size(); i++){
            if(locales.get(i)==null){
                //System.out.println(i+" "+langPaths.get(i)+" no theme");
                locales.remove(i);
                localePaths.remove(i);
            }else{
                //System.out.println(i+" "+langPaths.get(i)+" "+langNames.get(i));
            }
        }
        

        int menuPointsCount = 3;
        JPanel panel = newPanel(0,0);
        panel.setLayout(new BorderLayout());
        JPanel menuPanel = newPanel(menuPointsCount,1);
        menuPanel.setLayout(new GridLayout(menuPointsCount, 1));


        //todo init aufteilen und in seperater methode

        //ThemePanel
        JPanel themePanel = newPanel(1, 3);
        {
            themePanel.setBorder(new LineBorder(theme.getWritingOnMenuColor()));
            //themePanel.setLayout(new BoxLayout(themePanel,BoxLayout.X_AXIS));
            JLabel themeLabel = newLabel(r("settings_themeLabel"));
            String[] themeNames = new String[themes.size()];
            for (int i = 0; i < themeNames.length; i++) {
                themeNames[i] = themes.get(i).getName();
            }
            JList<String> themeList = newList(themeNames);
            themeList.setVisibleRowCount(3);
            themeList.addListSelectionListener(new ListSelectionListener(){
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int i = themeList.getSelectedIndex();
                    if(i>=0){
                        themeToApply = themePaths.get(i);
                        themeInfolabel[0].setText(themes.get(i).getName());
                        themeInfolabel[1].setText(themes.get(i).getAuthor());
                    }else{
                        themeToApply = pProperties.getProperty("slTheme");
                        themeInfolabel[0].setText(theme.getName());
                        themeInfolabel[1].setText(theme.getAuthor());
                    }
                }
            });
            JScrollPane jspTheme = newScrollPane(themeList);


            themeInfolabel = new JLabel[2];
            themeInfolabel[0] = newLabel(theme.getName());
            themeInfolabel[1] = newLabel(theme.getAuthor());

            JPanel themeInfoPanel = newPanel(2,2);
            themeInfoPanel.add(newLabel(r("info_name")));
            themeInfoPanel.add(themeInfolabel[0]);
            themeInfoPanel.add(newLabel(r("info_author")));
            themeInfoPanel.add(themeInfolabel[1]);

            themePanel.add(themeLabel); themePanel.add(jspTheme); themePanel.add(themeInfoPanel);
        }
        



        //SprachPanel
        JPanel localePanel = newPanel(1, 3);
        {
            localePanel.setBorder(new LineBorder(theme.getWritingOnMenuColor()));
            //localePanel.setLayout(new BoxLayout(localePanel,BoxLayout.X_AXIS));
            JLabel localeLabel = newLabel(r("settings_localeLabel"));
            String[] localeNames = new String[locales.size()];
            for (int i = 0; i < localeNames.length; i++) {
                localeNames[i] = locales.get(i).getString("lang_name");
            }
            JList<String> localeList = newList(localeNames);
            localeList.setVisibleRowCount(3);
            localeList.addListSelectionListener(new ListSelectionListener(){
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int i = localeList.getSelectedIndex();
                    if(i>=0){
                        langToApply = localePaths.get(i);
                        localeInfolabel[0].setText(locales.get(i).getString("lang_name"));
                        localeInfolabel[1].setText(locales.get(i).getString("file_author"));
                    }else{
                        langToApply = pProperties.getProperty("slLang");
                        localeInfolabel[0].setText(r("lang_name"));
                        localeInfolabel[1].setText(r("file_author"));
                    }
                }
            });
            JScrollPane jspLocale = newScrollPane(localeList);


            localeInfolabel = new JLabel[2];
            localeInfolabel[0] = newLabel(r("lang_name"));
            localeInfolabel[1] = newLabel(r("file_author"));

            JPanel localeInfoPanel = newPanel(2,2);
            localeInfoPanel.add(newLabel(r("info_name")));
            localeInfoPanel.add(localeInfolabel[0]);
            localeInfoPanel.add(newLabel(r("info_author")));
            localeInfoPanel.add(localeInfolabel[1]);

            localePanel.add(localeLabel); localePanel.add(jspLocale); localePanel.add(localeInfoPanel);
        }


        //ToolInfoPanel
        JPanel toolPanel = newPanel(1, 3);
        {
            toolPanel.setBorder(new LineBorder(theme.getWritingOnMenuColor()));
            //toolPanel.setLayout(new BoxLayout(toolPanel,BoxLayout.X_AXIS));
            JLabel toolspecsLabel = newLabel(r("settings_toolinfoLabel"));
            String[] toolNames = new String[pTools.length];
            for (int i = 0; i < toolNames.length; i++) {
                toolNames[i] = pTools[i].getName();
            }
            JList<String> toolList = newList(toolNames);
            toolList.addListSelectionListener(new ListSelectionListener(){
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int i = toolList.getSelectedIndex();
                    if(i>=0){
                        toolInfoTP.setText(r("info_author")+" "+pTools[i].getAuthor()+"\n\n"+r("info_description")+":\n"+pTools[i].getDescription()+"\n\n"+getToolusabilityString(pTools[i]));
                    }else{
                        toolInfoTP.setText("");
                    }
                }
            });
            JScrollPane jspTool = newScrollPane(toolList);


            toolInfoTP = new JTextPane(); toolInfoTP.setText("");
            toolInfoTP.setEditable(false); toolInfoTP.setBackground(theme.getMenuColor()); toolInfoTP.setForeground(theme.getWritingOnMenuColor());
            JScrollPane toolDesc = newScrollPane(toolInfoTP);

            toolPanel.add(toolspecsLabel); toolPanel.add(jspTool); toolPanel.add(toolDesc);
        }
        
        

        //ButtonPanel
        JPanel buttonPanel = newPanel(0, 0);
        buttonPanel.setLayout(new FlowLayout());
        JButton butt_ok = new JButton(r("option_ok"));
        butt_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.dispose();
                if(!pProperties.get("slTheme").equals(themeToApply)||!pProperties.get("slLang").equals(langToApply))
                {
                    pProperties.setProperty("slTheme", themeToApply);
                    pProperties.setProperty("slLang", langToApply);
                    pCon.saveProperties();
                    pCon.getView().notification("notif_restartForChange");
                }
                //todo reload statt notif aber ka grad + sprengt rahmen amk
                return;
            }
        });
        JButton butt_cancel = new JButton(r("option_cancel"));
        butt_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.dispose();
                return;
            }
        });
        JButton butt_help = new JButton(r("settings_help"));
        butt_help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showControls(win);
            }
        });
        buttonPanel.add(butt_ok);
        buttonPanel.add(butt_cancel);
        buttonPanel.add(butt_help);

        //MainPanel
        menuPanel.add(themePanel);
        menuPanel.add(localePanel);
        menuPanel.add(toolPanel);
        
        panel.add(menuPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        win.add(panel);
        win.pack();
        //win.setResizable(false);
        win.setVisible(true);
    }

    private static void showControls(JDialog pWin){
        JDialog conDia = new JDialog(pWin, r("help_controls"));
        conDia.setResizable(false);
        conDia.setLocationRelativeTo(pWin);
        conDia.setAlwaysOnTop(true);

        JTextPane contPanelTP = new JTextPane();
        contPanelTP.setBackground(theme.getMenuColor()); contPanelTP.setForeground(theme.getWritingOnMenuColor());
        contPanelTP.setText(getHierarchyFromRes("help_controltextpoint",0));
        contPanelTP.setEditable(false);
        JScrollPane contPanel = newScrollPane(contPanelTP);

        conDia.add(contPanel);
        conDia.pack();

        conDia.setVisible(true);

        conDia.addWindowFocusListener(new WindowFocusListener() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                conDia.dispose();
            }
            
        });
    }

    /**
     * Gibt Text auf Basis der Sprache hierarchisch aufgelistet zurück
     * @param pHBase Basisname der Hierarchie
     * @param pDepth aktuelle Tiefe (für Rekursion)
     * @return Der Text
     */
    private static String getHierarchyFromRes(String pHBase, int pDepth){//eine Schönheit will ich nur anmerken
        String ret = "";
        
        int i = 0;
        while(true){
            String currentLine = pHBase+"."+i;
            try {
                resB.getString(currentLine);
                for (int j = pDepth; j > 0; j--) {
                    if(j == pDepth){
                        ret += "└┄┄┄";
                    }else{
                        ret += "┄┄┄┄";
                    }
                }
                //ret += pDepth;
            } catch (Exception e) {
                
            }
            try {
                ret += resB.getString(currentLine)+"\n";
                ret += getHierarchyFromRes(currentLine, pDepth+1);
            } catch (Exception e) {

                break;
            }

            i++;
        }

        return ret;
    }

    /**
     * Gibt über Tool nutzbarkeit informierenden String zurück
     * @param pTool
     * @return Text mit Sprache angewandt
     */
    private static String getToolusabilityString(Tool pTool) {

        if((!(pTool.canBeRanFromMenuBar()||pTool.canBeRanFromNode()||pTool.canBeRanFromSpot())||!pTool.showInMenu())){
            return r("info_notusable");
        }else{
            String ret = r("info_usability");
            if(pTool.canBeRanFromMenuBar())ret+=" "+r("info_menubar");
            if(pTool.canBeRanFromNode())ret+=" - "+r("info_node");
            if(pTool.canBeRanFromSpot())ret+=" - "+r("info_spot");
            return ret;
        }

    }

    /**
     * Erstellt neue JList mit Theme
     * @param pContent
     * @return
     */
    private static JList<String> newList(String[] pContent){
        JList<String> ret = new JList<String>(pContent);
        ret.setBackground(theme.getMenuColor());
        ret.setForeground(theme.getWritingOnMenuColor());
        ret.setBorder(new LineBorder(theme.getWritingOnMenuColor()));

        return ret;
    }

    /**
     * Erstellt neues JPanel mit Theme und Gridlayout
     * Wenn 0 Zeilen oder Spalten wird kein Layout hinzugefügt
     * @param pRows Reihen
     * @param pCols Spalten
     * @return
     */
    private static JPanel newPanel(int pRows, int pCols) {
        JPanel ret = new JPanel();
        ret.setBackground(theme.getMenuColor());
        ret.setForeground(theme.getWritingOnMenuColor());
        if(pRows!=0&&pCols!=0)
        ret.setLayout(new GridLayout(pRows, pCols));
        ret.setBorder(new LineBorder(theme.getMenuColor()));
        return ret;
    }

    /**
     * Erstellt neues JLabel mit Theme
     * @param pText
     * @return
     */
    private static JLabel newLabel(String pText){
        JLabel ret = new JLabel(pText);
        ret.setForeground(theme.getWritingOnMenuColor());
        return ret;
    }

    /**
     * Erstellt neues JScrollPane mit Theme
     * @param pContent
     * @return
     */
    private static JScrollPane newScrollPane(Component pContent){
        JScrollPane ret  = new JScrollPane(pContent);
        ret.setBackground(theme.getBackgroundColor());
        ret.setBorder(new LineBorder(theme.getMenuColor()));

        JScrollBar[] bars = {
            ret.getVerticalScrollBar(),
            ret.getHorizontalScrollBar()
        };
        for (JScrollBar bar : bars) {
            bar.setBackground(theme.getMenuColor());
            bar.setBorder(new LineBorder(theme.getMenuColor()));
            bar.setForeground(theme.getWritingOnMenuColor());
        }
        return ret;
    }

    /**
     * Gibt Liste an Themes entsprechend Pfadliste zurück.
     * Wenn Pfad nicht auf gültiges Theme verweist wird das Theme an der Stelle null gesetzt
     * @return
     */
    private static List<Theme> getAssThemes(List<String> pPaths){
        List<Theme> ret = new ArrayList<Theme>();
        for (String p : pPaths) {
            if(p.endsWith(THEMESUFFIX)){
                try {
                    Theme t = ThemeFactory.fromFile(p);
                    ret.add(t);
                    //System.out.println(t);
                } catch (Exception e) {
                    //e.printStackTrace();
                    ret.add(null);
                    System.err.println(p+" does not seem to be a Theme");
                }
            }else{
                ret.add(null);
            }
        }
        return ret;
    }

    /**
     * Gibt Liste an Sprachen entsprechend Pfadliste zurück.
     * Wenn Pfad nicht auf gültige Sprache verweist wird die Sprache an der Stelle null gesetzt
     * @return
     */
    private static List<ResourceBundle> getAssLocales(List<String> pPaths){

        List<ResourceBundle> ret = new ArrayList<ResourceBundle>();
        for (String p : pPaths) {
            if(p.endsWith(LOCALESUFFIX)&&
            p.substring(LOCALEDIR.length()+1,p.length()).startsWith(LOCALEPREFIX)
            ){
                //System.out.println("\t"+p+" is lang");
                try {
                    ResourceBundle r = Controller.getBundleFromFile(p);
                    r.getString("lang_name");
                    r.getString("file_author");
                    ret.add(r);
                } catch (Exception e) {
                    //e.printStackTrace();
                    ret.add(null);
                }
            }else{
                ret.add(null);
            }
        }
        return ret;
    }

    /**
     * gibt Liste der Pfade der in einem gegebenen Pfad enthaltenen Dateien zurück
     * @param pDir gegebener Pfad
     * @return
     */
    private static List<String> getFilesInDir(String pDir){
        File dir = new File(pDir);
        File[] files = dir.listFiles();

        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            ret.add(files[i].toString());
        }
        
        return ret;
    }

    /**
     * Übersetzt Key in Sprachenspezifischen Text
     * @param pKey
     * @return
     */
    private static String r(String pKey){
        try{
            return resB.getString(pKey);
        }catch(MissingResourceException e){
            return "[SETKEY_"+pKey+"]";
        }
    }


}
