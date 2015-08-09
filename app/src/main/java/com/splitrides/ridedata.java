package com.splitrides;

import java.util.List;

/**
 * Created by gbm on 5/18/15.
 */
public class ridedata {
    String destination;
    String source;
    String date;
    String ridestatus;
    String joinedridestatus="";

    public String getJoinedridestatus() {
        return joinedridestatus;
    }

    public void setJoinedridestatus(String joinedridestatus) {
        this.joinedridestatus = joinedridestatus;
    }

    public String getSourcelat() {
        return sourcelat;
    }

    public void setSourcelat(String sourcelat) {
        this.sourcelat = sourcelat;
    }

    public String getSourcelong() {
        return sourcelong;
    }

    public void setSourcelong(String sourcelong) {
        this.sourcelong = sourcelong;
    }

    public String getDestinationlat() {
        return destinationlat;
    }

    public void setDestinationlat(String destinationlat) {
        this.destinationlat = destinationlat;
    }

    public String getDestinationlng() {
        return destinationlng;
    }

    public void setDestinationlng(String destinationlng) {
        this.destinationlng = destinationlng;
    }

    String sourcelat;
    String sourcelong;
    String destinationlat;
    String destinationlng;

    public String getEncodedpolyline() {
        return encodedpolyline;
    }

    public void setEncodedpolyline(String encodedpolyline) {
        this.encodedpolyline = encodedpolyline;
    }

    String encodedpolyline;

    public String getRidestatus() {
        return ridestatus;
    }

    public void setRidestatus(String ridestatus) {
        this.ridestatus = ridestatus;
    }

    public List<customer> getCustomerlistdata() {
        return customerlistdata;
    }

    public void setCustomerlistdata(List<customer> customerlistdata) {
        this.customerlistdata = customerlistdata;
    }

    List<customer> customerlistdata;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;


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

    public ridedata(String source,String destination,String date,String status,List<customer> customerlistdata,String rideId) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.customerlistdata = customerlistdata;
        this.rideId = rideId;
        this.status = status;
    }
    public ridedata(String source,String destination,String date,List<customer> customerlistdata,String rideflag,String rideId,String rideStatus,String joinedridestatus) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.customerlistdata = customerlistdata;
        this.rideFlag = rideflag;
        this.rideId = rideId;
        this.ridestatus = rideStatus;
        this.joinedridestatus = joinedridestatus;
    }
    public ridedata(String source,String destination,String date,List<customer> customerlistdata,String rideflag,String rideId) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.customerlistdata = customerlistdata;
        this.rideFlag = rideflag;
        this.rideId = rideId;
    }
    public ridedata(String source,String destination,String date,String rideId) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.rideId = rideId;

    }
    public ridedata(String source,String destination,String date) {
        this.source = source;
        this.destination = destination;
        this.date = date;
    }
    public ridedata(String source,String destination,String date,String rideId,String rideFlag,String rideownercustomernumber,String encodedpolyline,String joinedridestatus) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.rideId = rideId;
        this.rideFlag = rideFlag;
        this.rideownercustomernumber = rideownercustomernumber;
        this.encodedpolyline = encodedpolyline;
        this.joinedridestatus = joinedridestatus;
    }
    public ridedata(String source,String destination,String date,String rideId,String rideFlag,String rideownercustomernumber,String encodedpolyline, String sourcelat,String sourcelong ,String destinationlat,String destinationlng) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.rideId = rideId;
        this.rideFlag = rideFlag;
        this.rideownercustomernumber = rideownercustomernumber;
        this.encodedpolyline = encodedpolyline;
        this.sourcelat =sourcelat;
        this.sourcelong = sourcelong;
        this.destinationlat = destinationlat;
        this.destinationlng = destinationlng;
    }
}
