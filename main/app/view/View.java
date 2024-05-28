package app.view;

//import app.Tool; //16.03.2024 08:19 Nach Tortur im großen Stil (asfigg) finde ich heraus, dass das gehen würde.
import app.view.point.ActualPoint;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;


/**
 * Stellt im Bezug zum Model die View Klasse dar
 */
public class View{

////////////////////////////////////////////////////////////////////////////////////////////////////attribute

    private ResourceBundle r;

    private JFrame window;
    private ContentPanel pane;
    private JMenuBar menuBar;

    private ActionListener viewListener;
    private ViewState viewState;

    private List<ToolMenuItemSpecs>[] toolMenuItemSpecs;//0 menubar, 1 nodes, 2 spot
    
////////////////////////////////////////////////////////////////////////////////////////////////////get-set

    public ActionListener getViewListener(){
        return this.viewListener;
    }

    public ViewState getViewState(){
        return this.viewState;
    }

    public ContentPanel getContentPanel(){
        return this.pane;
    }

    public static ResourceBundle getBundleForLanguage(Locale pL){
        return ResourceBundle.getBundle("lgs/lang",pL);
    }

    public ResourceBundle getResourceBundle(){
        return this.r;
    }

    public void setResourceBundle(ResourceBundle pR){
        this.r = pR;
    }

    public JFrame getWindow() {
        return this.window;
    }

    public String r(String pKey){
        try{
            return getResourceBundle().getString(pKey);
        }catch(MissingResourceException e){
            return "[KEY_"+pKey+"]";
        }
    }

    private List<ToolMenuItemSpecs>[] getToolItemMenuSpecs(){
        return this.toolMenuItemSpecs;
    }

    /**
     * View und so
     * @param pViewState
     * @param pViewListener
     * @param pKeyListener
     * @param pMouseListener
     * @param pMouseWheelListener
     * @param pMouseMotionListener
     * @param pWindowAdapter
     */
    @SuppressWarnings("unchecked")
    public View(ViewState pViewState, ActionListener pViewListener, KeyListener pKeyListener, MouseListener pMouseListener, MouseWheelListener pMouseWheelListener, 
                MouseMotionListener pMouseMotionListener, WindowListener pWindowAdapter, ToolMenuItemSpecs[] pTMIS, ResourceBundle pRes){
        //System.out.println("in view \""+pLoc+"\"");

        this.viewListener = pViewListener;
        this.viewState = pViewState;
        this.r = pRes;

        //hätte man von Anfang an nehmen können man
        UIManager.put("OptionPane.cancelButtonText", r("option_cancel"));
        UIManager.put("OptionPane.okButtonText", r("option_ok"));
        //UIManager.put("Panel.background", getViewState().getTheme().getMenuColor());
        //UIManager.put("Panel.foreground", getViewState().getTheme().getMenuColor());
        //UIManager.put("Frame.background", getViewState().getTheme().getMenuColor());

        List<ToolMenuItemSpecs> mBarTMIS = new LinkedList<ToolMenuItemSpecs>();
        List<ToolMenuItemSpecs> nodeTMIS = new LinkedList<ToolMenuItemSpecs>();
        List<ToolMenuItemSpecs> spotTMIS = new LinkedList<ToolMenuItemSpecs>();

        for (ToolMenuItemSpecs tmi : pTMIS) {
            if(tmi.isRunnableFromMenuBar()){
                mBarTMIS.add(tmi);
            }
            if(tmi.isRunnableFromNode()){
                nodeTMIS.add(tmi);
            }
            if(tmi.isRunnableFromSpot()){
                spotTMIS.add(tmi);
            }
        }
        
        this.toolMenuItemSpecs = /*(List<ToolMenuItemSpecs>[])*/ new List[3];
        this.toolMenuItemSpecs[0] = mBarTMIS;
        this.toolMenuItemSpecs[1] = nodeTMIS;
        this.toolMenuItemSpecs[2] = spotTMIS;
        

        initMenuBar();
        pane = new ContentPanel(getViewState());
        pane.addMouseListener(pMouseListener);
        pane.addMouseMotionListener(pMouseMotionListener);
        pane.addMouseWheelListener(pMouseWheelListener);
        initWindow();
        window.addKeyListener(pKeyListener);
        window.addWindowListener(pWindowAdapter);

        window.setVisible(true);
    }
    /**
     * initialisiert die Menubar
     */
    private void initMenuBar(){
        menuBar = new JMenuBar();
        menuBar.setBorder(new LineBorder(viewState.getTheme().getMenuColor()));
        menuBar.setBackground(viewState.getTheme().getMenuColor());
        //menuBar.setBackground(Color.RED);

        //filemenu
        JMenu fileMenu = newMenuMenu("file");

        fileMenu.add(newMenuItem("file_new"));

        fileMenu.add(newSeparator());
        fileMenu.add(newMenuItem("file_save"));
        fileMenu.add(newMenuItem("file_saveas"));
        fileMenu.add(newMenuItem("file_load"));

        fileMenu.add(newSeparator());
        fileMenu.add(newMenuItem("file_import"));
        fileMenu.add(newMenuItem("file_export"));


        /* //test
        fileMenu.add(newSeparator());
        JMenu submen = newMenuMenu("boah");
        submen.add(newMenuItem("hmmmh"));
        submen.add(newMenuItem("indiefuttaluke"));
        fileMenu.add(submen);
        //*/

        fileMenu.add(newSeparator());
        fileMenu.add(newMenuItem("settings"));

        

        //ansicht
        JMenu viewMenu = newMenuMenu("view");

        viewMenu.add(newMenuItem("toggleState_showGrid"));
        viewMenu.add(newMenuItem("toggleState_showBounds"));
        viewMenu.add(newMenuItem("view_reset"));
        viewMenu.add(newMenuItem("view_removeMarkings"));

        //tools
        JMenu toolMenu = newToolMenu(getToolItemMenuSpecs()[0], ToolSources.MENUBAR);
        /*
        //share
        JMenu shareMenu = newMenuMenu("share");

        shareMenu.add(newMenuItem("feezbok"));
        shareMenu.add(newMenuItem("und im Bus"));
        */
        /////
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        if(toolMenu != null){
            menuBar.add(toolMenu);
        }

    }
    
