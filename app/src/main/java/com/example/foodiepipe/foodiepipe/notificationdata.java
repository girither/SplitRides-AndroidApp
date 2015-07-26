package com.example.foodiepipe.foodiepipe;

import java.util.List;

/**
 * Created by gbm on 6/26/15.
 */
public class notificationdata {


    String destination;
    String source;
    String date;
    String requestId;
    String rideId;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getOwnerrideid() {
        return ownerrideid;
    }

    public void setOwnerrideid(String ownerrideid) {
        this.ownerrideid = ownerrideid;
    }

    String ownerrideid;
    String todayortomorrow;
    List<customer> customerlistdata;

    public List<customer> getCustomerlistdata() {
        return customerlistdata;
    }

    public void setCustomerlistdata(List<customer> customerlistdata) {
        this.customerlistdata = customerlistdata;
    }



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



    public notificationdata(String source,String destination, String date, String requestId) {
        this.destination = destination;
        this.source = source;
        this.date = date;
        this.requestId = requestId;
    }

    public notificationdata(String source,String destination, String date,List<customer> customerlistdata,String requestId) {
        this.destination = destination;
        this.source = source;
        this.date = date;
        this.customerlistdata = customerlistdata;
        this.requestId = requestId;
    }

    public notificationdata(String source,String destination,String date,List<customer> customerlistdata,String rideId,String ownerrideid) {
        this.destination = destination;
        this.source = source;
        this.date = date;
        this.customerlistdata = customerlistdata;
        this.rideId = rideId;
        this.ownerrideid = ownerrideid;
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
