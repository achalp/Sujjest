package com.tota.sujjest.Entity;

/**
 * Created by aprabhakar on 1/8/16.
 */
public class ApplicationState {
    private static ApplicationState ourInstance = new ApplicationState();
    private Options options;

    public AppStateEnum getAppState() {
        return appState;
    }

    public void setAppState(AppStateEnum appState) {
        this.appState = appState;
    }

    private AppStateEnum appState;

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }



    public static ApplicationState getInstance() {
        return ourInstance;
    }

    private ApplicationState() {
        this.options =  new Options();
        this.appState = AppStateEnum.HOME_SCREEN;
    }
}