    /**
     * Erstellt ein neues Menüitem
     * @param pAction Mit dem Item verbundene Aktion
     * @return
     */
    private JMenuItem newMenuItem(String pAction){
        JMenuItem it = new JMenuItem(r(pAction));
        it.addActionListener(viewListener);
        it.setActionCommand(pAction);
        it.setBackground(viewState.getTheme().getMenuColor());
        it.setForeground(viewState.getTheme().getWritingOnMenuColor());
        return it;
    }

    /**
     * Erstellt ein neues Menü
     * @param pNameKey Key des Menünamens
     * @return
     */
    private JMenu newMenuMenu(String pNameKey){
        JMenu jm = new JMenu(r(pNameKey));
        jm.setBackground(viewState.getTheme().getMenuColor());
        jm.setForeground(viewState.getTheme().getWritingOnMenuColor());

        jm.getPopupMenu().setBorder(new LineBorder(viewState.getTheme().getWritingOnMenuColor()));
        jm.getPopupMenu().setBackground(viewState.getTheme().getMenuColor());

        return jm;
    }
    /**
     * erstellt Toolmenü
     * @param pTMIL Liste an Toolspezifikationen
     * @param pSrc Aufrufart
     * @return
     */
    private JMenu newToolMenu(List<ToolMenuItemSpecs> pTMIL, ToolSources pSrc){
        if (pTMIL.isEmpty()){
            return null;
        }
        JMenu ret = newMenuMenu("tools");
        
        for (ToolMenuItemSpecs tmi : pTMIL){
            JMenuItem it = new JMenuItem(tmi.getName());
            it.addActionListener(viewListener);
            it.setActionCommand("TOOL_"+tmi.getId()+"_"+pSrc);
            it.setBackground(viewState.getTheme().getMenuColor());
            it.setForeground(viewState.getTheme().getWritingOnMenuColor());
            ret.add(it);
        }


        return ret;
    }

    /**
     * Erstellt einen neuen Seperator im theme 
     * @return Seperator
     */
    private JSeparator newSeparator(){
        JSeparator js = new JSeparator();
        js.setForeground(viewState.getTheme().getWritingOnMenuColor());
        js.setBackground(viewState.getTheme().getWritingOnMenuColor());

        return js;
    }

