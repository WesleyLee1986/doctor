package com.tiandao.geniusdoctor.application;

import android.app.Application;

import database.DatabaseManager;

public class WinnerApplication extends Application {
    private static WinnerApplication winnerApplication;

    @Override
    public void onCreate() {
        winnerApplication = this;
        super.onCreate();
        DatabaseManager.getInstance().init(this);
    }

    public static WinnerApplication getWinnerApplication() {
        return winnerApplication;
    }
}
