package com.hpe.oo.android.model;

import java.util.UUID;

public class Flow {
    private String  mFlowId;
    private String  mFlowName;
    private String  mFlowPath;
    private String  mFlowType;
    private String  mFlowDescription;

    public Flow() {}

    public String getFlowId() {
        return mFlowId;
    }

    public void setFlowId(String flowId) {
        mFlowId = flowId;
    }

    public String getFlowName() {
        return mFlowName;
    }

    public void setFlowName(String flowName) {
        mFlowName = flowName;
    }

    public String getFlowPath() {
        return mFlowPath;
    }

    public void setFlowPath(String flowPath) {
        mFlowPath = flowPath;
    }

    public String getFlowType() {
        return mFlowType;
    }

    public void setFlowType(String flowType) {
        mFlowType = flowType;
    }

    public String getFlowDescription() {
        return mFlowDescription;
    }

    public void setFlowDescription(String description) {
        mFlowDescription = description;
    }

}
