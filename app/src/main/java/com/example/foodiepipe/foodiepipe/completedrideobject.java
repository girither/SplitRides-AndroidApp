package com.example.foodiepipe.foodiepipe;

import java.util.List;

/**
 * Created by gbm on 7/20/15.
 */
public class completedrideobject {
    private String rideStartedAt;
    private String rideEndedAt;
    private String baseFare;

    public List<splitfaredataobject> getListofsplitfare() {
        return listofsplitfare;
    }

    public void setListofsplitfare(List<splitfaredataobject> listofsplitfare) {
        this.listofsplitfare = listofsplitfare;
    }

    private List<splitfaredataobject> listofsplitfare;
    public String getTodayortomorrow() {
        return todayortomorrow;
    }

    public void setTodayortomorrow(String todayortomorrow) {
        this.todayortomorrow = todayortomorrow;
    }

    String todayortomorrow;

    public String getPfareForDistanceTravelled() {
        return pfareForDistanceTravelled;
    }

    public void setPfareForDistanceTravelled(String pfareForDistanceTravelled) {
        this.pfareForDistanceTravelled = pfareForDistanceTravelled;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getFareForTimeSpent() {
        return fareForTimeSpent;
    }

    public void setFareForTimeSpent(String fareForTimeSpent) {
        this.fareForTimeSpent = fareForTimeSpent;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getRideEndedAt() {
        return rideEndedAt;
    }

    public void setRideEndedAt(String rideEndedAt) {
        this.rideEndedAt = rideEndedAt;
    }

    public String getRideStartedAt() {
        return rideStartedAt;
    }

    public void setRideStartedAt(String rideStartedAt) {
        this.rideStartedAt = rideStartedAt;
    }

    public completedrideobject(String uniqueId, String totalFare, String fareForTimeSpent, String pfareForDistanceTravelled, String baseFare, String rideEndedAt, String rideStartedAt) {
        this.uniqueId = uniqueId;
        this.totalFare = totalFare;
        this.fareForTimeSpent = fareForTimeSpent;
        this.pfareForDistanceTravelled = pfareForDistanceTravelled;
        this.baseFare = baseFare;
        this.rideEndedAt = rideEndedAt;
        this.rideStartedAt = rideStartedAt;
    }

    public completedrideobject(String uniqueId, String totalFare, String fareForTimeSpent, String pfareForDistanceTravelled, String baseFare, String rideEndedAt, String rideStartedAt,List<splitfaredataobject> listsplitfare) {
        this.uniqueId = uniqueId;
        this.totalFare = totalFare;
        this.fareForTimeSpent = fareForTimeSpent;
        this.pfareForDistanceTravelled = pfareForDistanceTravelled;
        this.baseFare = baseFare;
        this.rideEndedAt = rideEndedAt;
        this.rideStartedAt = rideStartedAt;
        this.listofsplitfare = listsplitfare;
    }


    private String pfareForDistanceTravelled;
    private String fareForTimeSpent;
    private String totalFare ;
    private String uniqueId;
}
