import app.view.RedrawModes;
import app.view.point.InSysPoint;
import app.Tool;
import app.Controller;


public class ExampleTool implements Tool{

    /**
     * {@inheritDoc}
     * pC.getView().getContentPanel().setEdgeMarked(bool) (+ setDirEdgeMarked(bool))
     */
    @Override
    public String getId(){
        return "rw:exampleTool_91448";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(){
        return "Beispieltool";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return "Beispieltool zur Demonstration der Implementation eigener Tools.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthor(){
        return "Name des Erstellers";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showInMenu() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromMenuBar() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRanFromNode() {
        return true;
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
        System.out.println("Exampletool from Menubar");
        int n = pC.getView().nodeDialog();
        System.out.println("selected node "+n);
        if(n>=0){
            runOnNode(pC, n);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runOnNode(Controller pC, int pNodeId) {
        pC.removeMarkings();
        pC.getView().getContentPanel().getNodeById(pNodeId).setMarked(true);
        pC.getView().redraw(RedrawModes.RESCALE);
        System.out.println("Exampletool from Node "+pNodeId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAtSpot(Controller pC, InSysPoint pPt) {
        System.out.println("Exampletool from Spot "+pPt);
    }

}
