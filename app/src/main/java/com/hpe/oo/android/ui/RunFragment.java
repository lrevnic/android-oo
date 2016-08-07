package com.hpe.oo.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hpe.oo.android.OOConnector;
import com.hpe.oo.android.model.Run;
import com.hpe.oo.android.model.RunExecutionLog;
import com.hpe.oo.android.oo.R;

public class RunFragment extends Fragment {
    private static final String  TAG = "RunFragment";

    private Run                 mRun;
    private RunExecutionLog     mRunExecutionLog;
    private OOConnector mOOConnector;

    private EditText            mRunNameEditText;

    public static RunFragment newInstance(Long runId) {
        Bundle args = new Bundle();
        args.putSerializable(RunPagerActivity.EXTRA_RUN_ID, runId);

        RunFragment fragment = new RunFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOOConnector = OOConnector.newInstance();

        Long id = (Long) getArguments().getSerializable(RunPagerActivity.EXTRA_RUN_ID);
        mRun = mOOConnector.getRun(id);
        new FetchRunExecutionLogTask(id).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, parent, false);
        mRunNameEditText = (EditText) v.findViewById(R.id.run_name_text);
        mRunNameEditText.setText(mRun.getName());
        return v;
    }

    private void updateUI() {
        if (mRunExecutionLog != null) {
            RelativeLayout runLayout = (RelativeLayout) getActivity().findViewById(R.id.run_relative_layout);

            int prevTextViewId = 0;
            for(int i = 0; i < 10; i++)
            {
                final TextView textView = new TextView(this.getActivity());
                textView.setText("Text " + i);
                Log.d(TAG, "Adding TextView " + i);

                int curTextViewId = prevTextViewId + 1;
                textView.setId(curTextViewId);

                final RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                if (i != 0) {
                    params.addRule(RelativeLayout.BELOW, prevTextViewId);
                } else {
                    params.addRule(RelativeLayout.BELOW, mRunNameEditText.getId());
                }
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                textView.setLayoutParams(params);
                prevTextViewId = curTextViewId;
                runLayout.addView(textView, params);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    class FetchRunExecutionLogTask extends AsyncTask<Void, Void, RunExecutionLog> {
        private Long mRunId;

        public FetchRunExecutionLogTask(Long runId) {
            mRunId = runId;
        }

        @Override
        protected RunExecutionLog doInBackground(Void... params) {
            return mOOConnector.getRunExecutionLog(mRunId);
        }

        @Override
        protected void onPostExecute(RunExecutionLog runExecutionLog) {
            Log.i(TAG, "Received " + runExecutionLog.toString());
            mRunExecutionLog = runExecutionLog;
            updateUI();
        }
    }

}
