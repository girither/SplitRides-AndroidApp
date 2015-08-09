package com.splitrides;

/**
 * Created by gbm on 6/12/15.
 */
public class customer {
    private String customerName;
    private String customerEmail;

    public String getCustomernumber() {
        return customernumber;
    }

    public void setCustomernumber(String customernumber) {
        this.customernumber = customernumber;
    }

    private String customernumber;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    private String profileId;

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    private String latLong;

    public String getDropLatlong() {
        return dropLatlong;
    }

    public void setDropLatlong(String dropLatlong) {
        this.dropLatlong = dropLatlong;
    }

    private String dropLatlong;

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public customer(String customerName, String customerEmail, String customerPhoneNumber,String latLong,String droplatlong,String profileId,String customernumber) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.latLong = latLong;
        this.dropLatlong = droplatlong;
        this.profileId = profileId;
        this.customernumber = customernumber;
    }

    public customer(String customerName, String customerEmail, String customerPhoneNumber,String latLong,String droplatlong,String profileId, String customernumber, Integer mutualFriendsCount) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.latLong = latLong;
        this.dropLatlong = droplatlong;
        this.profileId = profileId;
        this.customernumber = customernumber;
        this.mutualFriendsCount = mutualFriendsCount;
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

    private Integer mutualFriendsCount;

    public void setMutualFriendsCount(Integer mutualFriendsCount) {
        this.mutualFriendsCount = mutualFriendsCount;
    }

    public Integer getMutualFriendsCount() {
        return mutualFriendsCount;
    }
}
