package com.hpe.oo.android.model;

/**
 * Created by revnic on 7/30/2016.
 */
public class FlowInput {
    private String mName;
    private String mLabel;
    private String mValue;

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }


}
