package com.splitrides;

import java.util.List;

/**
 * Created by gbm on 7/20/15.
 */
public class splitfaredataobject {

    private String fareForTimeSpentInThisLeg;

    public String getFareForThisLeg() {
        return fareForThisLeg;
    }

    public void setFareForThisLeg(String fareForThisLeg) {
        this.fareForThisLeg = fareForThisLeg;
    }

    public List<String> getPartners() {
        return partners;
    }

    public void setPartners(List<String> partners) {
        this.partners = partners;
    }

    public splitfaredataobject(String fareForTimeSpentInThisLeg, List<String> partners, String fareForThisLeg) {
        this.fareForTimeSpentInThisLeg = fareForTimeSpentInThisLeg;
        this.partners = partners;
        this.fareForThisLeg = fareForThisLeg;
    }

    public String getFareForTimeSpentInThisLeg() {
        return fareForTimeSpentInThisLeg;
    }

    public void setFareForTimeSpentInThisLeg(String fareForTimeSpentInThisLeg) {
        this.fareForTimeSpentInThisLeg = fareForTimeSpentInThisLeg;
    }

    private String fareForThisLeg;
    private List<String>  partners;
}