    /**
     * initialisiert das Fenster
     */
    private void initWindow(){
        window = new JFrame();
        window.setTitle(r("frameTitle"));

        ImageIcon ic = new ImageIcon("lgs/icons/"+r("frameIcon"));
        window.setIconImage(ic.getImage());

        //window.setSize(500,500);

        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.setLocationRelativeTo(null);

        window.setJMenuBar(menuBar);
        window.getContentPane().add(pane);
        window.pack();
    }

    /**
     * Setzt extra Text zum Titel
     * @param pAp anzufügender Text
     */
    public void setTitleAppend(String pAp){
        if(pAp == null){
            window.setTitle(r("frameTitle"));
        }else{
            window.setTitle(r("frameTitle")+" - \""+pAp+"\"");
        }
    }

    /**
     * Hängt dem Fenstertitel ein Sternchen an
     */
    public void titleAppendStar(){
        String title = window.getTitle();
        window.setTitle(title+" *");
    }

    /**
     * Öffnet Syyteminternes explorer Fenster zur Pfad und Dateiennamen auswahl
     * @param pLS 0/FileDialog.LOAD -> Laden; 1/FileDialog.SAVE -> Speichern
     * @return
     */
    public String fileChooser(int pLS, String pPath) throws Exception {
        //System.out.println(pPath);
        FileDialog fc = new FileDialog(window,r("fileChooserTitle"), pLS);
        //String[] exts = {"*.gr"};

        //fc.setFilenameFilter(new Filexts);
        //fc.setFile(null);
        Path pathOb = Paths.get(pPath);
        if(Files.notExists(pathOb)){
            new File(pPath).mkdirs();
        }

        fc.setDirectory(pPath);
        
        fc.setVisible(true);
        
        String path = fc.getFile();
        if(path==null){
            notification("notif_noPathSelected");
            throw new Exception("NoSelection");
        }

        viewListener.actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
        return fc.getDirectory()+path;
        
    }

    /**
     * Erstellt Notification Fenster
     * @param pNotification
     */
    public void notification(String pNotification){
        JOptionPane.showMessageDialog(window, r(pNotification), r("notif"), JOptionPane.WARNING_MESSAGE);
        viewListener.actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
    }

    /**
     * Ruft den Javainternen Colorpicker auf
     * @param pInitCol
     * @return Color
     */
    public Color colorDialog(Color pInitCol){
        if(pInitCol == null)pInitCol = getViewState().getTheme().getDefaultNodeColor();
        viewListener.actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
        return ColorPicker.showDialog(window, pInitCol, getViewState().getTheme(), getResourceBundle());
    }

    /**
     * Dialog zur Eingabe eines Strings
     * @param pInitVal
     * @return
     */
    public String stringDialog(String pInitVal){
        viewListener.actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
        return (String) JOptionPane.showInputDialog(window, r("input_string"), "", JOptionPane.DEFAULT_OPTION, null, null,pInitVal);
    }

    /**
     * Dialog zur Eingabe einer Zahl
     * @param pInitVal
     * @return
     */
    public int intDialog(int pInitVal){
        int ret = pInitVal;
        try {
            ret = Integer.parseInt((String)JOptionPane.showInputDialog(window, r("input_int"), "", JOptionPane.DEFAULT_OPTION, null, null,pInitVal));
        }catch(Exception e){

        }
        if(ret<(-Integer.MAX_VALUE/2)||ret>(Integer.MAX_VALUE/2)){
            ret = pInitVal;
        }

        viewListener.actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
        return ret;
    }

