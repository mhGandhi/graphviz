package app;

/**
 * Verpackung Repräsentant für das gesamte Programm
 */
public class App {
    public App(Tool[] pTools){
        @SuppressWarnings("unused")
        Controller c = new Controller(pTools);
        //todo restartfunc, instading vtl, imp ex schöner, auto light/dark, fix moving, auto dists, sicherheitsmaßnahmen gegen Tools (safe-aus, nicht mehrere gleichzeitig blalblabla), live breit, neurales netz tool, randomgraph tool
    }
}
