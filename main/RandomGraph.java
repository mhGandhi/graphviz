import app.Controller;
import app.ImportExport;
import app.Tool;
import app.model.Edge;
import app.view.NodeComponent;
import app.view.RedrawModes;
import app.view.point.InSysPoint;

import java.util.*;

public class RandomGraph implements Tool{

    private final boolean positiveEdges = true;
    private final boolean zeroEdges = false;
    private final boolean negativeEdges = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId(){
        return "aa:randomGraph";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(){
        return "Random Graph";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return "Fügt zufälligen Graphen mit beliebiger Länge ein.\n" +
                "Aktuelle Konfiguration:\n" +
                (positiveEdges ? "positiv gewichtete Kanten":"keine positiv gewichteten Kanten") +"\n"+
                (zeroEdges ? "Kanten mit Gewicht 0":"kein Kanten mit Gewicht 0") +"\n"+
                (negativeEdges ? "negativ gewichtete Kanten":"keine negativ gewichteten Kanten");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthor(){
        return "Adrian Akipi";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showInMenu() {
        return true; //ob das Tool im Programm angezeigt werden soll
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromMenuBar() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromNode() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromSpot() {
        return true;
    }
    //
    /**
     * {@inheritDoc}
     */
    @Override
    public void runFromMenuBar(Controller pC){

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runOnNode(Controller pC, int pNodeId) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {
        int nodeCount = pC.getView().intDialog(0);
        if(nodeCount<=1){
            pC.getView().nativeNotification("Invalid Number");
            return;
        }

        String mat = "";
        for(int j = 0; j < nodeCount; j++){
            for (int i = 0; i < nodeCount; i++) {
                String num = "1";//todo
                mat += num+",";
            }
            mat += "{["+pPt.getX()+"|"+pPt.getY()+"]}\n";
        }

        pC.getModel().appendModel(ImportExport.modelFromMatrix(mat));

        pC.getView().redraw(RedrawModes.RESCALE);
    }

}