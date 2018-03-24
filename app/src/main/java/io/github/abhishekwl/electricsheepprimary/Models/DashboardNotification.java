package io.github.abhishekwl.electricsheepprimary.Models;

import java.util.Date;

/**
 * Created by ghost on 3/24/18.
 */

public class DashboardNotification {

    private Date currentDateAndTime;
    private String notificationContent;

    public DashboardNotification(Date currentDateAndTime, String notificationContent) {
        this.currentDateAndTime = currentDateAndTime;
        this.notificationContent = notificationContent;
    }

    public Date getCurrentDateAndTime() {
        return currentDateAndTime;
    }

    public void setCurrentDateAndTime(Date currentDateAndTime) {
        this.currentDateAndTime = currentDateAndTime;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }
}
