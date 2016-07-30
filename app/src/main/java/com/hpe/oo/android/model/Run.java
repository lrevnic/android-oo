package com.hpe.oo.android.model;

import org.json.JSONArray;

public class Run {
    private Long      mId;
    private String    mName;
    private String    mRunStatus;
    private String    mResultStatusType;
    private Long      mRunStartTime;
    private Long      mRunStopTime;
    private String    mUser;

    public Run() {
        mRunStartTime = Long.valueOf(0);
    }

    public Run(Long id) {
        this();
        mId = id;
    }

    public String getResultStatusType() {
        return mResultStatusType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getRunStatus() {
        return mRunStatus;
    }

    public void setRunStatus(String runStatus) {
        mRunStatus = runStatus;
    }

    public Long getRunStartTime() {
        return mRunStartTime;
    }

    public void setRunStartTime(Long runStartTime) {
        mRunStartTime = runStartTime;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }


    @Override
    public String toString() {
        return mName;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public Long getDuration() {
        return mRunStopTime - mRunStartTime;
    }

    public void setRunStopTime(Long runStopTime) {
        mRunStopTime = runStopTime;
    }

    public void setResultStatusType(String resultStatusType) {
        mResultStatusType = resultStatusType;
    }
}
