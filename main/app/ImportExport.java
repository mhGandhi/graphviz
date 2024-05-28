package app;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.ResourceBundle;


import app.model.Edge;
import app.model.Model;
import app.model.Node;
import app.model.NodePresentation;

/**
 * Dient dem Importieren und Exportieren von Graphen
 */
public abstract class ImportExport {

    private static ResourceBundle res;

    /**
     * ruft Dialoge zum exportieren von Graphen auf
     * @param pAc
     */
    public static void exportGraph(ActionHandler pAc){
        Controller con = pAc.getController();
        res = con.getView().getResourceBundle();
        String[] options = {
            r("option_list"),
            r("option_matrix")
        };
        int opt = con.getView().optionDialog(r("notif_selectFormat"), options);
        switch (opt) {
            case 0://ex to list
                try {
                    FileSS.stringToFile(modelToList(con.getModel()), con.getView().fileChooser(FileDialog.SAVE, con.getProperties().getProperty("defaultSavePath")));
                } catch (Exception e) {
                    //
                }
                
                break;
            case 1://ex to matrix
                try {
                    FileSS.stringToFile(modelToMatrix(con.getModel()), con.getView().fileChooser(FileDialog.SAVE, con.getProperties().getProperty("defaultSavePath")));
                } catch (Exception e) {
                    //
                }
                break;
            default:
                break;
        }
    }

    /**
     * ruft Dialoge zum importienen von Graphen auf
     * @param pAc
     */
    public static void importGraph(ActionHandler pAc){
        Controller con = pAc.getController();
        res = con.getView().getResourceBundle();

        String[] options = {
            r("option_list"),
            r("option_matrix"),
            r("file")
        };
        int opt = con.getView().optionDialog(r("notif_selectFormat"), options);

        String path;
        try {  
            path = con.getView().fileChooser(FileDialog.LOAD, con.getProperties().getProperty("defaultSavePath"));
        } catch (Exception e) {
            return;
        }
        Model newModel = null;
        switch (opt) {
            case 0://imp from list
                try {
                    newModel=(modelFromList(FileSS.fileToString(path)));
                } catch (Exception e) {
                    //e.printStackTrace();
                    con.getView().notification("notif_couldntLoadFile");
                }
                break;
            case 1://imp from matrix
                try {
                    newModel=(modelFromMatrix(FileSS.fileToString(path)));
                } catch (Exception e) {
                    con.getView().notification("notif_couldntLoadFile");
                }
                break;
            case 2://imp from file
                try {
                    newModel=((Model)FileSS.loadFromFile(path));
                } catch (Exception e) {
                    con.getView().notification("notif_couldntLoadFile");
                }
                break;
            default:
                break;
            }
        if(newModel!=null){
            
            String[] opts = {
                r("file_import"),
                r("file_load")
            };
            int sl = con.getView().optionDialog(r("file_import"), opts);
            switch (sl) {
                case 0://import
                    con.getModel().appendModel(newModel);
                    break;
                case 1:
                    if(pAc.getController().assertGraphSaved()){
                        con.setModel(newModel);
                    }
                    break;
                default:
                    break;
            }
            
            
        }
    }


    /**
     * Konvertiert Adjazenzliste in Modell
     * @param pAdjacencyList
     * @return
     */
    public static Model modelFromList(String pAdjacencyList){
        Model model = new Model();

        HashMap<Integer, Integer> oldIDS = new HashMap<Integer, Integer>();
        HashMap<String, Integer> connections = new HashMap<String, Integer>();
        Queue<String> connectionList = new LinkedList<>();
        String tmp = "";
        int id = 0;
        int connectID = 0;
        int weight = 0;
        for(int i = 0; i < pAdjacencyList.length(); i++){
            try {
                if(pAdjacencyList.substring(i + 1, i + 2).equals("\"")){
                    id = Integer.parseInt(pAdjacencyList.substring(i, i + 1));
                } 
                if(weight > 0){
                    connectID = Integer.parseInt(pAdjacencyList.substring(i, i + 1));
                    connections.put(id + "," + connectID, weight);
                    connectionList.add(id + "," + connectID);
                    weight = 0;
                }
                
            } catch (Exception e) {

            }
            if(pAdjacencyList.substring(i, i + 1).equals("\"")){
                while(!pAdjacencyList.substring(i + 1, i + 2).equals("\"")){
                    tmp = tmp + pAdjacencyList.substring(i + 1, i + 2);
                    i++;
                }
                i++;
                if(pAdjacencyList.substring(i, i + 1).equals("\"")){
                    Node node = model.getGraph().addNode();
                    int tid = node.getId();
                    NodePresentation nPres = new NodePresentation(tid);
                    nPres.setLabel(tmp);
                    model.getNodes().putIfAbsent(tid, node);
                    model.getNodePresentations().putIfAbsent(tid, nPres);
                    model.getIds().add(tid);
                    oldIDS.put(id, tid);
                    tmp = "";
                }
            }
                
            if(pAdjacencyList.substring(i, i + 1).equals("[")){
                String sWeight = "";
                while(!pAdjacencyList.substring(i + 1, i + 2).equals("]")){
                    sWeight = sWeight + pAdjacencyList.substring(i + 1, i + 2);
                    i++;
                }
                weight = Integer.parseInt(sWeight);
            }
        }

        for(int i = 0; i < connectionList.size() + 1; i++){
            String connect = connectionList.poll();
            String sStart = "";
            int start = 0;
            String sEnd = "";
            int end = 0;
            for (int b = 0; b < connect.length(); b++){
            while(!connect.substring(b, b + 1).equals(",")){
                sStart = sStart + connect.substring(b, b + 1); 
                b++;
            }
            b++;
            start = Integer.parseInt(sStart);
            sEnd = connect.substring(b);
            end = Integer.parseInt(sEnd);
            }
            sStart = "";
            sEnd = "";
            
            model.getGraph().addEdge(oldIDS.get(start), oldIDS.get(end), connections.get(start + "," + end));
        }
        return model;
    }

