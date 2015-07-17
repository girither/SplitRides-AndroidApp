package com.example.foodiepipe.foodiepipe;

/**
 * Created by gbm on 7/17/15.
 */
public class ratecardobject {
    String price;

    public ratecardobject(String price, String distance, String time) {
        this.price = price;
        this.distance = distance;
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String distance;
    String time;

}
