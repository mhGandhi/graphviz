package app;

/**
 * Verpackung Repräsentant für das gesamte Programm
 */
public class App {
    public App(Tool[] pTools){
        @SuppressWarnings("unused")
        Controller c = new Controller(pTools);
        //todo restartfunc, dijk neu, instading vtl, tiefen und breite dings fixen, imp ex schöner, auto light/dark, exclude # lines when reading, fix moving, auto dists
    }
}
