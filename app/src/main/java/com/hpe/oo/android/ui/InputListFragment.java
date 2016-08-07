package com.hpe.oo.android.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hpe.oo.android.OOConnector;
import com.hpe.oo.android.model.FlowInput;
import com.hpe.oo.android.model.RunLog;
import com.hpe.oo.android.oo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by revnic on 7/30/2016.
 */
public class InputListFragment extends Fragment {
        private static final String  TAG = "InputListFragment";
        private static final String  ARG_EXTRA_RUN_ID = "run_id";

        private List<FlowInput>    mFlowInputs;
        private RecyclerView       mInputRecyclerView;

        public static InputListFragment newInstance(Long runId) {
            Bundle args                         = new Bundle();
            args.putSerializable(ARG_EXTRA_RUN_ID, runId);

            InputListFragment inputListFragment = new InputListFragment();
            inputListFragment.setArguments(args);

            return inputListFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            setHasOptionsMenu(true);

            Long runId = (Long) getArguments().getSerializable(ARG_EXTRA_RUN_ID);
            new FetchFlowInputsTask(runId).execute();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_input_list, container, false);
            mInputRecyclerView = (RecyclerView) view.findViewById(R.id.input_recycler_view);
            mInputRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            updateUI();
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();

            updateUI();
        }

        private void updateUI() {
        }

        private class FlowInputHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public  TextView  mNameTextView;
            public  TextView  mValueTextView;
            private FlowInput mFlowInput;

            public FlowInputHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);

                mNameTextView   = (TextView)   itemView.findViewById(R.id.input_list_item_nameTextView);
                mValueTextView  = (EditText) itemView.findViewById(R.id.input_list_item_valueTextField);
            }

            public void bindInput(FlowInput flowInput){
                mFlowInput = flowInput;

                mNameTextView.setText(mFlowInput.getName());
                mValueTextView.setText(mFlowInput.getValue());
            }

            protected FlowInput getFlowInput() {
                return mFlowInput;
            }

            @Override
            public void onClick(View v) {

            }
        }

        private class InputAdapter extends RecyclerView.Adapter<FlowInputHolder> {
            public InputAdapter(List<FlowInput> flowInputs) {
                mFlowInputs = flowInputs;
            }

            @Override
            public int getItemCount() {
                return mFlowInputs.size();
            }

            @Override
            public FlowInputHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.list_item_input, parent, false);
                return new FlowInputHolder(view);
            }

            @Override
            public void onBindViewHolder(FlowInputHolder holder, int position) {
                FlowInput flowInput = mFlowInputs.get(position);
                holder.bindInput(flowInput);
            }
        }

        class FetchFlowInputsTask extends AsyncTask<Void, Void, List<FlowInput>> {
            private OOConnector        mOOConnector;
            private Long               mRunId;

            public FetchFlowInputsTask(Long runId) {
                mRunId = runId;
                mOOConnector = OOConnector.newInstance();
            }

            @Override
            protected List<FlowInput> doInBackground(Void... params) {
                List<FlowInput> myFlowInputs = new ArrayList<>();

                RunLog execLog = mOOConnector.getRunExecutionLog(mRunId);
                JSONArray inputs = execLog.getInputs();
                for (int i = 0; i < inputs.length(); ++i) {
                    FlowInput myFlowInput = new FlowInput();
                    try {
                        JSONObject jsonObject = (JSONObject) inputs.get(i);
                        myFlowInput.setName(jsonObject.getString("name"));
                        myFlowInput.setValue(jsonObject.getString("value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    myFlowInputs.add(myFlowInput);
                }

                return myFlowInputs;
            }

            @Override
            protected void onPostExecute(List<FlowInput> inputs) {
                Log.i(TAG, "Received " + inputs.size() + " inputs");
                mFlowInputs = inputs;
                setupAdapter();
            }
        }

        private void setupAdapter() {
            if (isAdded()) {
                mInputRecyclerView.setAdapter(new InputAdapter(mFlowInputs));
            }
        }
    }
