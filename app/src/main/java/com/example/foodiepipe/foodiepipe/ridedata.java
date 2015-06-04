package com.example.foodiepipe.foodiepipe;

/**
 * Created by gbm on 5/18/15.
 */
public class ridedata {
    String destination;
    String source;
    String date;
    String rideownerName;
    String rideOwneremail;
    String rideownerPhoneNumber;

    public String getRideownercustomernumber() {
        return rideownercustomernumber;
    }

    public void setRideownercustomernumber(String rideownercustomernumber) {
        this.rideownercustomernumber = rideownercustomernumber;
    }

    String rideownercustomernumber;
    String pickUpLocation;
    String rideId;
    String rideFlag;

    public String getRideownerPhoneNumber() {
        return rideownerPhoneNumber;
    }

    public void setRideownerPhoneNumber(String rideownerPhoneNumber) {
        this.rideownerPhoneNumber = rideownerPhoneNumber;
    }

    public String getRideownerName() {
        return rideownerName;
    }

    public void setRideownerName(String rideownerName) {
        this.rideownerName = rideownerName;
    }

    public String getRideOwneremail() {
        return rideOwneremail;
    }

    public void setRideOwneremail(String rideOwneremail) {
        this.rideOwneremail = rideOwneremail;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getRideFlag() {
        return rideFlag;
    }

    public void setRideFlag(String rideFlag) {
        this.rideFlag = rideFlag;
    }



    public String getNoresults() {
        return noresults;
    }

    public void setNoresults(String noresults) {
        this.noresults = noresults;
    }

    String noresults;

    public String getTodayortomorrow() {
        return todayortomorrow;
    }

    public void setTodayortomorrow(String todayortomorrow) {
        this.todayortomorrow = todayortomorrow;
    }

    String todayortomorrow;

    public String getDate() {
        return date;
    }
    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public ridedata(String source,String destination,String date,String rideownerName,String rideownerPhoneNumber, String rideOwneremail,String rideId) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.rideownerName = rideownerName;
        this.rideOwneremail = rideOwneremail;
        this.rideownerPhoneNumber = rideownerPhoneNumber;
        this.rideId = rideId;
    }
    public ridedata(String source,String destination,String date) {
        this.source = source;
        this.destination = destination;
        this.date = date;

    }
    public ridedata(String source,String destination,String date,String rideId,String rideFlag,String rideownercustomernumber) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.rideId = rideId;
        this.rideFlag = rideFlag;
        this.rideownercustomernumber = rideownercustomernumber;
    }
}
