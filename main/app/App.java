package app;

/**
 * Verpackung Repräsentant für das gesamte Programm
 */
public class App {
    public App(Tool[] pTools){
        @SuppressWarnings("unused")
        Controller c = new Controller(pTools);
        //todo restartfunc, instading vtl, imp ex schöner, auto light/dark, exclude # lines when reading, fix moving, auto dists
    }
}
