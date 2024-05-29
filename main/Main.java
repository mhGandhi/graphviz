import app.Tool;
import app.App;

/**
 * Hauptklasse
 */
public class Main {
    /**
     * Register für angepasste Benutzertools
     */
    public static Tool[] tools = {
        new ExampleTool(),
        new Breitensuche(),
		new Tiefensuche(),
        new Path()
        //,[...]
    };

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        App a = new App(tools);
    }
}