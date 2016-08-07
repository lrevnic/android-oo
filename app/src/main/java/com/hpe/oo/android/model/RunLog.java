package com.hpe.oo.android.model;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by revnic on 7/23/2016.
 */
public class RunLog {
    private JSONObject  mExecutionSummary;
    private JSONArray   mInputs;
    private JSONObject  mOutputs;

    public JSONObject getExecutionSummary() {
        return mExecutionSummary;
    }

    public void setExecutionSummary(JSONObject executionSummary) {
        mExecutionSummary = executionSummary;
    }

    public JSONArray getInputs() {
        return mInputs;
    }

    public void setInputs(JSONArray inputs) {
        mInputs = inputs;
    }

    public JSONObject getOutputs() {
        return mOutputs;
    }

    public void setOutputs(JSONObject outputs) {
        mOutputs = outputs;
    }

}