    /**
     * Ruft einen Dialog, der den Nutzer zur Angabe eines Knotes auffordert auf
     * @return -1 wenn abgebrochen
     */
    public int nodeDialog(){
        String tit = window.getTitle();
        window.setTitle(r("input_node"));
        //notification("input_node");
        int[] n = {-1};

        //modales Fenster über aktuellem um Mausklick zu registrieren
        JDialog overlay = new JDialog(getWindow(),"",true);
                    
        overlay.setAlwaysOnTop(true);
        Rectangle bounds = pane.getBounds();
        bounds.setLocation(pane.getLocationOnScreen());
        overlay.setBounds(bounds);
        overlay.setUndecorated(true);
        overlay.setResizable(false);
                    
        overlay.setOpacity(0.2f);

        //diverse Listener um entsprechende rückgaben festzulegen
        WindowFocusListener wfl = new WindowFocusListener() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                n[0] = -1;
                overlay.dispose();
            }
            
        };
        ComponentListener cl = new ComponentListener() {

        @Override
        public void componentResized(ComponentEvent e) {
                            
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            n[0] = -1;
            overlay.dispose();
        }

        @Override
        public void componentShown(ComponentEvent e) {
                            
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            n[0] = -1;
            overlay.dispose();
            }      
        };
        WindowListener wl = new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                            
            }

            @Override
            public void windowClosing(WindowEvent e) {
                n[0] = -1;
                overlay.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                            
            }

            @Override
            public void windowIconified(WindowEvent e) {
                n[0] = -1;
                overlay.dispose();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                            
            }

            @Override
            public void windowActivated(WindowEvent e) {
                            
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                            
            }
                        
        };
        KeyListener kl = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                            
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==27){
                    n[0] = -1;
                    overlay.dispose();
                }   
            }

            @Override
            public void keyReleased(KeyEvent e) {
                            
            }
                        
        };
        MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==1){
                    int cN = pane.getFirstNodeAtPoint(new ActualPoint(e));
                    if(cN >= 0){
                        n[0] = cN;
                        overlay.dispose();
                    }
                }else{
                    n[0] = -1;
                    overlay.dispose();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                            
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                            
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                            
            }

            @Override
            public void mouseExited(MouseEvent e) {
                            
            }
                        
        };
        overlay.addWindowFocusListener(wfl);
        overlay.addComponentListener(cl);
        overlay.addWindowListener(wl);
        overlay.addKeyListener(kl);
        overlay.addMouseListener(ml);

        overlay.setVisible(true);


        window.setTitle(tit);
        return n[0];
    }

    /**
     * Startet optionsidalog
     * @param pMsg      Angezeigte Nachricht
     * @param pOptions  Array das die Auswahloptionen als Strings enthält
     * @return          getroffene Auswahl
     */
    public int optionDialog(String pMsg, String[] pOptions){
        viewListener.actionPerformed(new ActionEvent(this, 0, "rmKeyMods"));
        return JOptionPane.showOptionDialog(window, pMsg, r("notif"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, pOptions, pOptions[0]);
    }

    /**
     * Aktualisiert das Fenster
     * @param pRM Modus, in dem aktualisiert werden soll
     */
    public void redraw(RedrawModes pRM){
        pane.setNextRedrawMode(pRM);
        pane.repaint();
        //pane.setNextRedrawMode(RedrawModes.MOVE);
    }

    /**
     * Zeigt frei schwebendes popup menu an
     * @param pPt Punkt, an dem das Menü angezeigt werden soll
     */
    public void freePopUp(ActualPoint pPt){
        //todo vtl popupmenus iwo speichern
        JPopupMenu menu = new JPopupMenu();

        menu.setBackground(getViewState().getTheme().getMenuColor());
        menu.setForeground(getViewState().getTheme().getWritingOnMenuColor());
        menu.setBorder(new LineBorder(getViewState().getTheme().getWritingOnMenuColor()));

        menu.add(newMenuItem("file_save"));
        menu.add(newMenuItem("file_load"));
        menu.add(newSeparator());
        menu.add(newMenuItem("action_newNode"));
        JMenu toolMenu = newToolMenu(getToolItemMenuSpecs()[2],ToolSources.SPOT);
        if(toolMenu!=null){
            menu.add(toolMenu);
        }

        menu.show(pane, pPt.getX(), pPt.getY());
    }

    /**
     * Zeigt frei schwebendes popup menu zum Kontext eines Knotens an
     * @param pPt Punkt, an dem das Menü angezeigt werden soll
     */
    public void nodePopUp(ActualPoint pPt){
        JPopupMenu menu = new JPopupMenu();

        menu.setBackground(getViewState().getTheme().getMenuColor());
        menu.setForeground(getViewState().getTheme().getWritingOnMenuColor());
        menu.setBorder(new LineBorder(getViewState().getTheme().getWritingOnMenuColor()));

        menu.add(newMenuItem("nodeAction_recolour"));
        menu.add(newMenuItem("nodeAction_rename"));
        menu.add(newMenuItem("nodeAction_delete"));
        menu.add(newMenuItem("nodeAction_removeEdges"));

        JMenu toolMenu = newToolMenu(getToolItemMenuSpecs()[1],ToolSources.NODE);
        if(toolMenu!=null){
            menu.add(newSeparator());
            menu.add(toolMenu);
        }

        menu.show(pane, pPt.getX(), pPt.getY());
    }
}