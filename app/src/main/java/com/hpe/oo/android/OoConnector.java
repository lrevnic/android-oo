package com.hpe.oo.android;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;

import com.hpe.oo.android.model.Flow;
import com.hpe.oo.android.model.Run;
import com.hpe.oo.android.model.RunExecutionLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OoConnector {
    public  static final    String TAG                    = "OoConnector";

    private LruCache<String, Flow>  mFlowsCache;
    private LruCache<Long, Run>     mRunsCache;
    private static final    int     CACHE_SIZE              = 2 * 1024 * 1024; //2 MiB

    private static          OoConnector                     sOoConnector;

    private                 String  mUrl;
    private                 String  mUsername;
    private                 String  mPassword;


    private OoConnector() {
    }

    private OoConnector(String endpoint, String username, String password) {
        mUrl      = endpoint;
        mUsername = username;
        mPassword = password;
    }


    public static OoConnector newInstance() {
        if (sOoConnector == null) {
            sOoConnector = new OoConnector();
        }

        return sOoConnector;
    }

    private final class REST {
            private static final    String BASE_PATH          = "rest/v2";
            private static final    String AUTH               = "Basic";
            private static final    String VERSION_RESOURCE   = "version";
            private static final    String RUNS_RESOURCE      = "executions";
            private static final    String FLOWS_RESOURCE     = "flows/library";

            private static final    String RUN_NAME           = "runName";
            private static final    String RUN_LOG            = "execution-log";

            private static final    String SEPARATOR          = "/";
    }

    private void addAuthHeader(HttpURLConnection urlConnection) {
        String userCredentials = mUsername + ":" + mPassword;
        String basicAuth = REST.AUTH + " " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
        urlConnection.setRequestProperty ("Authorization", basicAuth);
    }


    public boolean login(String endpoint, String username, String password) {
        try {
            sOoConnector = new OoConnector(endpoint, username, password);

            URL url = new URL(endpoint + REST.SEPARATOR + REST.BASE_PATH + REST.SEPARATOR + REST.VERSION_RESOURCE);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String userCredentials = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.d("JSON Parser", "result: " + result.toString());
                return true;

            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private String buildUrlSearch(String resource, String query) {
        Uri.Builder uriBuilder = Uri.parse(mUrl + REST.SEPARATOR + REST.BASE_PATH).buildUpon().appendEncodedPath(resource);

        if (resource.equalsIgnoreCase(REST.RUNS_RESOURCE)) {
            if (query != null && !query.isEmpty()) {
                uriBuilder.appendQueryParameter(REST.RUN_NAME, query);
            }
        }

        return uriBuilder.toString();
    }

    private String buildUrlLog(String resource, Long id) {
        Uri.Builder uriBuilder = Uri.parse(mUrl + REST.SEPARATOR + REST.BASE_PATH).buildUpon().appendEncodedPath(resource);

        if (resource.equalsIgnoreCase(REST.RUNS_RESOURCE)) {
            if (id != null ) {
                uriBuilder.appendEncodedPath(Long.toString(id));
                uriBuilder.appendEncodedPath(REST.RUN_LOG);
            }
        }

        return uriBuilder.toString();
    }

    public List<Run> searchRuns(String query) {
        String url = buildUrlSearch(REST.RUNS_RESOURCE, query);
        return getRuns(url);
    }

    public List<Run> getAllRuns() {
        String url = buildUrlSearch(REST.RUNS_RESOURCE, "");
        List<Run> runs;
        if (mRunsCache == null) {
            mRunsCache = new LruCache<Long, Run>(CACHE_SIZE) {};

            runs = getRuns(url);
            for (Run run:runs) {
                mRunsCache.put(run.getId(), run);
            }
        } else {
            runs = new ArrayList<>();
            Map<Long, Run> runMap = mRunsCache.snapshot();
            for (Map.Entry<Long, Run> runEntry:runMap.entrySet()) {
                runs.add(runEntry.getValue());
            }
        }

        return runs;
    }

    public Run getRun(Long id) {
        Run run = null;

        if (mRunsCache != null) {
            run = mRunsCache.get(id);
        } else {
            //TODO: execute REST call to refresh the cache
        }

        return run;
    }

    public RunExecutionLog getRunExecutionLog(Long id) {
        JSONObject jsonObj = fetchObject(buildUrlLog(REST.RUNS_RESOURCE, id));

        RunExecutionLog executionLog = new RunExecutionLog();

        try {
            executionLog.setExecutionSummary(jsonObj.getJSONObject("executionSummary"));
            executionLog.setInputs(jsonObj.getJSONArray("flowVars"));
            executionLog.setOutputs(jsonObj.getJSONObject("flowOutput"));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse RunsExecutionLog JSON", e);
        }

        return executionLog;
    }

    public List<Run> getRuns(String strUrl) {
        List<Run> runs = new ArrayList<>();

        try {
            JSONArray jsonArray = fetchArray(strUrl);
            Log.i(TAG, jsonArray.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject runJsonObject = jsonArray.getJSONObject(i);

                Run run = new Run();
                run.setId(Long.parseLong(runJsonObject.getString("executionId")));
                run.setName(runJsonObject.getString("executionName"));
                run.setRunStatus(runJsonObject.getString("status"));
                run.setResultStatusType(runJsonObject.getString("resultStatusType"));
                run.setRunStartTime(runJsonObject.getLong("startTime"));
                run.setRunStopTime(runJsonObject.getLong("endTime"));
                runs.add(run);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse Runs JSON", e);
        }

        return runs;
    }


    public List<Flow> searchFlows(String query) {
        return getFlowsFromCache(query);
    }

    private List<Flow> getFlowsFromCache(String query) {
        List<Flow> flows = new ArrayList<Flow>();

        Map<String, Flow> snapshot = mFlowsCache.snapshot();
        for (String id : snapshot.keySet()) {
            Flow myFlow = mFlowsCache.get(id);
            if (myFlow.getFlowName().contains(query) ||
                    myFlow.getFlowDescription().contains(query)) {
                flows.add(myFlow);
            }
        }

        return flows;
    }

    public List<Flow> getAllFlows() {
        mFlowsCache = new LruCache<String, Flow>(CACHE_SIZE) {};
        String url = buildUrlSearch(REST.FLOWS_RESOURCE, "");
        List<Flow> flows = getFlows(url);
        for (Flow flow:flows) {
            mFlowsCache.put(flow.getFlowName(), flow);
        }
        return flows;
    }

    public List<Flow> getFlows(String strUrl) {
        List<Flow> flows = new ArrayList<>();

        try {
            JSONArray jsonArray = fetchArray(strUrl);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject flowsJsonObject = jsonArray.getJSONObject(i);

                //populate just runnable elements (exclude folders, ops etc)
                if ( !flowsJsonObject.getBoolean("runnable") ) {
                    continue;
                }

                //exclude deprecated flows
                String path = flowsJsonObject.getString("path");
                if ( path.toLowerCase().contains("deprecated") ) {
                    continue;
                }

                Flow flow = new Flow();
                flow.setFlowId(flowsJsonObject.getString("id"));
                flow.setFlowName(flowsJsonObject.getString("name"));
                flow.setFlowPath(flowsJsonObject.getString("path"));
                flow.setFlowDescription(flowsJsonObject.getString("path"));

                flows.add(flow);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }

        return flows;
    }

    private HttpURLConnection openConnection(String strUrl) {
        HttpURLConnection connection= null;

        try {
            URL url = new URL(strUrl);
            connection = (HttpURLConnection)url.openConnection();
            addAuthHeader(connection);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to connect", ioe);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return connection;
    }

    private InputStream getInputStream(HttpURLConnection connection) {
        InputStream in = null;

        try {
            in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get input stream from connection");
            connection.disconnect();
        }
        return in;
    }

    private JSONArray fetchArray(String strUrl) {
        HttpURLConnection connection = openConnection(strUrl);
        Scanner scanner = new Scanner(getInputStream(connection));
        String jsonString = scanner.useDelimiter("\\A").next();
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        if (jsonArray != null) {
            Log.i(TAG, jsonArray.toString());
        }

        return jsonArray;
    }

    private JSONObject fetchObject(String strUrl) {
        HttpURLConnection connection = openConnection(strUrl);
        Scanner scanner = new Scanner(getInputStream(connection));
        String jsonString = scanner.useDelimiter("\\A").next();
        JSONObject jsonObj = null;

        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        if (jsonObj != null) {
            Log.i(TAG, jsonObj.toString());
        }

        return jsonObj;
    }
}
