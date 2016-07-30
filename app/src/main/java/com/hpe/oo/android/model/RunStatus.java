package com.hpe.oo.android.model;

/**
 * Created by revnic on 7/8/2016.
 */
public enum RunStatus {
    RUNNING("RUNNING"),
    COMPLETED("COMPLETED"),
    SYSTEM_FAILURE("SYSTEM_FAILURE"),
    PAUSED("PAUSED"),
    PENDIND_PAUSE("PENDING_PAUSE"),
    CANCELED("CANCELED"),
    PENDING_CANCEL("PENDING_CANCEL"),
    ERROR("ERROR");

    private final String name;

    private RunStatus(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}