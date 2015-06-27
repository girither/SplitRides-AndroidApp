package com.example.foodiepipe.foodiepipe;

/**
 * Created by gbm on 6/26/15.
 */
public class notificationdata {


    String destination;
    String source;
    String date;
    String requestId;
    String todayortomorrow;


    public String getTodayortomorrow() {
        return todayortomorrow;
    }

    public void setTodayortomorrow(String todayortomorrow) {
        this.todayortomorrow = todayortomorrow;
    }

    public notificationdata(String destination, String source, String date) {
        this.destination = destination;
        this.source = source;
        this.date = date;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }



    public notificationdata(String destination, String source, String date, String requestId) {
        this.destination = destination;
        this.source = source;
        this.date = date;
        this.requestId = requestId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


}
