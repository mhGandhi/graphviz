package app;

/**
 * Verpackung Repräsentant für das gesamte Programm
 */
public class App {
    public App(Tool[] pTools){
        @SuppressWarnings("unused")
        Controller c = new Controller(pTools);
        //todo restartfunc
    }
}
