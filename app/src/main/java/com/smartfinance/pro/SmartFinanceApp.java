package com.smartfinance.pro;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * Application class — initializes global app state.
 * Creates notification channels for budget alerts.
 */
public class SmartFinanceApp extends Application {

    public static final String CHANNEL_BUDGET_ID   = "budget_alerts";
    public static final String CHANNEL_GENERAL_ID  = "general";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            // Budget alert channel
            NotificationChannel budgetChannel = new NotificationChannel(
                CHANNEL_BUDGET_ID,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            budgetChannel.setDescription("Notifikasi ketika budget mendekati atau melebihi batas");
            manager.createNotificationChannel(budgetChannel);

            // General channel
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_GENERAL_ID,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(generalChannel);
        }
    }
}
