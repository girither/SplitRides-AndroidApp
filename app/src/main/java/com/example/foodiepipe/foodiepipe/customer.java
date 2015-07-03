package com.example.foodiepipe.foodiepipe;

/**
 * Created by gbm on 6/12/15.
 */
public class customer {
    private String customerName;
    private String customerEmail;

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    private String latLong;

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public customer(String customerName, String customerEmail, String customerPhoneNumber,String latLong) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.latLong = latLong;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private String customerPhoneNumber;
}