    /**
     * Konvertiert Adjazenzmatrix in Model [KAPUTT], Lennis Tränen weilen hier
     * @param pAdjMatrix
     * @return
     */
    @Deprecated
    public static Model modelFromMatrixx(String pAdjMatrix){
        Model model = new Model();

        HashMap<Integer, Integer> oldIDS = new HashMap<Integer, Integer>();
        HashMap<String, Integer> connections = new HashMap<String, Integer>();
        Queue<String> connectionList = new LinkedList<>();
        List<String> verzweiflung = new LinkedList<>();
        String tmp = "";
        int id = 0;
        int connectID = 0;
        String sWeight = "";
        int weight = 0;
        String sCheck = "";

        for(int i = 0; i < pAdjMatrix.length(); i++){
            try {
                if(pAdjMatrix.substring(i + 1, i + 2).equals("\"")){
                    id = Integer.parseInt(pAdjMatrix.substring(i, i + 1));
                } 
                
            } catch (Exception e) {

            }
            if(pAdjMatrix.substring(i, i + 1).equals("\"")){
                while(!pAdjMatrix.substring(i + 1, i + 2).equals("\"")){
                    tmp = tmp + pAdjMatrix.substring(i + 1, i + 2);
                    i++;
                }
                i++;
                if(pAdjMatrix.substring(i, i + 1).equals("\"")){
                    Node node = model.getGraph().addNode();
                    int tid = node.getId();
                    NodePresentation nPres = new NodePresentation(tid);
                    nPres.setLabel(tmp);
                    model.getNodes().putIfAbsent(tid, node);
                    model.getNodePresentations().putIfAbsent(tid, nPres);
                    model.getIds().add(tid);
                    oldIDS.put(id, tid);
                    tmp = "";
                }
            }
            try {
                if(pAdjMatrix.substring(i, i + 1).equals(":")){
                    
                    while(!pAdjMatrix.substring(i + 1, i + 2).equals(",")){
                        sWeight = sWeight + pAdjMatrix.substring(i + 1, i + 2);
                        i++;                            
                    }
                    for(int c = 0; c < sWeight.length(); c++){
                        if(!sWeight.substring(c, c + 1).equals(" ")){
                            sCheck = sCheck + sWeight.substring(c, c + 1);
                        }   
                    }
                    //if(!sCheck.equals("X"))
                    verzweiflung.add(sCheck);
                    sCheck = "";
                    sWeight = "";
                }
                for(connectID = 0; connectID < verzweiflung.size(); connectID++)
                if(!verzweiflung.get(connectID).equals("X")){
                    weight = Integer.parseInt(verzweiflung.get(connectID));
                    connections.put(id + "," + connectID, weight);
                    connectionList.add(id + "," + connectID);
                    weight = 0;
                    i++;
                   
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < connectionList.size() + 1; i++){
            String connect = connectionList.poll();
            String sStart = "";
            int start = 0;
            String sEnd = "";
            int end = 0;
            for (int b = 0; b < connect.length(); b++){
            while(!connect.substring(b, b + 1).equals(",")){
                sStart = sStart + connect.substring(b, b + 1); 
                b++;
            }
            b++;
            start = Integer.parseInt(sStart);
            sEnd = connect.substring(b);
            end = Integer.parseInt(sEnd);
            }
            sStart = "";
            sEnd = "";
            
            model.getGraph().addEdge(oldIDS.get(start), oldIDS.get(end), connections.get(start + "," + end));
        }
        System.out.println(verzweiflung.toString());
        System.out.println(connectionList.toString());
        System.out.println(connections.toString());
        System.out.println(oldIDS.toString());
        return model;
    }

    /**
     * Konvertiert Adjazenzmatrix in Model
     * @param pAdjMatrix
     * @return
     */
    public static Model modelFromMatrix(String pAdjMatrix){
        Model model = new Model();

        //clear whitespace
        String text = "";
        {
            boolean inQuotes = false;
            for (int i = 0; i < pAdjMatrix.toCharArray().length; i++) {
                char ch = pAdjMatrix.toCharArray()[i];
                if (ch == '"') {
                    inQuotes = !inQuotes;
                }
                if (
                        !Character.isWhitespace(ch)
                                || inQuotes
                                || ch == '\n'
                ) {
                    text += ch;
                }
            }
        }

        //divide into lines (with content)
        List<String> lines = new ArrayList<String>();
        {
            int last = 0;
            for (int i = 0; i < text.toCharArray().length; i++) {
                if (text.toCharArray()[i] == '\n') {
                    String line = text.substring(last, i);
                    last = i + 1;
                    if (!line.isBlank()) {
                        lines.add(line);
                    }
                }
            }
        }

        //create nodes, add names and remove from lines
        List<String> connections = new ArrayList<String>();
        {
            for (String ln : lines) {
                int nodeId = model.addNode();
                if (ln.startsWith("\"")) {
                    String name = "";
                    int secondAp = 1;
                    for (int j = 1; j < ln.toCharArray().length; j++) {
                        if (ln.toCharArray()[j] == '"') {
                            secondAp = j;
                            name = ln.substring(1, secondAp);
                            model.getNodePresentation(nodeId).setLabel(name);
                            break;
                        }
                    }
                    ln = ln.substring(secondAp + 1);
                }
                connections.add(ln);
            }
        }

        //form edges
        for (int j = 0; j < connections.size(); j++) {
            String ln = connections.get(j);
            //System.out.println(ln);
            int i = 0;
            int conTo = 0;
            int last = 0;
            do{
                char ch = ln.toCharArray()[i];
                if(ch==','){
                    String dist = ln.substring(last,i);
                    last = i+1;
                    try{
                        int ds = Integer.parseInt(dist);
                        model.getGraph().addDirectedEdge(j,conTo,ds);
                    }catch(Exception e){
                        //no connection
                    }
                    conTo++;
                }

                i++;
            }while(i<ln.length());
        }

        System.out.println(model.getGraph().toString());
        return model;
    }

    private static String r(String pKey){
        try{
            return res.getString(pKey);
        }catch(MissingResourceException e){
            return "[IMPEXKEY_"+pKey+"]";
        }
    }


    /**
     * Konvertiert Model in Adjazenzmatrix
     * @param pModel
     * @return
     */
    private static String modelToMatrix(Model pModel){
        List<Node> nodes = new ArrayList<Node>(pModel.getGraph().getAdjList().keySet());

        {//17.03.2024 20:33 cool zu wissen, dass das geht
            boolean sorted = false;
            while(!sorted){
                sorted = true;
                for (int i = 0; i < nodes.size()-1; i++) {
                    if(nodes.get(i).getId()>nodes.get(i+1).getId()){
                        Node tmp = nodes.get(i);
                        nodes.set(i, nodes.get(i+1));
                        nodes.set(i+1, tmp);
                        sorted = false;
                    }
                }
            }
        }

        String ret = "";
        for (Node nodeFrom : nodes){
            String lb = pModel.getNodePresentation(nodeFrom.getId()).getLabel();
            if(!lb.isBlank()){
                ret += "\"" + lb + "\"";
            }
            for (Node nodeTo : nodes) {
                if(!pModel.getGraph().hasEdgeTo(nodeFrom.getId(), nodeTo.getId())){
                    ret += "X, ";
                }else{
                    List<Edge> egs = pModel.getGraph().getAdjList().get(nodeFrom);
                    for (Edge eg : egs) {
                        if(eg.getNode().getId()==nodeTo.getId()){
                            ret += eg.getWeight()+", ";
                        }
                    }
                }
            }
            ret += "\n";
        }
        return ret;
    }


    /**
     * Konvertiert Model zu Adjazenzliste
     * @param pModel
     * @return
     */
    private static String modelToList(Model pModel){
        String ret = "";
    
        for (Node n : pModel.getGraph().getAdjList().keySet()) {
            ret += n.getId() + "\"" + pModel.getNodePresentation(n.getId()).getLabel() + "\"";
            for (Edge e : pModel.getGraph().getOutEdges(n.getId())) {
                ret += " ["+e.getWeight()+"]> "+e.getNode().getId();
            }
            ret += "\n";
        }
        return ret;
    }
}